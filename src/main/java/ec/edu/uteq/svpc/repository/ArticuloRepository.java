package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    Optional<Articulo> findByIdProcesoAndCodigo(Long idProceso, String codigo);

    @Query("""
            SELECT a
            FROM Articulo a
            WHERE a.idProceso = :idProceso
            AND UPPER(TRIM(a.estadoRevision)) = 'APROBADO'
            """)
    List<Articulo> buscarArticulosAprobadosPorProceso(@Param("idProceso") Long idProceso);

    @Query("""
            SELECT a
            FROM Articulo a
            WHERE a.idProceso = :idProceso
            AND UPPER(TRIM(a.estadoRevision)) = 'APROBADO'
            AND a.tipoProceeding IS NOT NULL
            """)
    List<Articulo> buscarProceedingsAprobadosPorProceso(
            @Param("idProceso") Long idProceso);

}