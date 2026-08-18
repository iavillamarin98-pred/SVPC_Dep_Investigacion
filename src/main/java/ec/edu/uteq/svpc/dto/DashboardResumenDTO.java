package ec.edu.uteq.svpc.dto;

import java.math.BigDecimal;

public class DashboardResumenDTO {

    private long articulos;
    private long proceedings;
    private long libros;
    private long capitulos;
    private long proyectos;

    // Puntaje total de bonificaciones
    private BigDecimal bonificaciones;

    private long docentesEvaluados;
    private BigDecimal puntajeTotal;

    public DashboardResumenDTO() {
    }

    public DashboardResumenDTO(
            long articulos,
            long proceedings,
            long libros,
            long capitulos,
            long proyectos,
            BigDecimal bonificaciones,
            long docentesEvaluados,
            BigDecimal puntajeTotal) {

        this.articulos = articulos;
        this.proceedings = proceedings;
        this.libros = libros;
        this.capitulos = capitulos;
        this.proyectos = proyectos;
        this.bonificaciones = bonificaciones;
        this.docentesEvaluados = docentesEvaluados;
        this.puntajeTotal = puntajeTotal;
    }

    public long getArticulos() {
        return articulos;
    }

    public long getProceedings() {
        return proceedings;
    }

    public long getLibros() {
        return libros;
    }

    public long getCapitulos() {
        return capitulos;
    }

    public long getProyectos() {
        return proyectos;
    }

    public BigDecimal getBonificaciones() {
        return bonificaciones;
    }

    public long getDocentesEvaluados() {
        return docentesEvaluados;
    }

    public BigDecimal getPuntajeTotal() {
        return puntajeTotal;
    }
}