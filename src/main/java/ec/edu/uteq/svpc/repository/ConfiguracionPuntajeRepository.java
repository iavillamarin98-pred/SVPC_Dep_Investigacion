package ec.edu.uteq.svpc.repository;

import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConfiguracionPuntajeRepository extends JpaRepository<ConfiguracionPuntaje, Integer> {

        /*
         * Método usado por CalculoPuntajeArticuloService.
         * Se mantiene con Long porque artículos trabaja con Long idProceso.
         */
        @Query(value = """
                        SELECT *
                        FROM configuracion_puntaje
                        WHERE id_proceso = :idProceso
                          AND UPPER(categoria) = UPPER(:categoria)
                          AND UPPER(criterio) = UPPER(:criterio)
                          AND UPPER(rol) = UPPER(:rol)
                          AND estado = TRUE
                        LIMIT 1
                        """, nativeQuery = true)
        Optional<ConfiguracionPuntaje> findByIdProcesoAndCategoriaAndCriterioAndRolAndEstadoTrue(
                        @Param("idProceso") Long idProceso,
                        @Param("categoria") String categoria,
                        @Param("criterio") String criterio,
                        @Param("rol") String rol);

        /*
         * Método usado por CalculoPuntajeLibroService.
         * Se mantiene con Integer porque libros trabaja con Integer idProceso.
         */
        @Query(value = """
                        SELECT *
                        FROM configuracion_puntaje
                        WHERE id_proceso = :idProceso
                          AND UPPER(categoria) = UPPER(:categoria)
                          AND UPPER(criterio) = UPPER(:criterio)
                          AND UPPER(rol) = UPPER(:rol)
                          AND estado = TRUE
                        LIMIT 1
                        """, nativeQuery = true)
        Optional<ConfiguracionPuntaje> findByIdProcesoAndCategoriaIgnoreCaseAndCriterioIgnoreCaseAndRolIgnoreCaseAndEstadoTrue(
                        @Param("idProceso") Integer idProceso,
                        @Param("categoria") String categoria,
                        @Param("criterio") String criterio,
                        @Param("rol") String rol);

        /*
         * Método usado únicamente por CalculoPuntajeCapituloLibroService.
         * No afecta artículos ni libros.
         * Busca la configuración de puntaje para capítulos de libro.
         */
        @Query(value = """
                        SELECT *
                        FROM configuracion_puntaje
                        WHERE id_proceso = :idProceso
                          AND UPPER(categoria) = 'CAPITULO_LIBRO'
                          AND UPPER(criterio) = 'CAPITULO DE LIBRO'
                          AND UPPER(rol) = UPPER(:rol)
                          AND estado = TRUE
                        LIMIT 1
                        """, nativeQuery = true)
        Optional<ConfiguracionPuntaje> findConfiguracionCapituloLibro(
                        @Param("idProceso") Integer idProceso,
                        @Param("rol") String rol);

}