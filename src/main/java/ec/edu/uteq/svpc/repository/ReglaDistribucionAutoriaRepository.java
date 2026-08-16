package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReglaDistribucionAutoriaRepository
        extends JpaRepository<ReglaDistribucionAutoria, Integer> {

    Optional<ReglaDistribucionAutoria> findByIdProcesoAndEscenarioAndEstadoTrue(
            Integer idProceso,
            String escenario);

    List<ReglaDistribucionAutoria> findByIdProcesoAndEstadoTrueOrderByEscenarioAsc(
            Integer idProceso);
}