package ec.edu.uteq.svpc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.uteq.svpc.entity.ProcesoValoracion;

public interface ProcesoValoracionRepository
        extends JpaRepository<ProcesoValoracion, Integer> {

    List<ProcesoValoracion> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombreAndPeriodo(String nombre, String periodo);

    Optional<ProcesoValoracion> findFirstByEstado(String estado);

    long countByEstado(String estado);

}