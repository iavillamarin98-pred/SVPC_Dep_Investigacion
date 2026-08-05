package ec.edu.uteq.svpc.dto;

public class RankingCapituloDTO {

    private Integer posicion;
    private String cedula;
    private String apellidos;
    private String nombres;
    private String carrera;
    private Double puntajeCapitulos;

    public RankingCapituloDTO() {
    }

    public RankingCapituloDTO(
            String cedula,
            String apellidos,
            String nombres,
            String carrera,
            Double puntajeCapitulos) {

        this.cedula = cedula;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.carrera = carrera;
        this.puntajeCapitulos = puntajeCapitulos;
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

    public Double getPuntajeCapitulos() {
        return puntajeCapitulos;
    }

    public void setPuntajeCapitulos(Double puntajeCapitulos) {
        this.puntajeCapitulos = puntajeCapitulos;
    }
}