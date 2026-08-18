package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.dto.DashboardResumenDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardResumenDTO obtenerResumen(Integer idProceso) {

        Long articulos = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM articulos
                WHERE id_proceso = ?
                  AND UPPER(TRIM(estado_revision)) = 'APROBADO'
                  AND tipo_proceeding IS NULL
                """, Long.class, idProceso);

        Long proceedings = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM articulos
                WHERE id_proceso = ?
                  AND UPPER(TRIM(estado_revision)) = 'APROBADO'
                  AND tipo_proceeding IS NOT NULL
                """, Long.class, idProceso);

        Long libros = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM libros
                WHERE id_proceso = ?
                  AND UPPER(TRIM(tipo)) <> 'CAPITULO DE LIBRO'
                """, Long.class, idProceso);

        Long capitulos = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM libros
                WHERE id_proceso = ?
                  AND UPPER(TRIM(tipo)) = 'CAPITULO DE LIBRO'
                """, Long.class, idProceso);

        Long proyectos = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM proyectos
                WHERE id_proceso = ?
                  AND UPPER(TRIM(estado)) = 'APROBADO'
                """, Long.class, idProceso);

        BigDecimal bonificaciones = jdbcTemplate.queryForObject("""
                SELECT COALESCE(SUM(puntaje_asignado), 0)
                FROM bonificaciones_docente
                WHERE id_proceso = ?
                """, BigDecimal.class, idProceso);

        Long docentes = jdbcTemplate.queryForObject("""
                SELECT COUNT(DISTINCT id_docente)
                FROM valoraciones
                WHERE id_proceso = ?
                """, Long.class, idProceso);

        BigDecimal puntaje = jdbcTemplate.queryForObject("""
                SELECT COALESCE(SUM(puntaje_total), 0)
                FROM valoraciones
                WHERE id_proceso = ?
                """, BigDecimal.class, idProceso);

        return new DashboardResumenDTO(
                articulos != null ? articulos : 0,
                proceedings != null ? proceedings : 0,
                libros != null ? libros : 0,
                capitulos != null ? capitulos : 0,
                proyectos != null ? proyectos : 0,
                bonificaciones != null ? bonificaciones : BigDecimal.ZERO,
                docentes != null ? docentes : 0,
                puntaje != null ? puntaje : BigDecimal.ZERO);
    }
}