package ec.edu.uteq.svpc.service;

import org.springframework.jdbc.core.JdbcTemplate;

import ec.edu.uteq.svpc.dto.RankingArticuloDTO;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingProceedingService {
    private final JdbcTemplate jdbcTemplate;

    public RankingProceedingService(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RankingArticuloDTO> obtenerRankingProceedings(Long idProceso) {

        String sql = """
                SELECT
                    ROW_NUMBER() OVER (
                        ORDER BY COALESCE(v.puntaje_proceedings,0) DESC
                    ) AS puesto,
                    d.cedula,
                    d.nombres,
                    d.apellidos,
                    d.carrera,
                    COALESCE(v.puntaje_proceedings,0) AS puntaje_proceedings
                FROM valoraciones v
                INNER JOIN docentes d
                    ON d.id_docente = v.id_docente
                WHERE v.id_proceso = ?
                AND COALESCE(v.puntaje_proceedings,0) > 0
                ORDER BY puntaje_proceedings DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new RankingArticuloDTO(
                rs.getLong("puesto"),
                rs.getString("cedula"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("carrera"),
                rs.getBigDecimal("puntaje_proceedings")), idProceso);
    }
}
