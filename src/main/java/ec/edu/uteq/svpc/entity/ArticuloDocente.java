package ec.edu.uteq.svpc.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "articulo_docente")
public class ArticuloDocente {

    @EmbeddedId
    private ArticuloDocenteId id;

    @Column(name = "rol_participante", nullable = false, length = 50)
    private String rolParticipante;

    @Column(name = "puntaje_obtenido")
    private BigDecimal puntajeObtenido = BigDecimal.ZERO;

    public ArticuloDocenteId getId() {
        return id;
    }

    public void setId(ArticuloDocenteId id) {
        this.id = id;
    }

    public String getRolParticipante() {
        return rolParticipante;
    }

    public void setRolParticipante(String rolParticipante) {
        this.rolParticipante = rolParticipante;
    }

    public BigDecimal getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(BigDecimal puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }
}