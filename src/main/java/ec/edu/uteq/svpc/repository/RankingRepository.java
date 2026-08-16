package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.dto.RankingDocenteView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ec.edu.uteq.svpc.entity.Docente; // ajusta el import a tu entidad real

import java.util.List;

public interface RankingRepository extends JpaRepository<Docente, Integer> {

    String BASE_QUERY = """
            SELECT
                d.id_docente        AS idDocente,
                d.cedula             AS cedula,
                d.nombres            AS nombres,
                d.apellidos          AS apellidos,
                d.facultad           AS facultad,
                d.carrera            AS carrera,
                COALESCE(art.total, 0)  AS puntajeArticulos,
                COALESCE(proc.total,0)  AS puntajeProceedings,
                COALESCE(lib.total, 0)  AS puntajeLibros,
                COALESCE(cap.total, 0)  AS puntajeCapitulos,
                COALESCE(pro.total, 0)  AS puntajeProyectos,
                COALESCE(bon.total, 0)  AS puntajeBonificaciones,
                (COALESCE(art.total,0) + COALESCE(proc.total,0) + COALESCE(lib.total,0)
                  + COALESCE(cap.total,0) + COALESCE(pro.total,0) + COALESCE(bon.total,0)) AS total,
                RANK() OVER (ORDER BY
                  (COALESCE(art.total,0) + COALESCE(proc.total,0) + COALESCE(lib.total,0)
                    + COALESCE(cap.total,0) + COALESCE(pro.total,0) + COALESCE(bon.total,0)) DESC
                ) AS puesto
            FROM docentes d
            LEFT JOIN (
                SELECT ad.id_docente, SUM(ad.puntaje_obtenido) total
                FROM articulo_docente ad
                JOIN articulos a ON a.id_articulo = ad.id_articulo
                WHERE a.id_proceso = :idProceso
                  AND (a.base_indexada IS DISTINCT FROM 'PROCEEDING')
                GROUP BY ad.id_docente
            ) art  ON art.id_docente = d.id_docente
            LEFT JOIN (
                SELECT ad.id_docente, SUM(ad.puntaje_obtenido) total
                FROM articulo_docente ad
                JOIN articulos a ON a.id_articulo = ad.id_articulo
                WHERE a.id_proceso = :idProceso
                  AND a.base_indexada = 'PROCEEDING'
                GROUP BY ad.id_docente
            ) proc ON proc.id_docente = d.id_docente
            LEFT JOIN (
                SELECT ld.id_docente, SUM(ld.puntaje_obtenido) total
                FROM libro_docente ld JOIN libros l ON l.id_libro = ld.id_libro
                WHERE l.id_proceso = :idProceso AND l.tipo = 'LIBRO'
                GROUP BY ld.id_docente
            ) lib ON lib.id_docente = d.id_docente
            LEFT JOIN (
                SELECT ld.id_docente, SUM(ld.puntaje_obtenido) total
                FROM libro_docente ld JOIN libros l ON l.id_libro = ld.id_libro
                WHERE l.id_proceso = :idProceso AND l.tipo = 'CAPITULO DE LIBRO'
                GROUP BY ld.id_docente
            ) cap ON cap.id_docente = d.id_docente
            LEFT JOIN (
                SELECT pd.id_docente, SUM(pd.puntaje_obtenido) total
                FROM proyecto_docente pd JOIN proyectos p ON p.id_proyecto = pd.id_proyecto
                WHERE p.id_proceso = :idProceso
                GROUP BY pd.id_docente
            ) pro ON pro.id_docente = d.id_docente
            LEFT JOIN (
                SELECT id_docente, SUM(puntaje_asignado) total
                FROM bonificaciones_docente
                WHERE id_proceso = :idProceso
                GROUP BY id_docente
            ) bon ON bon.id_docente = d.id_docente
            WHERE d.estado = true
            """;

    @Query(value = BASE_QUERY + " ORDER BY total DESC", nativeQuery = true)
    List<RankingDocenteView> findRankingGeneral(@Param("idProceso") Integer idProceso);

    @Query(value = BASE_QUERY + " AND d.facultad = :facultad ORDER BY total DESC", nativeQuery = true)
    List<RankingDocenteView> findRankingPorFacultad(@Param("idProceso") Integer idProceso,
            @Param("facultad") String facultad);

    @Query(value = "SELECT DISTINCT d.facultad FROM docentes d WHERE d.estado = true AND d.facultad IS NOT NULL ORDER BY d.facultad", nativeQuery = true)
    List<String> findFacultadesDisponibles();
}