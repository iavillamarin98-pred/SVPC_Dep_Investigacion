package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

        Optional<Docente> findByCedula(String cedula);

        boolean existsByCedula(String cedula);

        List<Docente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
                        String nombres,
                        String apellidos);

        List<Docente> findByCedulaContaining(String cedula);

        List<Docente> findByCedulaContainingIgnoreCaseOrNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrFacultadContainingIgnoreCaseOrCarreraContainingIgnoreCase(
                        String cedula,
                        String nombres,
                        String apellidos,
                        String facultad,
                        String carrera);

        List<Docente> findByNombresContainingIgnoreCaseAndApellidosContainingIgnoreCase(
                        String nombres,
                        String apellidos);
}
