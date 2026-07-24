package ec.edu.uteq.svpc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ArticuloDocenteId implements Serializable {

    @Column(name = "id_articulo")
    private Long idArticulo;

    @Column(name = "id_docente")
    private Long idDocente;

    public ArticuloDocenteId() {
    }

    public ArticuloDocenteId(Long idArticulo, Long idDocente) {
        this.idArticulo = idArticulo;
        this.idDocente = idDocente;
    }

    public Long getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Long idArticulo) {
        this.idArticulo = idArticulo;
    }

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticuloDocenteId that)) return false;
        return Objects.equals(idArticulo, that.idArticulo)
                && Objects.equals(idDocente, that.idDocente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idArticulo, idDocente);
    }
}