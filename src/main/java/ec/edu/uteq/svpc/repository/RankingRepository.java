package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.dto.RankingDocenteView;
import ec.edu.uteq.svpc.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RankingRepository
        extends JpaRepository<Docente, Integer> {

    String BASE_QUERY = """
            SELECT
                d.id_docente AS idDocente,
                d.cedula AS cedula,
                d.nombres AS nombres,
                d.apellidos AS apellidos,
                d.facultad AS facultad,
                d.carrera AS carrera,

                COALESCE(v.puntaje_articulos, 0)
                    AS puntajeArticulos,

                COALESCE(v.puntaje_proceedings, 0)
                    AS puntajeProceedings,

                COALESCE(v.puntaje_libros, 0)
                    AS puntajeLibros,

                COALESCE(v.puntaje_capitulos_libro, 0)
                    AS puntajeCapitulos,

                COALESCE(v.puntaje_proyectos, 0)
                    AS puntajeProyectos,

                COALESCE(bon.total, 0)
                    AS puntajeBonificaciones,

                (
                    COALESCE(v.puntaje_articulos, 0)
                    + COALESCE(v.puntaje_proceedings, 0)
                    + COALESCE(v.puntaje_libros, 0)
                    + COALESCE(v.puntaje_capitulos_libro, 0)
                    + COALESCE(v.puntaje_proyectos, 0)
                    + COALESCE(bon.total, 0)
                ) AS total,

                RANK() OVER (
                    ORDER BY
                    (
                        COALESCE(v.puntaje_articulos, 0)
                        + COALESCE(v.puntaje_proceedings, 0)
                        + COALESCE(v.puntaje_libros, 0)
                        + COALESCE(v.puntaje_capitulos_libro, 0)
                        + COALESCE(v.puntaje_proyectos, 0)
                        + COALESCE(bon.total, 0)
                    ) DESC
                ) AS puesto

            FROM docentes d

            LEFT JOIN valoraciones v
                ON v.id_docente = d.id_docente
                AND v.id_proceso = :idProceso

            LEFT JOIN (
                SELECT
                    id_docente,
                    id_proceso,
                    SUM(puntaje_asignado) AS total
                FROM bonificaciones_docente
                GROUP BY
                    id_docente,
                    id_proceso
            ) bon
                ON bon.id_docente = d.id_docente
                AND bon.id_proceso = :idProceso

            WHERE d.estado = true
            """;

    // =========================================================
    // RANKING GENERAL
    // =========================================================

    @Query(value = BASE_QUERY + """
            ORDER BY total DESC
            """, nativeQuery = true)
    List<RankingDocenteView> findRankingGeneral(
            @Param("idProceso") Integer idProceso);

    // =========================================================
    // RANKING POR FACULTAD
    // =========================================================

    @Query(value = BASE_QUERY + """
            AND d.facultad = :facultad
            ORDER BY total DESC
            """, nativeQuery = true)
    List<RankingDocenteView> findRankingPorFacultad(
            @Param("idProceso") Integer idProceso,
            @Param("facultad") String facultad);

    // =========================================================
    // FACULTADES DISPONIBLES
    // =========================================================

    @Query(value = """
            SELECT DISTINCT d.facultad
            FROM docentes d
            WHERE d.estado = true
              AND d.facultad IS NOT NULL
            ORDER BY d.facultad
            """, nativeQuery = true)
    List<String> findFacultadesDisponibles();
}