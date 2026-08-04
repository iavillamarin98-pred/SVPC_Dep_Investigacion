package ec.edu.uteq.svpc.dto;

public class RankingProyectoDTO {

    private Long idDocente;
    private String cedula;
    private String docente;
    private String facultad;
    private String carrera;
    private Double puntaje;

    public RankingProyectoDTO() {
    }

    public RankingProyectoDTO(
            Long idDocente,
            String cedula,
            String docente,
            String facultad,
            String carrera,
            Double puntaje) {

        this.idDocente = idDocente;
        this.cedula = cedula;
        this.docente = docente;
        this.facultad = facultad;
        this.carrera = carrera;
        this.puntaje = puntaje;
    }

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Double getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Double puntaje) {
        this.puntaje = puntaje;
    }

}