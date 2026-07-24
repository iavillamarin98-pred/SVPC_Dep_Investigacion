package ec.edu.uteq.svpc.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

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

    public void actualizarPuntajeCapitulos(Integer idProceso, Integer idDocente, BigDecimal puntajeCapitulos) {
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

        jdbcTemplate.update(sql, puntajeCapitulos, puntajeCapitulos, idProceso, idDocente);
    }
}