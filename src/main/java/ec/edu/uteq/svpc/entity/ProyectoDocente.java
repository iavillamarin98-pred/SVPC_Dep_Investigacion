package ec.edu.uteq.svpc.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "proyecto_docente")
public class ProyectoDocente {

    @EmbeddedId
    private ProyectoDocenteId id;

    @Column(name = "rol_participante", nullable = false, length = 50)
    private String rolParticipante;

    @Column(name = "puntaje_obtenido")
    private Double puntajeObtenido = 0.0;

    public ProyectoDocente() {
    }

    public ProyectoDocente(ProyectoDocenteId id, String rolParticipante) {
        this.id = id;
        this.rolParticipante = rolParticipante;
        this.puntajeObtenido = 0.0;
    }

    public ProyectoDocenteId getId() {
        return id;
    }

    public void setId(ProyectoDocenteId id) {
        this.id = id;
    }

    public String getRolParticipante() {
        return rolParticipante;
    }

    public void setRolParticipante(String rolParticipante) {
        this.rolParticipante = rolParticipante;
    }

    public Double getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(Double puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }
}