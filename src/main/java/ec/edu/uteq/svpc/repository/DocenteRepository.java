package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

    Optional<Docente> findByCedula(String cedula);

    boolean existsByCedula(String cedula);
}