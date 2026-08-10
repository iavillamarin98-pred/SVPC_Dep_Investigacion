package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.dto.RankingArticuloDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingArticuloService {

    private final JdbcTemplate jdbcTemplate;

    public RankingArticuloService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RankingArticuloDTO> obtenerRankingArticulos(Long idProceso) {

        String sql = """
                SELECT
                    ROW_NUMBER() OVER (
                        ORDER BY COALESCE(v.puntaje_articulos, 0) DESC
                    ) AS puesto,

                    d.cedula,
                    d.nombres,
                    d.apellidos,
                    d.facultad,
                    d.carrera,

                    COALESCE(v.puntaje_articulos, 0) AS puntaje_articulos

                FROM valoraciones v

                JOIN docentes d
                    ON d.id_docente = v.id_docente

                WHERE v.id_proceso = ?

                AND COALESCE(v.puntaje_articulos, 0) > 0

                ORDER BY
                    COALESCE(v.puntaje_articulos, 0) DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new RankingArticuloDTO(
                        rs.getLong("puesto"),
                        rs.getString("cedula"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("facultad"),
                        rs.getString("carrera"),
                        rs.getBigDecimal("puntaje_articulos")),
                idProceso);
    }
}