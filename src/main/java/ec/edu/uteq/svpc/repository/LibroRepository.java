package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

    Optional<Libro> findByIdProcesoAndCodigo(Integer idProceso, String codigo);

    List<Libro> findByIdProcesoAndEstadoIgnoreCase(Integer idProceso, String estado);

    List<Libro> findByIdProceso(Integer idProceso);
}