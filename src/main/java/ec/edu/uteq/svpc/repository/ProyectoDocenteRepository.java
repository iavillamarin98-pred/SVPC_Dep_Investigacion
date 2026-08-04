package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.dto.RankingProyectoDTO;
import ec.edu.uteq.svpc.entity.ProyectoDocente;
import ec.edu.uteq.svpc.entity.ProyectoDocenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ProyectoDocenteRepository
                extends JpaRepository<ProyectoDocente, ProyectoDocenteId> {

        /**
         * Obtener todos los docentes de un proyecto.
         */
        List<ProyectoDocente> findByIdIdProyecto(Integer idProyecto);

        /**
         * Obtener todos los proyectos de un docente.
         */
        List<ProyectoDocente> findByIdIdDocente(Long idDocente);

        /**
         * Contar participantes de un proyecto.
         */
        long countByIdIdProyecto(Integer idProyecto);

        /**
         * Verificar existencia de una relación.
         */
        boolean existsById(ProyectoDocenteId id);

        /**
         * Verificar si el proyecto ya tiene un director.
         */
        boolean existsByIdIdProyectoAndRolParticipante(
                        Integer idProyecto,
                        String rolParticipante);

        /**
         * Eliminar todas las relaciones de un proyecto.
         */
        void deleteByIdIdProyecto(Integer idProyecto);

        @Query("""
                        SELECT
                        pd.id.idDocente,
                        SUM(pd.puntajeObtenido)
                        FROM ProyectoDocente pd
                        JOIN Proyecto p
                        ON pd.id.idProyecto=p.idProyecto
                        WHERE p.idProceso=:idProceso
                        GROUP BY pd.id.idDocente
                        """)
        List<Object[]> sumarPuntajeProyectosPorDocente(Integer idProceso);

        @Query("""
                        SELECT new ec.edu.uteq.svpc.dto.RankingProyectoDTO(
                            d.idDocente,
                            d.cedula,
                            CONCAT(d.apellidos,' ',d.nombres),
                            d.facultad,
                            d.carrera,
                            SUM(pd.puntajeObtenido)
                        )
                        FROM ProyectoDocente pd
                        JOIN Docente d
                        ON d.idDocente = pd.id.idDocente
                        JOIN Proyecto p
                        ON p.idProyecto = pd.id.idProyecto
                        WHERE p.idProceso = :idProceso
                        AND UPPER(p.estado)='APROBADO'
                        GROUP BY
                            d.idDocente,
                            d.cedula,
                            d.apellidos,
                            d.nombres,
                            d.facultad,
                            d.carrera
                        ORDER BY SUM(pd.puntajeObtenido) DESC
                        """)
        List<RankingProyectoDTO> obtenerRankingProyectos(Integer idProceso);

}