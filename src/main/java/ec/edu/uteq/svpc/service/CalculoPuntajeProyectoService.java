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

    public String calcular(Integer idProceso) {

        int proyectosProcesados = 0;
        int relacionesProcesadas = 0;
        int proyectosSinConfiguracion = 0;

        List<Proyecto> proyectos = proyectoRepository.findByIdProcesoAndEstadoIgnoreCase(
                idProceso,
                "APROBADO");

        for (Proyecto proyecto : proyectos) {

            ConfiguracionPuntajeProyecto config = configuracionRepository
                    .findByTipoFinanciamientoIgnoreCase(
                            proyecto.getTipoFinanciamiento())
                    .orElse(null);

            if (config == null) {

                for (ProyectoDocente participante : proyectoDocenteRepository
                        .findByIdIdProyecto(proyecto.getIdProyecto())) {

                    participante.setPuntajeObtenido(0.0);

                    proyectoDocenteRepository.save(participante);
                }

                proyectosSinConfiguracion++;

                continue;
            }

            List<ProyectoDocente> participantes = proyectoDocenteRepository.findByIdIdProyecto(
                    proyecto.getIdProyecto());

            for (ProyectoDocente participante : participantes) {

                if ("DIRECTOR".equalsIgnoreCase(participante.getRolParticipante())) {

                    participante.setPuntajeObtenido(
                            config.getPuntajeDirector());

                } else {

                    participante.setPuntajeObtenido(
                            config.getPuntajeIntegrante());

                }

                proyectoDocenteRepository.save(participante);
                relacionesProcesadas++;
            }

            proyectosProcesados++;
        }

        consolidarValoraciones(idProceso);

        return """
                Cálculo de puntajes de proyectos finalizado.

                Proyectos procesados: %d
                Relaciones procesadas: %d
                Proyectos sin configuración: %d
                """
                .formatted(
                        proyectosProcesados,
                        relacionesProcesadas,
                        proyectosSinConfiguracion);
    }

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
                                (id_proceso,
                                 id_docente,
                                 puntaje_proyectos)
                                VALUES (?,?,?)
                                """,
                        idProceso,
                        idDocente,
                        puntaje);

            }

        }

    }

}