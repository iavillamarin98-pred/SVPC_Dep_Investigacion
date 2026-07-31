package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Articulo;
import ec.edu.uteq.svpc.entity.ArticuloDocente;
import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import ec.edu.uteq.svpc.repository.ArticuloDocenteRepository;
import ec.edu.uteq.svpc.repository.ArticuloRepository;
import ec.edu.uteq.svpc.repository.ConfiguracionPuntajeRepository;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class CalculoPuntajeProceedingService {

    private static final String CATEGORIA_ARTICULO = "ARTICULO";
    private static final String ROL_AUTOR = "AUTOR";
    private static final String ROL_COAUTOR = "COAUTOR";

    private final ArticuloRepository articuloRepository;
    private final ArticuloDocenteRepository articuloDocenteRepository;
    private final ConfiguracionPuntajeRepository configuracionPuntajeRepository;
    private final JdbcTemplate jdbcTemplate;

    public CalculoPuntajeProceedingService(
            ArticuloRepository articuloRepository,
            ArticuloDocenteRepository articuloDocenteRepository,
            ConfiguracionPuntajeRepository configuracionPuntajeRepository,
            JdbcTemplate jdbcTemplate) {

        this.articuloRepository = articuloRepository;
        this.articuloDocenteRepository = articuloDocenteRepository;
        this.configuracionPuntajeRepository = configuracionPuntajeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String calcularPuntajesProceedings(Long idProceso) {

        List<Articulo> proceedings = articuloRepository.buscarProceedingsAprobadosPorProceso(idProceso);

        int proceedingsProcesados = 0;
        int relacionesProcesadas = 0;
        int proceedingsSinRelaciones = 0;
        int proceedingsSinCriterio = 0;
        int proceedingsSinConfiguracion = 0;

        for (Articulo proceeding : proceedings) {

            List<ArticuloDocente> relaciones = articuloDocenteRepository
                    .findByIdIdArticulo(proceeding.getIdArticulo());

            if (relaciones.isEmpty()) {
                proceedingsSinRelaciones++;
                continue;
            }

            String criterio = determinarCriterioArticulo(proceeding);

            if (criterio == null) {
                ponerPuntajeCero(relaciones);
                proceedingsSinCriterio++;
                proceedingsProcesados++;
                continue;
            }

            BigDecimal puntajeBaseAutor = obtenerPuntajeBase(
                    idProceso,
                    criterio,
                    ROL_AUTOR);

            BigDecimal puntajeBaseCoautor = obtenerPuntajeBase(
                    idProceso,
                    criterio,
                    ROL_COAUTOR);

            if (puntajeBaseAutor == null || puntajeBaseCoautor == null) {
                ponerPuntajeCero(relaciones);
                proceedingsSinConfiguracion++;
                proceedingsProcesados++;
                continue;
            }

            long cantidadAutores = relaciones.stream()
                    .filter(r -> esAutor(r.getRolParticipante()))
                    .count();

            long cantidadCoautores = relaciones.stream()
                    .filter(r -> esCoautor(r.getRolParticipante()))
                    .count();

            for (ArticuloDocente relacion : relaciones) {

                BigDecimal puntajeCalculado = calcularPuntajeDistribuido(
                        relacion,
                        puntajeBaseAutor,
                        puntajeBaseCoautor,
                        cantidadAutores,
                        cantidadCoautores);

                relacion.setPuntajeObtenido(puntajeCalculado);
                articuloDocenteRepository.save(relacion);
                relacionesProcesadas++;
            }

            proceedingsProcesados++;
        }

        consolidarPuntajeProceedingsEnValoraciones(idProceso);

        return "Cálculo de puntajes de proceedings finalizado. " +
                "Proceedings aprobados encontrados: " + proceedings.size() +
                ", proceedings procesados: " + proceedingsProcesados +
                ", relaciones procesadas: " + relacionesProcesadas +
                ", proceedings sin relaciones: " + proceedingsSinRelaciones +
                ", proceedings sin criterio válido: " + proceedingsSinCriterio +
                ", proceedings sin configuración: " + proceedingsSinConfiguracion +
                ". Puntajes consolidados en valoraciones.puntaje_proceedings.";
    }

    private BigDecimal calcularPuntajeDistribuido(
            ArticuloDocente relacion,
            BigDecimal puntajeBaseAutor,
            BigDecimal puntajeBaseCoautor,
            long cantidadAutores,
            long cantidadCoautores) {

        boolean esAutor = esAutor(relacion.getRolParticipante());
        boolean esCoautor = esCoautor(relacion.getRolParticipante());

        if (!esAutor && !esCoautor) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        /*
         * Escenario 1:
         * Autor UTEQ solo.
         * Recibe 100% del puntaje base de autor.
         */
        if (cantidadAutores >= 1 && cantidadCoautores == 0) {
            if (esAutor) {
                return redondear(puntajeBaseAutor);
            }
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        /*
         * Escenario 2:
         * Autor UTEQ + 1 coautor UTEQ.
         * Autor recibe 60%.
         * Coautor recibe 40%.
         */
        if (cantidadAutores >= 1 && cantidadCoautores == 1) {
            if (esAutor) {
                return redondear(puntajeBaseAutor.multiply(new BigDecimal("0.60")));
            }

            if (esCoautor) {
                return redondear(puntajeBaseAutor.multiply(new BigDecimal("0.40")));
            }
        }

        /*
         * Escenario 3:
         * Autor UTEQ + 2 o más coautores UTEQ.
         * Autor recibe 50%.
         * El otro 50% se divide entre coautores.
         */
        if (cantidadAutores >= 1 && cantidadCoautores >= 2) {
            if (esAutor) {
                return redondear(puntajeBaseAutor.multiply(new BigDecimal("0.50")));
            }

            if (esCoautor) {
                return redondear(
                        puntajeBaseAutor
                                .multiply(new BigDecimal("0.50"))
                                .divide(BigDecimal.valueOf(cantidadCoautores), 10, RoundingMode.HALF_UP));
            }
        }

        /*
         * Escenario 4:
         * Solo coautores UTEQ.
         * El puntaje base de coautor se divide entre todos los coautores.
         */
        if (cantidadAutores == 0 && cantidadCoautores > 0) {
            if (esCoautor) {
                return redondear(
                        puntajeBaseCoautor.divide(
                                BigDecimal.valueOf(cantidadCoautores),
                                10,
                                RoundingMode.HALF_UP));
            }
        }

        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private void consolidarPuntajeProceedingsEnValoraciones(Long idProceso) {

        List<Object[]> puntajesPorDocente = articuloDocenteRepository
                .sumarPuntajeProceedingsPorDocente(idProceso);

        for (Object[] fila : puntajesPorDocente) {

            Long idDocente = ((Number) fila[0]).longValue();
            BigDecimal puntajeProceedings = (BigDecimal) fila[1];

            Integer existe = jdbcTemplate.queryForObject(
                    """
                            SELECT COUNT(*)
                            FROM valoraciones
                            WHERE id_proceso = ?
                              AND id_docente = ?
                            """,
                    Integer.class,
                    idProceso,
                    idDocente);

            if (existe != null && existe > 0) {

                jdbcTemplate.update(
                        """
                                UPDATE valoraciones
                                SET puntaje_proceedings = ?
                                WHERE id_proceso = ?
                                  AND id_docente = ?
                                """,
                        puntajeProceedings,
                        idProceso,
                        idDocente);

            } else {

                jdbcTemplate.update(
                        """
                                INSERT INTO valoraciones
                                (
                                    id_proceso,
                                    id_docente,
                                    puntaje_proceedings
                                )
                                VALUES (?, ?, ?)
                                """,
                        idProceso,
                        idDocente,
                        puntajeProceedings);

            }
        }
    }

    private void ponerPuntajeCero(List<ArticuloDocente> relaciones) {
        for (ArticuloDocente relacion : relaciones) {
            relacion.setPuntajeObtenido(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            articuloDocenteRepository.save(relacion);
        }
    }

    private BigDecimal obtenerPuntajeBase(Long idProceso, String criterio, String rol) {
        return configuracionPuntajeRepository
                .findByIdProcesoAndCategoriaAndCriterioAndRolAndEstadoTrue(
                        idProceso,
                        CATEGORIA_ARTICULO,
                        criterio,
                        rol)
                .map(ConfiguracionPuntaje::getPuntajeBase)
                .orElse(null);
    }

    private String determinarCriterioArticulo(Articulo articulo) {

        String base = normalizarTexto(articulo.getBaseIndexada());
        String cuartil = normalizarCuartil(articulo.getCuartil());

        /*
         * 1. Proceedings.
         * Se identifican primero porque utilizan una configuración propia.
         */
        if (base.contains("PROCEEDING")) {
            return "PROCEEDING";
        }

        /*
         * 2. Bases regionales.
         */
        if (esBaseRegional(base)) {
            return "REGIONAL";
        }

        /*
         * 3. Web of Science.
         * Con cuartil -> Q1..Q4
         * Sin cuartil -> Q0
         */
        if (base.contains("WEB OF SCIENCE")
                || base.equals("WOS")
                || base.contains("WOS")) {

            if (esCuartilValido(cuartil)) {
                return cuartil;
            }

            return "Q0";
        }

        /*
         * 4. Scopus.
         */
        if (base.contains("SCOPUS")) {

            if (esCuartilValido(cuartil)) {
                return cuartil;
            }

            return "Q0";
        }

        return null;
    }

    private boolean esBaseMundial(String base) {
        return base.contains("SCOPUS")
                || base.contains("WEB OF SCIENCE")
                || base.equals("WOS")
                || base.contains("WOS ");
    }

    private boolean esBaseRegional(String base) {
        return base.contains("LATINDEX CATALOGO 2.0")
                || base.equals("LATINDEX")
                || base.contains("SCIELO")
                || base.contains("DOAJ")
                || base.contains("ERIHPLUS")
                || base.contains("REDALYC")
                || base.contains("OAJI")
                || base.contains("EBSCO");
    }

    private boolean esCuartilValido(String cuartil) {
        return "Q1".equals(cuartil)
                || "Q2".equals(cuartil)
                || "Q3".equals(cuartil)
                || "Q4".equals(cuartil);
    }

    private String normalizarCuartil(String cuartil) {
        if (cuartil == null || cuartil.isBlank()) {
            return "";
        }

        String valor = normalizarTexto(cuartil);

        return switch (valor) {
            case "NAQ1" -> "Q1";
            case "NAQ2" -> "Q2";
            case "NAQ3" -> "Q3";
            case "NAQ4" -> "Q4";
            case "Q1" -> "Q1";
            case "Q2" -> "Q2";
            case "Q3" -> "Q3";
            case "Q4" -> "Q4";
            case "Q0" -> "Q0";
            case "NO APLICA" -> "NO APLICA";
            default -> valor;
        };
    }

    private boolean esAutor(String rol) {
        return ROL_AUTOR.equals(normalizarTexto(rol));
    }

    private boolean esCoautor(String rol) {
        return ROL_COAUTOR.equals(normalizarTexto(rol));
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return sinAcentos
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace(",", "")
                .replace(".", ".")
                .replaceAll("\\s+", " ");
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

}
