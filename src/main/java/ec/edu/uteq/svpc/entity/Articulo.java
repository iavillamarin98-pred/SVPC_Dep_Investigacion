package ec.edu.uteq.svpc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "articulos")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_articulo")
    private Long idArticulo;

    @Column(name = "id_proceso", nullable = false)
    private Long idProceso;
    @Column(name = "tipo_proceeding")
    private String tipoProceeding;

    @Column(name = "codigo", length = 120)
    private String codigo;

    @Column(name = "titulo", nullable = false, columnDefinition = "TEXT")
    private String titulo;

    @Column(name = "issn", length = 50)
    private String issn;

    @Column(name = "revista", columnDefinition = "TEXT")
    private String revista;

    @Column(name = "base_indexada", columnDefinition = "TEXT")
    private String baseIndexada;

    @Column(name = "cuartil", length = 30)
    private String cuartil;

    @Column(name = "area_articulo", columnDefinition = "TEXT")
    private String areaArticulo;

    @Column(name = "tipo_participante", columnDefinition = "TEXT")
    private String tipoParticipante;

    @Column(name = "periodo", columnDefinition = "TEXT")
    private String periodo;

    /*
     * Este campo guarda la columna:
     * ESTADO DE PUBLICACIÓN
     * Ejemplo: PUBLICADO
     */
    @Column(name = "estado", columnDefinition = "TEXT")
    private String estado;

    /*
     * Este campo guarda la columna:
     * ESTADO
     * Ejemplo: APROBADO
     */
    @Column(name = "estado_revision", length = 50)
    private String estadoRevision;

    public Long getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Long idArticulo) {
        this.idArticulo = idArticulo;
    }

    public Long getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(Long idProceso) {
        this.idProceso = idProceso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getRevista() {
        return revista;
    }

    public void setRevista(String revista) {
        this.revista = revista;
    }

    public String getBaseIndexada() {
        return baseIndexada;
    }

    public void setBaseIndexada(String baseIndexada) {
        this.baseIndexada = baseIndexada;
    }

    public String getCuartil() {
        return cuartil;
    }

    public void setCuartil(String cuartil) {
        this.cuartil = cuartil;
    }

    public String getAreaArticulo() {
        return areaArticulo;
    }

    public void setAreaArticulo(String areaArticulo) {
        this.areaArticulo = areaArticulo;
    }

    public String getTipoParticipante() {
        return tipoParticipante;
    }

    public void setTipoParticipante(String tipoParticipante) {
        this.tipoParticipante = tipoParticipante;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoRevision() {
        return estadoRevision;
    }

    public void setEstadoRevision(String estadoRevision) {
        this.estadoRevision = estadoRevision;
    }

    public String getTipoProceeding() {
        return tipoProceeding;
    }

    public void setTipoProceeding(String tipoProceeding) {
        this.tipoProceeding = tipoProceeding;
    }
}