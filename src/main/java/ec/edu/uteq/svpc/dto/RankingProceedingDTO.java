package ec.edu.uteq.svpc.dto;

import java.math.BigDecimal;

public class RankingProceedingDTO {

    private String apellidos;
    private String nombres;
    private BigDecimal puntaje;

    public RankingProceedingDTO() {
    }

    public RankingProceedingDTO(String apellidos,
            String nombres,
            BigDecimal puntaje) {

        this.apellidos = apellidos;
        this.nombres = nombres;
        this.puntaje = puntaje;
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

    public BigDecimal getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(BigDecimal puntaje) {
        this.puntaje = puntaje;
    }

}