package ec.edu.uteq.svpc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProyectoDocenteId implements Serializable {

    @Column(name = "id_proyecto")
    private Integer idProyecto;

    @Column(name = "id_docente")
    private Long idDocente;

    public ProyectoDocenteId() {
    }

    public ProyectoDocenteId(Integer idProyecto, Long idDocente) {
        this.idProyecto = idProyecto;
        this.idDocente = idDocente;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
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
        if (!(o instanceof ProyectoDocenteId))
            return false;
        ProyectoDocenteId that = (ProyectoDocenteId) o;
        return Objects.equals(idProyecto, that.idProyecto) &&
                Objects.equals(idDocente, that.idDocente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProyecto, idDocente);
    }
}