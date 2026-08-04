package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.entity.Proyecto;
import ec.edu.uteq.svpc.entity.ProyectoDocente;
import ec.edu.uteq.svpc.entity.ProyectoDocenteId;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import ec.edu.uteq.svpc.repository.ProyectoDocenteRepository;
import ec.edu.uteq.svpc.repository.ProyectoRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ImportacionProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final ProyectoDocenteRepository proyectoDocenteRepository;
    private final DocenteRepository docenteRepository;

    public ImportacionProyectoService(

            ProyectoRepository proyectoRepository,
            ProyectoDocenteRepository proyectoDocenteRepository,
            DocenteRepository docenteRepository) {

        this.proyectoRepository = proyectoRepository;
        this.proyectoDocenteRepository = proyectoDocenteRepository;
        this.docenteRepository = docenteRepository;
    }

    public String importar(
            MultipartFile archivo,
            Integer idProceso) throws Exception {

        Workbook workbook = WorkbookFactory.create(archivo.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        EstadisticasImportacionProyecto estadisticas = new EstadisticasImportacionProyecto();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row fila = sheet.getRow(i);

            if (fila == null) {
                continue;
            }

            try {

                procesarFila(fila, idProceso, estadisticas);

            } catch (Exception e) {

                estadisticas.filaOmitida();

            }
        }

        workbook.close();

        return estadisticas.generarResumen();
    }

    private void procesarFila(
            Row fila,
            Integer idProceso,
            EstadisticasImportacionProyecto estadisticas) {

        Proyecto proyecto = obtenerOCrearProyecto(fila, idProceso, estadisticas);

        Docente director = obtenerOCrearDirector(fila, estadisticas);

        guardarRelacion(
                proyecto.getIdProyecto(),
                director.getIdDocente(),
                "DIRECTOR",
                estadisticas);

        obtenerOCrearInvestigadores(
                fila,
                proyecto.getIdProyecto(),
                estadisticas);

    }

    private Proyecto obtenerOCrearProyecto(
            Row fila,
            Integer idProceso,
            EstadisticasImportacionProyecto estadisticas) {

        String nombre = obtenerTexto(fila.getCell(1));

        Proyecto proyecto = proyectoRepository
                .findByIdProcesoAndNombre(idProceso, nombre)
                .orElse(new Proyecto());

        boolean nuevo = proyecto.getIdProyecto() == null;

        proyecto.setIdProceso(idProceso);
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(obtenerTexto(fila.getCell(22)));
        proyecto.setPeriodo(obtenerTexto(fila.getCell(20)));
        proyecto.setTipoFinanciamiento("INTERNO");
        proyecto.setEstado("APROBADO");

        Proyecto guardado = proyectoRepository.save(proyecto);

        if (nuevo) {
            estadisticas.proyectoInsertado();
        } else {
            estadisticas.proyectoActualizado();
        }

        return guardado;
    }

    private Docente obtenerOCrearDirector(
            Row fila,
            EstadisticasImportacionProyecto estadisticas) {

        String cedula = obtenerTexto(fila.getCell(9));

        Docente docente = docenteRepository
                .findByCedula(cedula)
                .orElse(new Docente());

        boolean nuevo = docente.getIdDocente() == null;

        String nombreCompleto = obtenerTexto(fila.getCell(8));

        String[] partes = separarNombre(nombreCompleto);

        docente.setCedula(cedula);
        docente.setApellidos(partes[0]);
        docente.setNombres(partes[1]);
        docente.setCorreo(obtenerTexto(fila.getCell(11)));
        docente.setFacultad(obtenerTexto(fila.getCell(13)));
        docente.setCarrera(obtenerTexto(fila.getCell(14)));

        Docente guardado = docenteRepository.save(docente);

        if (nuevo) {
            estadisticas.docenteInsertado();
        }

        return guardado;
    }

    private Docente obtenerOCrearInvestigador(
            String nombreCompleto,
            EstadisticasImportacionProyecto estadisticas) {

        String[] partes = separarNombre(nombreCompleto);

        String apellidos = partes[0];
        String nombres = partes[1];

        return docenteRepository
                .findByNombresContainingIgnoreCaseAndApellidosContainingIgnoreCase(
                        nombres,
                        apellidos)
                .stream()
                .findFirst()
                .orElseGet(() -> {

                    Docente docente = new Docente();

                    docente.setCedula("");
                    docente.setNombres(nombres);
                    docente.setApellidos(apellidos);

                    estadisticas.docenteInsertado();

                    return docenteRepository.save(docente);

                });
    }

    private String[] separarNombre(String nombreCompleto) {

        nombreCompleto = nombreCompleto.trim().replaceAll("\\s+", " ");

        String[] palabras = nombreCompleto.split(" ");

        if (palabras.length == 1) {
            return new String[] { "", palabras[0] };
        }

        if (palabras.length == 2) {
            return new String[] { palabras[0], palabras[1] };
        }

        if (palabras.length == 3) {

            return new String[] {
                    palabras[0],
                    palabras[1] + " " + palabras[2]
            };

        }

        int mitad = palabras.length / 2;

        StringBuilder apellidos = new StringBuilder();
        StringBuilder nombres = new StringBuilder();

        for (int i = 0; i < mitad; i++) {

            if (i > 0) {
                apellidos.append(" ");
            }

            apellidos.append(palabras[i]);
        }

        for (int i = mitad; i < palabras.length; i++) {

            if (i > mitad) {
                nombres.append(" ");
            }

            nombres.append(palabras[i]);
        }

        return new String[] {
                apellidos.toString(),
                nombres.toString()
        };

    }

    private void guardarRelacion(
            Integer idProyecto,
            Long idDocente,
            String rol,
            EstadisticasImportacionProyecto estadisticas) {

        ProyectoDocenteId id = new ProyectoDocenteId(
                idProyecto,
                idDocente);

        if (proyectoDocenteRepository.existsById(id)) {
            return;
        }

        ProyectoDocente relacion = new ProyectoDocente();

        relacion.setId(id);
        relacion.setRolParticipante(rol);
        relacion.setPuntajeObtenido(0.0);

        proyectoDocenteRepository.save(relacion);

        estadisticas.relacionGuardada();
    }

    private String obtenerTexto(Cell cell) {

        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();

        return formatter.formatCellValue(cell).trim();

    }

    private void obtenerOCrearInvestigadores(
            Row fila,
            Integer idProyecto,
            EstadisticasImportacionProyecto estadisticas) {

        String investigadores = obtenerTexto(fila.getCell(12));

        if (investigadores.isBlank()) {
            return;
        }

        String[] lista = investigadores.split(",");

        for (String nombreCompleto : lista) {

            nombreCompleto = nombreCompleto.trim();

            if (nombreCompleto.isBlank()) {
                continue;
            }

            Docente docente = obtenerOCrearInvestigador(
                    nombreCompleto,
                    estadisticas);

            guardarRelacion(
                    idProyecto,
                    docente.getIdDocente(),
                    "INTEGRANTE",
                    estadisticas);
        }
    }

}