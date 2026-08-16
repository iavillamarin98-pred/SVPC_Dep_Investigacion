package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.ConfiguracionPuntajeProyecto;
import ec.edu.uteq.svpc.entity.Proyecto;
import ec.edu.uteq.svpc.entity.ProyectoDocente;
import ec.edu.uteq.svpc.repository.ConfiguracionPuntajeProyectoRepository;
import ec.edu.uteq.svpc.repository.ProyectoDocenteRepository;
import ec.edu.uteq.svpc.repository.ProyectoRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CalculoPuntajeProyectoService {

        private final ProyectoRepository proyectoRepository;
        private final ProyectoDocenteRepository proyectoDocenteRepository;
        private final ConfiguracionPuntajeProyectoRepository configuracionRepository;
        private final JdbcTemplate jdbcTemplate;

        public CalculoPuntajeProyectoService(
                        ProyectoRepository proyectoRepository,
                        ProyectoDocenteRepository proyectoDocenteRepository,
                        ConfiguracionPuntajeProyectoRepository configuracionRepository,
                        JdbcTemplate jdbcTemplate) {

                this.proyectoRepository = proyectoRepository;
                this.proyectoDocenteRepository = proyectoDocenteRepository;
                this.configuracionRepository = configuracionRepository;
                this.jdbcTemplate = jdbcTemplate;
        }

        @Transactional
        public String calcular(Integer idProceso) {

                int proyectosEncontrados = 0;
                int proyectosProcesados = 0;
                int relacionesProcesadas = 0;
                int proyectosSinParticipantes = 0;
                int proyectosSinConfiguracion = 0;

                /*
                 * 1. Obtener únicamente proyectos APROBADOS
                 */
                List<Proyecto> proyectos = proyectoRepository.findByIdProcesoAndEstadoIgnoreCase(
                                idProceso,
                                "APROBADO");

                proyectosEncontrados = proyectos.size();

                /*
                 * 2. Procesar cada proyecto
                 */
                for (Proyecto proyecto : proyectos) {

                        /*
                         * Buscar configuración según el tipo
                         * de financiamiento.
                         */
                        String tipoFinanciamiento = normalizarTipoFinanciamiento(
                                        proyecto.getTipoFinanciamiento());

                        ConfiguracionPuntajeProyecto config = configuracionRepository
                                        .findByTipoFinanciamientoIgnoreCase(
                                                        tipoFinanciamiento)
                                        .orElse(null);

                        /*
                         * Obtener participantes
                         */
                        List<ProyectoDocente> participantes = proyectoDocenteRepository
                                        .findByIdIdProyecto(
                                                        proyecto.getIdProyecto());

                        if (participantes.isEmpty()) {

                                proyectosSinParticipantes++;

                                continue;
                        }

                        /*
                         * Si no existe configuración,
                         * todos los participantes reciben 0.
                         */
                        if (config == null) {

                                for (ProyectoDocente participante : participantes) {

                                        participante.setPuntajeObtenido(0.0);

                                        proyectoDocenteRepository.save(participante);
                                }

                                proyectosSinConfiguracion++;

                                continue;
                        }

                        /*
                         * 3. Asignar puntajes según el rol
                         */
                        for (ProyectoDocente participante : participantes) {

                                String rol = normalizarRol(
                                                participante.getRolParticipante());

                                if ("DIRECTOR".equals(rol)) {

                                        participante.setPuntajeObtenido(
                                                        config.getPuntajeDirector());

                                } else if ("INTEGRANTE".equals(rol)) {

                                        participante.setPuntajeObtenido(
                                                        config.getPuntajeIntegrante());

                                } else {

                                        /*
                                         * Rol desconocido.
                                         * No se asignan puntos.
                                         */
                                        participante.setPuntajeObtenido(0.0);
                                }

                                proyectoDocenteRepository.save(participante);

                                relacionesProcesadas++;
                        }

                        proyectosProcesados++;
                }

                /*
                 * 4. Consolidar puntajes por docente
                 */
                consolidarValoraciones(idProceso);

                /*
                 * 5. Mensaje de resultado
                 */
                return """
                                Cálculo de puntajes de proyectos finalizado.

                                Proyectos aprobados encontrados: %d
                                Proyectos procesados: %d
                                Relaciones procesadas: %d
                                Proyectos sin participantes: %d
                                Proyectos sin configuración: %d

                                Puntajes consolidados en valoraciones.puntaje_proyectos.
                                """
                                .formatted(
                                                proyectosEncontrados,
                                                proyectosProcesados,
                                                relacionesProcesadas,
                                                proyectosSinParticipantes,
                                                proyectosSinConfiguracion);
        }

        /*
         * ==========================================================
         * CONSOLIDACIÓN
         * ==========================================================
         */

        private void consolidarValoraciones(Integer idProceso) {

                List<Object[]> puntajes = proyectoDocenteRepository
                                .sumarPuntajeProyectosPorDocente(idProceso);

                for (Object[] fila : puntajes) {

                        Long idDocente = ((Number) fila[0]).longValue();

                        Double puntaje = ((Number) fila[1]).doubleValue();

                        Integer existe = jdbcTemplate.queryForObject(
                                        """
                                                        SELECT COUNT(*)
                                                        FROM valoraciones
                                                        WHERE id_proceso = ?
                                                          AND id_docente = ?
                                                        """,
                                        Integer.class,
                                        idProceso,
                                        idDocente);

                        if (existe != null && existe > 0) {

                                jdbcTemplate.update(
                                                """
                                                                UPDATE valoraciones
                                                                SET puntaje_proyectos = ?
                                                                WHERE id_proceso = ?
                                                                  AND id_docente = ?
                                                                """,
                                                puntaje,
                                                idProceso,
                                                idDocente);

                        } else {

                                jdbcTemplate.update(
                                                """
                                                                INSERT INTO valoraciones
                                                                (
                                                                    id_proceso,
                                                                    id_docente,
                                                                    puntaje_proyectos
                                                                )
                                                                VALUES (?, ?, ?)
                                                                """,
                                                idProceso,
                                                idDocente,
                                                puntaje);
                        }
                }
        }

        /*
         * ==========================================================
         * NORMALIZACIÓN
         * ==========================================================
         */

        private String normalizarTipoFinanciamiento(String tipo) {

                if (tipo == null || tipo.isBlank()) {
                        return "";
                }

                String valor = tipo.trim()
                                .toUpperCase()
                                .replace("Á", "A")
                                .replace("É", "E")
                                .replace("Í", "I")
                                .replace("Ó", "O")
                                .replace("Ú", "U");

                /*
                 * Financiamiento externo / en red
                 */
                if (valor.contains("EXTERNO")) {
                        return "EXTERNO";
                }

                /*
                 * Financiamiento conjunto
                 */
                if (valor.contains("CONJUNTO")) {
                        return "CONJUNTO";
                }

                /*
                 * Financiamiento interno
                 */
                if (valor.contains("INTERNO")) {
                        return "INTERNO";
                }

                return valor;
        }

        private String normalizarRol(String rol) {

                if (rol == null || rol.isBlank()) {
                        return "";
                }

                return rol.trim()
                                .toUpperCase()
                                .replace("Á", "A")
                                .replace("É", "E")
                                .replace("Í", "I")
                                .replace("Ó", "O")
                                .replace("Ú", "U");
        }
}