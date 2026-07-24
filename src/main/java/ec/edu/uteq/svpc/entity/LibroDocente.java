package ec.edu.uteq.svpc.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "libro_docente")
public class LibroDocente {

    @EmbeddedId
    private LibroDocenteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idLibro")
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idDocente")
    @JoinColumn(name = "id_docente")
    private Docente docente;

    @Column(name = "rol_participante", nullable = false)
    private String rolParticipante;

    @Column(name = "puntaje_obtenido")
    private BigDecimal puntajeObtenido = BigDecimal.ZERO;

    public LibroDocenteId getId() {
        return id;
    }

    public void setId(LibroDocenteId id) {
        this.id = id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
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