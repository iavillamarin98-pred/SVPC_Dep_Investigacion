package ec.edu.uteq.svpc.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "configuracion_puntaje")
public class ConfiguracionPuntaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @Column(name = "id_proceso", nullable = false)
    private Long idProceso;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "criterio", nullable = false)
    private String criterio;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "puntaje_base", nullable = false)
    private BigDecimal puntajeBase;

    @Column(name = "estado")
    private Boolean estado = true;

    public Long getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(Long idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public Long getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(Long idProceso) {
        this.idProceso = idProceso;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public BigDecimal getPuntajeBase() {
        return puntajeBase;
    }

    public void setPuntajeBase(BigDecimal puntajeBase) {
        this.puntajeBase = puntajeBase;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}