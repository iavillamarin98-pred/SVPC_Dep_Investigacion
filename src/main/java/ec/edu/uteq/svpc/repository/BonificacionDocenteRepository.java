package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.BonificacionDocente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonificacionDocenteRepository
        extends JpaRepository<BonificacionDocente, Integer> {

    List<BonificacionDocente> findByDocenteIdDocente(Long idDocente);

}