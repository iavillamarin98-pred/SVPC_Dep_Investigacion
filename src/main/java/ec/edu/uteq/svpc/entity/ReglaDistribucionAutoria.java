package ec.edu.uteq.svpc.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "regla_distribucion_autoria")
public class ReglaDistribucionAutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regla")
    private Integer idRegla;

    @Column(name = "id_proceso", nullable = false)
    private Integer idProceso;

    @Column(name = "escenario", nullable = false)
    private String escenario;

    @Column(name = "porcentaje_autor", nullable = false)
    private BigDecimal porcentajeAutor;

    @Column(name = "porcentaje_coautor", nullable = false)
    private BigDecimal porcentajeCoautor;

    @Column(name = "estado")
    private Boolean estado = true;

    // Getters y Setters

    public Integer getIdRegla() {
        return idRegla;
    }

    public void setIdRegla(Integer idRegla) {
        this.idRegla = idRegla;
    }

    public Integer getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(Integer idProceso) {
        this.idProceso = idProceso;
    }

    public String getEscenario() {
        return escenario;
    }

    public void setEscenario(String escenario) {
        this.escenario = escenario;
    }

    public BigDecimal getPorcentajeAutor() {
        return porcentajeAutor;
    }

    public void setPorcentajeAutor(BigDecimal porcentajeAutor) {
        this.porcentajeAutor = porcentajeAutor;
    }

    public BigDecimal getPorcentajeCoautor() {
        return porcentajeCoautor;
    }

    public void setPorcentajeCoautor(BigDecimal porcentajeCoautor) {
        this.porcentajeCoautor = porcentajeCoautor;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}