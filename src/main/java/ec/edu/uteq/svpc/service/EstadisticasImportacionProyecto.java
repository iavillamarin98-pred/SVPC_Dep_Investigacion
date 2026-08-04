package ec.edu.uteq.svpc.service;

public class EstadisticasImportacionProyecto {

    private int proyectosInsertados;
    private int proyectosActualizados;
    private int docentesInsertados;
    private int relacionesGuardadas;
    private int filasOmitidas;

    public void proyectoInsertado() {
        proyectosInsertados++;
    }

    public void proyectoActualizado() {
        proyectosActualizados++;
    }

    public void docenteInsertado() {
        docentesInsertados++;
    }

    public void relacionGuardada() {
        relacionesGuardadas++;
    }

    public void filaOmitida() {
        filasOmitidas++;
    }

    public String generarResumen() {

        return """
                Importación finalizada.

                Proyectos insertados: %d

                Proyectos actualizados: %d

                Docentes insertados: %d

                Relaciones guardadas: %d

                Filas omitidas: %d
                """.formatted(
                proyectosInsertados,
                proyectosActualizados,
                docentesInsertados,
                relacionesGuardadas,
                filasOmitidas);
    }
}