package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.ArticuloDocente;
import ec.edu.uteq.svpc.entity.ArticuloDocenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticuloDocenteRepository extends JpaRepository<ArticuloDocente, ArticuloDocenteId> {

    List<ArticuloDocente> findByIdIdArticulo(Long idArticulo);

    List<ArticuloDocente> findByIdIdDocente(Long idDocente);

    @Query("""
            SELECT ad.id.idDocente, COALESCE(SUM(ad.puntajeObtenido), 0)
            FROM ArticuloDocente ad
            JOIN Articulo a ON a.idArticulo = ad.id.idArticulo
            WHERE a.idProceso = :idProceso
            AND UPPER(TRIM(a.estadoRevision)) = 'APROBADO'
            GROUP BY ad.id.idDocente
            """)
    List<Object[]> sumarPuntajeArticulosPorDocente(@Param("idProceso") Long idProceso);

    @Query("""
            SELECT
                ad.id.idDocente,
                COALESCE(SUM(ad.puntajeObtenido),0)
            FROM ArticuloDocente ad
            JOIN Articulo a
            ON ad.id.idArticulo = a.idArticulo
            WHERE a.idProceso = :idProceso
            AND UPPER(TRIM(a.estadoRevision))='APROBADO'
            AND a.tipoProceeding IS NOT NULL
            GROUP BY ad.id.idDocente
            """)
    List<Object[]> sumarPuntajeProceedingsPorDocente(
            @Param("idProceso") Long idProceso);
}