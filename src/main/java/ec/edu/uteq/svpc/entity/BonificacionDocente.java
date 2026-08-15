package ec.edu.uteq.svpc.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bonificaciones_docente")
public class BonificacionDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bonificacion")
    private Integer idBonificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente", nullable = false)
    private Docente docente;

    @Column(name = "criterio_asignacion", nullable = false, length = 50)
    private String criterioAsignacion;

    @Column(name = "puntaje_asignado", nullable = false, precision = 5, scale = 2)
    private BigDecimal puntajeAsignado;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        fechaAsignacion = LocalDateTime.now();
    }

    public Integer getIdBonificacion() {
        return idBonificacion;
    }

    public void setIdBonificacion(Integer idBonificacion) {
        this.idBonificacion = idBonificacion;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public String getCriterioAsignacion() {
        return criterioAsignacion;
    }

    public void setCriterioAsignacion(String criterioAsignacion) {
        this.criterioAsignacion = criterioAsignacion;
    }

    public BigDecimal getPuntajeAsignado() {
        return puntajeAsignado;
    }

    public void setPuntajeAsignado(BigDecimal puntajeAsignado) {
        this.puntajeAsignado = puntajeAsignado;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}