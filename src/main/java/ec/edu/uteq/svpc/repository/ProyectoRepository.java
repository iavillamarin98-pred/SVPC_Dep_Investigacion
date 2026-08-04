package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

        /**
         * Buscar un proyecto por proceso y nombre.
         * Se utiliza para evitar duplicados durante la importación.
         */
        Optional<Proyecto> findByIdProcesoAndNombre(
                        Integer idProceso,
                        String nombre);

        /**
         * Obtener todos los proyectos pertenecientes a un proceso.
         */
        List<Proyecto> findByIdProceso(Integer idProceso);

        /**
         * Obtener únicamente proyectos aprobados de un proceso.
         */
        List<Proyecto> findByIdProcesoAndEstadoIgnoreCase(
                        Integer idProceso,
                        String estado);

        Optional<Proyecto> findByCodigo(String codigo);

        @Query("""
                        SELECT p
                        FROM Proyecto p
                        WHERE p.idProceso = :idProceso
                          AND UPPER(p.estado) = 'APROBADO'
                        """)
        List<Proyecto> buscarProyectosAprobados(Integer idProceso);

}