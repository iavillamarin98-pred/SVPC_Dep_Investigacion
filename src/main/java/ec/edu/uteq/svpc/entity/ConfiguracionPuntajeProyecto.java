package ec.edu.uteq.svpc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion_puntaje_proyecto")
public class ConfiguracionPuntajeProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Integer idConfiguracion;

    @Column(name = "tipo_financiamiento")
    private String tipoFinanciamiento;

    @Column(name = "puntaje_director")
    private Double puntajeDirector;

    @Column(name = "puntaje_integrante")
    private Double puntajeIntegrante;

    public Integer getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(Integer idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public String getTipoFinanciamiento() {
        return tipoFinanciamiento;
    }

    public void setTipoFinanciamiento(String tipoFinanciamiento) {
        this.tipoFinanciamiento = tipoFinanciamiento;
    }

    public Double getPuntajeDirector() {
        return puntajeDirector;
    }

    public void setPuntajeDirector(Double puntajeDirector) {
        this.puntajeDirector = puntajeDirector;
    }

    public Double getPuntajeIntegrante() {
        return puntajeIntegrante;
    }

    public void setPuntajeIntegrante(Double puntajeIntegrante) {
        this.puntajeIntegrante = puntajeIntegrante;
    }

}