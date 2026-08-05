package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.entity.Libro;
import ec.edu.uteq.svpc.entity.LibroDocente;
import ec.edu.uteq.svpc.entity.LibroDocenteId;
import ec.edu.uteq.svpc.repository.CapituloLibroRepository;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import ec.edu.uteq.svpc.repository.LibroDocenteRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ImportacionCapituloLibroService {

    private final CapituloLibroRepository capituloLibroRepository;
    private final LibroDocenteRepository libroDocenteRepository;
    private final DocenteRepository docenteRepository;

    public ImportacionCapituloLibroService(
            CapituloLibroRepository capituloLibroRepository,
            LibroDocenteRepository libroDocenteRepository,
            DocenteRepository docenteRepository) {
        this.capituloLibroRepository = capituloLibroRepository;
        this.libroDocenteRepository = libroDocenteRepository;
        this.docenteRepository = docenteRepository;
    }

    @Transactional
    public String importarCapitulosLibro(MultipartFile archivo, Integer idProceso) {

        int capitulosInsertados = 0;
        int capitulosActualizados = 0;
        int docentesInsertados = 0;
        int docentesActualizados = 0;

        int relacionesGuardadas = 0;
        int relacionesActualizadas = 0;
        int filasOmitidas = 0;

        try (InputStream inputStream = archivo.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet hoja = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    filasOmitidas++;
                    continue;
                }

                String tipoExcel = obtenerTexto(fila, 0, formatter);

                String codigo = obtenerTexto(fila, 1, formatter);
                String titulo = obtenerTexto(fila, 2, formatter);
                String isbn = obtenerTexto(fila, 3, formatter);

                String editorial = obtenerTexto(fila, 13, formatter); // Editor compilador
                String periodo = obtenerTexto(fila, 5, formatter); // Fecha publicación
                String estado = obtenerTexto(fila, 9, formatter);

                // =====================================================
                // DATOS DEL DOCENTE
                // =====================================================

                String cedula = obtenerTexto(fila, 10, formatter);

                String rolParticipante = obtenerTexto(fila, 11, formatter);

                String participante = obtenerTexto(fila, 12, formatter);

                // El Excel no separa nombres y apellidos
                String nombres = participante;
                String apellidos = "";

                // Estas columnas no existen en el Excel
                String correo = "";
                String facultad = "";
                String carrera = "";

                if (titulo.isBlank() || cedula.isBlank()) {
                    filasOmitidas++;
                    continue;
                }

                String tipo = "CAPITULO DE LIBRO";
                String rolNormalizado = normalizarRol(rolParticipante);

                Libro capitulo;

                Optional<Libro> capituloExistente = capituloLibroRepository.buscarCapituloPorProcesoYCodigo(
                        idProceso,
                        codigo);

                if (capituloExistente.isPresent()) {

                    capitulo = capituloExistente.get();

                    capitulo.setTitulo(titulo);
                    capitulo.setTipo(tipo);
                    capitulo.setIsbn(isbn);
                    capitulo.setEditorial(editorial);
                    capitulo.setPeriodo(periodo);
                    capitulo.setEstado(estado);

                    capitulosActualizados++;

                } else {

                    capitulo = new Libro();

                    capitulo.setIdProceso(idProceso);
                    capitulo.setCodigo(codigo);
                    capitulo.setTitulo(titulo);
                    capitulo.setTipo(tipo);
                    capitulo.setIsbn(isbn);
                    capitulo.setEditorial(editorial);
                    capitulo.setPeriodo(periodo);
                    capitulo.setEstado(estado);

                    capitulosInsertados++;
                }

                System.out.println("=================================");
                System.out.println("codigo (" + codigo.length() + "): " + codigo);
                System.out.println("titulo (" + titulo.length() + ")");
                System.out.println("tipo (" + tipo.length() + "): " + tipo);
                System.out.println("isbn (" + isbn.length() + "): " + isbn);
                System.out.println("editorial (" + editorial.length() + ")");
                System.out.println("periodo (" + periodo.length() + "): " + periodo);
                System.out.println("estado (" + estado.length() + "): " + estado);
                System.out.println("=================================");

                capitulo = capituloLibroRepository.save(capitulo);

                Docente docente;

                Optional<Docente> docenteExistente = docenteRepository.findByCedula(cedula);

                if (docenteExistente.isPresent()) {

                    docente = docenteExistente.get();

                    docente.setNombres(nombres);
                    docente.setApellidos(apellidos);
                    docente.setCorreo(correo);
                    docente.setFacultad(facultad);
                    docente.setCarrera(carrera);

                    docentesActualizados++;

                } else {

                    docente = new Docente();

                    docente.setCedula(cedula);
                    docente.setNombres(nombres);
                    docente.setApellidos(apellidos);
                    docente.setCorreo(correo);
                    docente.setFacultad(facultad);
                    docente.setCarrera(carrera);
                    docente.setEstado(true);

                    docentesInsertados++;
                }

                docente = docenteRepository.save(docente);

                LibroDocenteId idRelacion = new LibroDocenteId(
                        capitulo.getIdLibro(),
                        docente.getIdDocente());

                Optional<LibroDocente> relacionExistente = libroDocenteRepository.findById(idRelacion);

                if (relacionExistente.isPresent()) {

                    LibroDocente relacion = relacionExistente.get();

                    relacion.setRolParticipante(rolNormalizado);
                    relacion.setPuntajeObtenido(BigDecimal.ZERO);

                    libroDocenteRepository.save(relacion);

                    relacionesActualizadas++;

                } else {

                    LibroDocente relacion = new LibroDocente();

                    relacion.setId(idRelacion);
                    relacion.setLibro(capitulo);
                    relacion.setDocente(docente);
                    relacion.setRolParticipante(rolNormalizado);
                    relacion.setPuntajeObtenido(BigDecimal.ZERO);

                    libroDocenteRepository.save(relacion);

                    relacionesGuardadas++;
                }
            }

            return "Importación de capítulos de libro finalizada. " +
                    "Capítulos insertados: " + capitulosInsertados +
                    ", capítulos actualizados: " + capitulosActualizados +
                    ", docentes insertados: " + docentesInsertados +
                    ", docentes actualizados: " + docentesActualizados +
                    ", relaciones guardadas: " + relacionesGuardadas +
                    ", relaciones actualizadas: " + relacionesActualizadas +
                    ", filas omitidas: " + filasOmitidas + ".";

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al importar capítulos de libro: " + e.getMessage(),
                    e);
        }
    }

    private String obtenerTexto(Row fila, int indice, DataFormatter formatter) {
        Cell celda = fila.getCell(indice);

        if (celda == null) {
            return "";
        }

        return formatter.formatCellValue(celda).trim();
    }

    private String normalizarRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return "COAUTOR";
        }

        String valor = rol.trim().toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U");

        if (valor.equals("AUTOR")) {
            return "AUTOR";
        }

        return "COAUTOR";
    }
}