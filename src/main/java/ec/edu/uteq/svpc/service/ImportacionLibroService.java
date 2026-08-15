package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.entity.Libro;
import ec.edu.uteq.svpc.entity.LibroDocente;
import ec.edu.uteq.svpc.entity.LibroDocenteId;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import ec.edu.uteq.svpc.repository.LibroDocenteRepository;
import ec.edu.uteq.svpc.repository.LibroRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ImportacionLibroService {

    private final LibroRepository libroRepository;
    private final LibroDocenteRepository libroDocenteRepository;
    private final DocenteRepository docenteRepository;

    public ImportacionLibroService(
            LibroRepository libroRepository,
            LibroDocenteRepository libroDocenteRepository,
            DocenteRepository docenteRepository) {
        this.libroRepository = libroRepository;
        this.libroDocenteRepository = libroDocenteRepository;
        this.docenteRepository = docenteRepository;
    }

    @Transactional
    public String importarLibros(MultipartFile archivo, Integer idProceso) {

        int librosInsertados = 0;
        int librosActualizados = 0;
        int docentesInsertados = 0;
        int docentesActualizados = 0;
        int relacionesGuardadas = 0;
        int relacionesActualizadas = 0;
        int filasOmitidas = 0;

        try (
                InputStream inputStream = archivo.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 3; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    filasOmitidas++;
                    continue;
                }

                String tipo = normalizarTipoLibro(valor(row, 0));
                String codigo = limpiar(valor(row, 1));
                String titulo = limpiar(valor(row, 2));
                String isbn = limpiar(valor(row, 3));
                String estado = limpiar(valor(row, 10));
                String cedula = limpiar(valor(row, 11));
                String rolParticipante = normalizarRol(valor(row, 12));
                String participante = limpiar(valor(row, 13));
                String periodo = limpiar(valor(row, 14));

                if (codigo.isBlank()
                        || titulo.isBlank()
                        || cedula.isBlank()
                        || participante.isBlank()
                        || rolParticipante.isBlank()) {
                    filasOmitidas++;
                    continue;
                }

                boolean libroYaExiste = libroRepository
                        .findByIdProcesoAndCodigo(idProceso, codigo)
                        .isPresent();

                Libro libroGuardado = guardarLibro(
                        idProceso,
                        codigo,
                        titulo,
                        tipo,
                        isbn,
                        periodo,
                        estado);

                if (libroYaExiste) {
                    librosActualizados++;
                } else {
                    librosInsertados++;
                }
                /*------------------------------ */
                Optional<Docente> docenteExistente = docenteRepository.findByCedula(cedula);

                Docente docenteGuardado;

                if (docenteExistente.isPresent()) {

                    docenteGuardado = docenteExistente.get();
                    docentesActualizados++;

                } else {

                    docenteGuardado = guardarDocente(
                            cedula,
                            participante);

                    docentesInsertados++;
                }

                LibroDocenteId idRelacion = new LibroDocenteId(
                        libroGuardado.getIdLibro(),
                        docenteGuardado.getIdDocente());

                boolean existeRelacion = libroDocenteRepository.existsById(idRelacion);

                LibroDocente relacion;

                if (existeRelacion) {
                    relacion = libroDocenteRepository.findById(idRelacion).orElseThrow();
                } else {
                    relacion = new LibroDocente();
                    relacion.setId(idRelacion);
                    relacion.setPuntajeObtenido(BigDecimal.ZERO);
                }

                relacion.setLibro(libroGuardado);
                relacion.setDocente(docenteGuardado);
                relacion.setRolParticipante(rolParticipante);

                // ===== PRUEBA =====
                LibroDocente guardado = libroDocenteRepository.save(relacion);
                libroDocenteRepository.flush();

                System.out.println("=================================");
                System.out.println("Libro: " + guardado.getId().getIdLibro());
                System.out.println("Docente: " + guardado.getId().getIdDocente());

                boolean existe = libroDocenteRepository.existsById(idRelacion);

                System.out.println("¿Existe en BD?: " + existe);
                // ==================

                if (existeRelacion) {
                    relacionesActualizadas++;
                } else {
                    relacionesGuardadas++;
                }
            }

            return "Importación de libros finalizada. " +
                    "Libros insertados: " + librosInsertados +
                    ", libros actualizados: " + librosActualizados +
                    ", docentes insertados: " + docentesInsertados +
                    ", docentes actualizados: " + docentesActualizados +
                    ", relaciones guardadas: " + relacionesGuardadas +
                    ", relaciones actualizadas: " + relacionesActualizadas +
                    ", filas omitidas: " + filasOmitidas;

        } catch (Exception e) {
            throw new RuntimeException("Error al importar libros: " + e.getMessage(), e);
        }
    }

    private Libro guardarLibro(
            Integer idProceso,
            String codigo,
            String titulo,
            String tipo,
            String isbn,
            String periodo,
            String estado) {

        Optional<Libro> libroOpt = libroRepository.findByIdProcesoAndCodigo(idProceso, codigo);

        Libro libro;

        if (libroOpt.isPresent()) {
            libro = libroOpt.get();
        } else {
            libro = new Libro();
            libro.setIdProceso(idProceso);
            libro.setCodigo(codigo);
        }

        libro.setTitulo(titulo);
        libro.setTipo(tipo);
        libro.setIsbn(isbn);
        libro.setEditorial(null);
        libro.setPeriodo(periodo);
        libro.setEstado(estado);

        return libroRepository.save(libro);
    }

    /* -- -------------------------------------------- */
    private Docente guardarDocente(
            String cedula,
            String participante) {

        Optional<Docente> docenteOpt = docenteRepository.findByCedula(cedula);

        /*
         * ==================================================
         * DOCENTE YA EXISTENTE
         * ==================================================
         *
         * Si el docente ya existe en Gestión de Docentes,
         * NO se modifica absolutamente ningún dato maestro.
         *
         * La importación de libros solamente necesita
         * obtener el docente para crear la relación
         * LIBRO_DOCENTE.
         */
        if (docenteOpt.isPresent()) {

            return docenteOpt.get();
        }

        /*
         * ==================================================
         * DOCENTE NUEVO
         * ==================================================
         *
         * Si por alguna razón el docente no fue cargado
         * previamente en Gestión de Docentes, se crea un
         * registro mínimo.
         */
        Docente docente = new Docente();

        docente.setCedula(cedula);

        String[] nombresApellidos = separarParticipante(participante);

        docente.setApellidos(
                nombresApellidos[0]);

        docente.setNombres(
                nombresApellidos[1]);

        /*
         * Estos datos no deben obtenerse de la importación
         * para un docente que ya exista.
         *
         * Para uno nuevo se dejan vacíos/null porque la
         * información oficial debe administrarse desde
         * Gestión de Docentes.
         */
        docente.setCorreo(null);
        docente.setFacultad(null);
        docente.setCarrera(null);
        docente.setEstado(true);

        return docenteRepository.save(docente);
    }
    /*------------------------------------------------ */

    private String valor(Row row, int index) {

        Cell cell = row.getCell(index);

        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private String limpiar(String texto) {

        if (texto == null) {
            return "";
        }

        return texto.trim().replaceAll("\\s+", " ");
    }

    private String normalizarRol(String rol) {

        String r = limpiar(rol).toUpperCase();

        if (r.contains("COAUTOR")) {
            return "COAUTOR";
        }

        if (r.contains("AUTOR")) {
            return "AUTOR";
        }

        return r;
    }

    private String normalizarTipoLibro(String tipo) {

        String t = limpiar(tipo).toUpperCase();

        if (t.contains("CAP")) {
            return "CAPITULO DE LIBRO";
        }

        return "LIBRO";
    }

    private String[] separarParticipante(String participante) {

        String limpio = limpiar(participante).toUpperCase();
        String[] partes = limpio.split(" ");

        if (partes.length >= 4) {

            String apellidos = partes[0] + " " + partes[1];

            StringBuilder nombres = new StringBuilder();

            for (int i = 2; i < partes.length; i++) {
                nombres.append(partes[i]).append(" ");
            }

            return new String[] {
                    apellidos.trim(),
                    nombres.toString().trim()
            };
        }

        if (partes.length == 3) {
            return new String[] {
                    partes[0] + " " + partes[1],
                    partes[2]
            };
        }

        if (partes.length == 2) {
            return new String[] {
                    partes[0],
                    partes[1]
            };
        }

        return new String[] {
                limpio,
                "SIN NOMBRES"
        };
    }
}