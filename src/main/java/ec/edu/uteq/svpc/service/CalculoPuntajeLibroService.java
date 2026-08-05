package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import ec.edu.uteq.svpc.entity.Libro;
import ec.edu.uteq.svpc.entity.LibroDocente;
import ec.edu.uteq.svpc.repository.ConfiguracionPuntajeRepository;
import ec.edu.uteq.svpc.repository.LibroDocenteRepository;
import ec.edu.uteq.svpc.repository.LibroRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CalculoPuntajeLibroService {

    private final LibroRepository libroRepository;
    private final LibroDocenteRepository libroDocenteRepository;
    private final ConfiguracionPuntajeRepository configuracionPuntajeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CalculoPuntajeLibroService(
            LibroRepository libroRepository,
            LibroDocenteRepository libroDocenteRepository,
            ConfiguracionPuntajeRepository configuracionPuntajeRepository) {
        this.libroRepository = libroRepository;
        this.libroDocenteRepository = libroDocenteRepository;
        this.configuracionPuntajeRepository = configuracionPuntajeRepository;
    }

    @Transactional
    public String calcularPuntajesLibros(Integer idProceso) {

        int librosAprobadosEncontrados = 0;
        int librosProcesados = 0;
        int relacionesProcesadas = 0;
        int librosSinRelaciones = 0;
        int librosSinConfiguracion = 0;

        libroDocenteRepository.reiniciarPuntajesPorProceso(idProceso);

        List<Libro> libros = libroRepository.findByIdProcesoAndEstadoIgnoreCase(idProceso, "APROBADO");

        librosAprobadosEncontrados = libros.size();

        for (Libro libro : libros) {

            List<LibroDocente> relaciones = libroDocenteRepository.findByLibro_IdLibro(libro.getIdLibro());

            if (relaciones.isEmpty()) {
                librosSinRelaciones++;
                continue;
            }

            String criterio = normalizarTipoLibro(libro.getTipo());

            BigDecimal puntajeBaseAutor = obtenerPuntajeBase(
                    idProceso,
                    criterio,
                    "AUTOR");

            BigDecimal puntajeBaseCoautor = obtenerPuntajeBase(
                    idProceso,
                    criterio,
                    "COAUTOR");

            if (puntajeBaseAutor == null || puntajeBaseCoautor == null) {
                librosSinConfiguracion++;
                continue;
            }

            long cantidadAutores = relaciones.stream()
                    .filter(relacion -> esAutor(relacion.getRolParticipante()))
                    .count();

            long cantidadCoautores = relaciones.stream()
                    .filter(relacion -> esCoautor(relacion.getRolParticipante()))
                    .count();

            for (LibroDocente relacion : relaciones) {

                BigDecimal puntajeCalculado = BigDecimal.ZERO;

                if (cantidadAutores > 0) {

                    if (esAutor(relacion.getRolParticipante())) {

                        if (cantidadCoautores == 0) {
                            puntajeCalculado = puntajeBaseAutor;
                        } else if (cantidadCoautores == 1) {
                            puntajeCalculado = puntajeBaseAutor.multiply(new BigDecimal("0.60"));
                        } else {
                            puntajeCalculado = puntajeBaseAutor.multiply(new BigDecimal("0.50"));
                        }

                    } else if (esCoautor(relacion.getRolParticipante())) {

                        if (cantidadCoautores == 1) {
                            puntajeCalculado = puntajeBaseAutor.multiply(new BigDecimal("0.40"));
                        } else if (cantidadCoautores >= 2) {
                            puntajeCalculado = puntajeBaseAutor
                                    .multiply(new BigDecimal("0.50"))
                                    .divide(
                                            BigDecimal.valueOf(cantidadCoautores),
                                            2,
                                            RoundingMode.HALF_UP);
                        }
                    }

                } else {

                    if (esCoautor(relacion.getRolParticipante()) && cantidadCoautores > 0) {
                        puntajeCalculado = puntajeBaseCoautor.divide(
                                BigDecimal.valueOf(cantidadCoautores),
                                2,
                                RoundingMode.HALF_UP);
                    }
                }

                relacion.setPuntajeObtenido(
                        puntajeCalculado.setScale(2, RoundingMode.HALF_UP));

                libroDocenteRepository.save(relacion);

                relacionesProcesadas++;
            }

            librosProcesados++;
        }

        consolidarPuntajesLibros(idProceso);

        return "Cálculo de puntajes de libros finalizado. " +
                "Libros aprobados encontrados: " + librosAprobadosEncontrados +
                ", libros procesados: " + librosProcesados +
                ", relaciones procesadas: " + relacionesProcesadas +
                ", libros sin relaciones: " + librosSinRelaciones +
                ", libros sin configuración: " + librosSinConfiguracion +
                ". Puntajes consolidados en valoraciones.puntaje_libros.";
    }

    private BigDecimal obtenerPuntajeBase(
            Integer idProceso,
            String criterio,
            String rol) {

        return configuracionPuntajeRepository
                .findByIdProcesoAndCategoriaIgnoreCaseAndCriterioIgnoreCaseAndRolIgnoreCaseAndEstadoTrue(
                        idProceso,
                        "LIBRO",
                        criterio,
                        rol)
                .map(ConfiguracionPuntaje::getPuntajeBase)
                .orElse(null);
    }

    private String normalizarTipoLibro(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            return "LIBRO";
        }

        String valor = tipo.trim().toUpperCase();

        if (valor.contains("CAP")) {
            return "CAPITULO DE LIBRO";
        }

        return "LIBRO";
    }

    private boolean esAutor(String rol) {

        if (rol == null) {
            return false;
        }

        return rol.trim().equalsIgnoreCase("AUTOR");
    }

    private boolean esCoautor(String rol) {

        if (rol == null) {
            return false;
        }

        return rol.trim().equalsIgnoreCase("COAUTOR");
    }

    private void consolidarPuntajesLibros(Integer idProceso) {

        entityManager.createNativeQuery("""
                INSERT INTO valoraciones (
                    id_proceso,
                    id_docente,
                    puntaje_articulos,
                    puntaje_libros,
                    puntaje_proyectos,
                    puntaje_total,
                    fecha_calculo
                )
                SELECT
                    l.id_proceso,
                    ld.id_docente,
                    0,
                    COALESCE(SUM(ld.puntaje_obtenido), 0),
                    0,
                    COALESCE(SUM(ld.puntaje_obtenido), 0),
                    CURRENT_TIMESTAMP
                FROM libro_docente ld
                INNER JOIN libros l ON l.id_libro = ld.id_libro
                WHERE l.id_proceso = :idProceso
                GROUP BY l.id_proceso, ld.id_docente
                ON CONFLICT (id_proceso, id_docente)
                DO UPDATE SET
                    puntaje_libros = EXCLUDED.puntaje_libros,
                    puntaje_total = COALESCE(valoraciones.puntaje_articulos, 0)
                                  + COALESCE(EXCLUDED.puntaje_libros, 0)
                                  + COALESCE(valoraciones.puntaje_proyectos, 0),
                    fecha_calculo = CURRENT_TIMESTAMP
                """)
                .setParameter("idProceso", idProceso)
                .executeUpdate();
    }
}