package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.LibroDocente;
import ec.edu.uteq.svpc.entity.LibroDocenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CapituloLibroDocenteRepository extends JpaRepository<LibroDocente, LibroDocenteId> {

    List<LibroDocente> findByIdIdLibro(Integer idLibro);

    @Query(value = """
            SELECT
                ld.id_docente,
                COALESCE(SUM(ld.puntaje_obtenido), 0) AS total_capitulos
            FROM libro_docente ld
            JOIN libros l ON l.id_libro = ld.id_libro
            WHERE l.id_proceso = :idProceso
              AND UPPER(l.tipo) = 'CAPITULO DE LIBRO'
            GROUP BY ld.id_docente
            """, nativeQuery = true)
    List<Object[]> obtenerPuntajeCapitulosPorDocente(@Param("idProceso") Integer idProceso);
}