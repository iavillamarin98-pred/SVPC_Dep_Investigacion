package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.LibroDocente;
import ec.edu.uteq.svpc.entity.LibroDocenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface LibroDocenteRepository extends JpaRepository<LibroDocente, LibroDocenteId> {

    List<LibroDocente> findByLibro_IdLibro(Integer idLibro);

    @Modifying
    @Query(value = """
            UPDATE libro_docente ld
            SET puntaje_obtenido = 0
            FROM libros l
            WHERE l.id_libro = ld.id_libro
              AND l.id_proceso = :idProceso
            """, nativeQuery = true)
    void reiniciarPuntajesPorProceso(Integer idProceso);

    @Query(value = """
            SELECT
                d.id_docente AS id_docente,
                d.cedula AS cedula,
                d.apellidos AS apellidos,
                d.nombres AS nombres,
                SUM(ld.puntaje_obtenido) AS puntaje
            FROM libro_docente ld
            JOIN libros l ON l.id_libro = ld.id_libro
            JOIN docentes d ON d.id_docente = ld.id_docente
            WHERE l.id_proceso = :idProceso
            GROUP BY d.id_docente, d.cedula, d.apellidos, d.nombres
            ORDER BY puntaje DESC
            """, nativeQuery = true)
    List<Map<String, Object>> resumenPuntajesLibros(Integer idProceso);
}