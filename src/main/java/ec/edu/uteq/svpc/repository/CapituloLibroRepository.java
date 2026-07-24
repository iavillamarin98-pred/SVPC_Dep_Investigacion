package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CapituloLibroRepository extends JpaRepository<Libro, Integer> {

  @Query("""
      SELECT l
      FROM Libro l
      WHERE l.idProceso = :idProceso
        AND UPPER(l.tipo) = 'CAPITULO DE LIBRO'
      """)
  List<Libro> buscarCapitulosPorProceso(@Param("idProceso") Integer idProceso);

  @Query("""
      SELECT l
      FROM Libro l
      WHERE l.idProceso = :idProceso
        AND l.codigo = :codigo
        AND UPPER(l.tipo) = 'CAPITULO DE LIBRO'
      """)
  Optional<Libro> buscarCapituloPorProcesoYCodigo(
      @Param("idProceso") Integer idProceso,
      @Param("codigo") String codigo);
}