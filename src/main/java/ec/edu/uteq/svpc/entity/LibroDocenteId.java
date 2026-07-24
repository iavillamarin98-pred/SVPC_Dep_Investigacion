package ec.edu.uteq.svpc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LibroDocenteId implements Serializable {

    @Column(name = "id_libro")
    private Integer idLibro;

    @Column(name = "id_docente")
    private Long idDocente;

    public LibroDocenteId() {
    }

    public LibroDocenteId(Integer idLibro, Long idDocente) {
        this.idLibro = idLibro;
        this.idDocente = idDocente;
    }

    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof LibroDocenteId that))
            return false;
        return Objects.equals(idLibro, that.idLibro)
                && Objects.equals(idDocente, that.idDocente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLibro, idDocente);
    }
}