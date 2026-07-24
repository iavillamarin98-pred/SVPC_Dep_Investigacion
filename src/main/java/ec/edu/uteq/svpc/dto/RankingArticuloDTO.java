package ec.edu.uteq.svpc.dto;

import java.math.BigDecimal;

public class RankingArticuloDTO {

    private Long puesto;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String carrera;
    private BigDecimal puntajeArticulos;

    public RankingArticuloDTO() {
    }

    public RankingArticuloDTO(Long puesto, String cedula, String nombres, String apellidos, String carrera,
            BigDecimal puntajeArticulos) {
        this.puesto = puesto;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.carrera = carrera;
        this.puntajeArticulos = puntajeArticulos;
    }

    public Long getPuesto() {
        return puesto;
    }

    public void setPuesto(Long puesto) {
        this.puesto = puesto;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public BigDecimal getPuntajeArticulos() {
        return puntajeArticulos;
    }

    public void setPuntajeArticulos(BigDecimal puntajeArticulos) {
        this.puntajeArticulos = puntajeArticulos;
    }
}