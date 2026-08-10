package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Docente> findByNombresIgnoreCaseAndApellidosIgnoreCase(
            String nombres,
            String apellidos);

    @Query("""
                SELECT d
                FROM Docente d
                WHERE
                    (:cedula IS NULL OR LOWER(d.cedula) LIKE LOWER(CONCAT('%', :cedula, '%')))
                    AND
                    (:nombre IS NULL OR
                        LOWER(d.nombres) LIKE LOWER(CONCAT('%', :nombre, '%'))
                        OR
                        LOWER(d.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))
                    )
                    AND
                    (:facultad IS NULL OR LOWER(d.facultad) LIKE LOWER(CONCAT('%', :facultad, '%')))
                ORDER BY d.apellidos, d.nombres
            """)
    List<Docente> buscarConFiltros(
            @Param("cedula") String cedula,
            @Param("nombre") String nombre,
            @Param("facultad") String facultad);

    List<Docente> findByApellidosContainingIgnoreCase(String apellidos);

}