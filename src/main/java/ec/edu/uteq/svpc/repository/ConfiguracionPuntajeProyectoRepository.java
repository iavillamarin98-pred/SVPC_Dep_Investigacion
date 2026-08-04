package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.ConfiguracionPuntajeProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionPuntajeProyectoRepository
        extends JpaRepository<ConfiguracionPuntajeProyecto, Integer> {

    Optional<ConfiguracionPuntajeProyecto> findByTipoFinanciamientoIgnoreCase(String tipoFinanciamiento);

}