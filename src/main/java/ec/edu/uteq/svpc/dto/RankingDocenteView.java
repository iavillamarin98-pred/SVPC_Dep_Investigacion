package ec.edu.uteq.svpc.dto;

import java.math.BigDecimal;

public interface RankingDocenteView {
    Integer getIdDocente();

    String getCedula();

    String getNombres();

    String getApellidos();

    String getFacultad();

    String getCarrera();

    BigDecimal getPuntajeArticulos();

    BigDecimal getPuntajeProceedings();

    BigDecimal getPuntajeLibros();

    BigDecimal getPuntajeCapitulos();

    BigDecimal getPuntajeProyectos();

    BigDecimal getPuntajeBonificaciones();

    BigDecimal getTotal();

    Long getPuesto();
}