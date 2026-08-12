package ec.edu.uteq.svpc.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ValoracionCapituloLibroRepository {

    private final JdbcTemplate jdbcTemplate;

    public ValoracionCapituloLibroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertarSiNoExiste(Integer idProceso, Integer idDocente) {

        String sql = """
                INSERT INTO valoraciones (
                    id_proceso,
                    id_docente,
                    puntaje_articulos,
                    puntaje_libros,
                    puntaje_capitulos_libro,
                    puntaje_proyectos,
                    puntaje_total,
                    fecha_calculo
                )
                VALUES (?, ?, 0, 0, 0, 0, 0, CURRENT_TIMESTAMP)
                ON CONFLICT (id_proceso, id_docente)
                DO NOTHING
                """;

        jdbcTemplate.update(sql, idProceso, idDocente);
    }

    public void actualizarPuntajeCapitulos(
            Integer idProceso,
            Integer idDocente,
            BigDecimal puntajeCapitulos) {

        String sql = """
                UPDATE valoraciones
                SET
                    puntaje_capitulos_libro = ?,
                    puntaje_total =
                        COALESCE(puntaje_articulos, 0) +
                        COALESCE(puntaje_libros, 0) +
                        COALESCE(?, 0) +
                        COALESCE(puntaje_proyectos, 0),
                    fecha_calculo = CURRENT_TIMESTAMP
                WHERE id_proceso = ?
                  AND id_docente = ?
                """;

        jdbcTemplate.update(
                sql,
                puntajeCapitulos,
                puntajeCapitulos,
                idProceso,
                idDocente);
    }

    public List<Object[]> obtenerRankingCapitulos(Integer idProceso) {

        String sql = """
                SELECT
                    d.id_docente,
                    d.cedula,
                    d.apellidos,
                    d.nombres,
                    d.carrera,
                    v.puntaje_capitulos_libro
                FROM valoraciones v
                INNER JOIN docentes d
                    ON d.id_docente = v.id_docente
                WHERE v.id_proceso = ?
                ORDER BY
                    v.puntaje_capitulos_libro DESC,
                    d.apellidos,
                    d.nombres
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Object[] {
                        rs.getLong("id_docente"),
                        rs.getString("cedula"),
                        rs.getString("apellidos"),
                        rs.getString("nombres"),
                        rs.getString("carrera"),
                        rs.getBigDecimal("puntaje_capitulos_libro")
                },
                idProceso);
    }
}