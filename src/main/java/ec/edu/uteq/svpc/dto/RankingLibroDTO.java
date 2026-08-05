package ec.edu.uteq.svpc.dto;

import java.math.BigDecimal;

public class RankingLibroDTO {

    private Integer posicion;
    private String cedula;
    private String apellidos;
    private String nombres;
    private String carrera;
    private BigDecimal puntajeLibros;

    public RankingLibroDTO() {
    }

    public RankingLibroDTO(
            Integer posicion,
            String cedula,
            String apellidos,
            String nombres,
            String carrera,
            BigDecimal puntajeLibros) {

        this.posicion = posicion;
        this.cedula = cedula;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.carrera = carrera;
        this.puntajeLibros = puntajeLibros;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public BigDecimal getPuntajeLibros() {
        return puntajeLibros;
    }

    public void setPuntajeLibros(BigDecimal puntajeLibros) {
        this.puntajeLibros = puntajeLibros;
    }
}