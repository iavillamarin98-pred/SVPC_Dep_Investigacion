package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Articulo;
import ec.edu.uteq.svpc.entity.ArticuloDocente;
import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import ec.edu.uteq.svpc.repository.ArticuloDocenteRepository;
import ec.edu.uteq.svpc.repository.ArticuloRepository;
import ec.edu.uteq.svpc.repository.ConfiguracionPuntajeRepository;
import ec.edu.uteq.svpc.repository.ReglaDistribucionAutoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import ec.edu.uteq.svpc.repository.ReglaDistribucionAutoriaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class CalculoPuntajeArticuloService {

    private static final String CATEGORIA_ARTICULO = "ARTICULO";
    private static final String ROL_AUTOR = "AUTOR";
    private static final String ROL_COAUTOR = "COAUTOR";

    private final ArticuloRepository articuloRepository;
    private final ArticuloDocenteRepository articuloDocenteRepository;
    private final ConfiguracionPuntajeRepository configuracionPuntajeRepository;
    private final ReglaDistribucionAutoriaRepository reglaDistribucionAutoriaRepository;

    private final JdbcTemplate jdbcTemplate;

    public CalculoPuntajeArticuloService(
            ArticuloRepository articuloRepository,
            ArticuloDocenteRepository articuloDocenteRepository,
            ConfiguracionPuntajeRepository configuracionPuntajeRepository,
            ReglaDistribucionAutoriaRepository reglaDistribucionAutoriaRepository,
            JdbcTemplate jdbcTemplate) {
        this.articuloRepository = articuloRepository;
        this.articuloDocenteRepository = articuloDocenteRepository;
        this.configuracionPuntajeRepository = configuracionPuntajeRepository;
        this.reglaDistribucionAutoriaRepository = reglaDistribucionAutoriaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String calcularPuntajesArticulos(Long idProceso) {

        List<Articulo> articulos = articuloRepository.buscarArticulosAprobadosPorProceso(idProceso);

        int articulosProcesados = 0;
        int relacionesProcesadas = 0;
        int articulosSinRelaciones = 0;
        int articulosSinCriterio = 0;
        int articulosSinConfiguracion = 0;

        for (Articulo articulo : articulos) {

            List<ArticuloDocente> relaciones = articuloDocenteRepository
                    .findByIdIdArticulo(articulo.getIdArticulo());

            if (relaciones.isEmpty()) {
                articulosSinRelaciones++;
                continue;
            }

            String criterio = determinarCriterioArticulo(articulo);

            if (criterio == null) {
                ponerPuntajeCero(relaciones);
                articulosSinCriterio++;
                articulosProcesados++;
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
                articulosSinConfiguracion++;
                articulosProcesados++;
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
                        cantidadCoautores,
                        idProceso.intValue());

                relacion.setPuntajeObtenido(puntajeCalculado);
                articuloDocenteRepository.save(relacion);
                relacionesProcesadas++;
            }

            articulosProcesados++;
        }

        consolidarPuntajeArticulosEnValoraciones(idProceso);

        return "Cálculo de puntajes de artículos finalizado. " +
                "Artículos aprobados encontrados: " + articulos.size() +
                ", artículos procesados: " + articulosProcesados +
                ", relaciones procesadas: " + relacionesProcesadas +
                ", artículos sin relaciones: " + articulosSinRelaciones +
                ", artículos sin criterio válido: " + articulosSinCriterio +
                ", artículos sin configuración: " + articulosSinConfiguracion +
                ". Puntajes consolidados en valoraciones.puntaje_articulos.";
    }

    /* ------------------------------------------------- */
    private BigDecimal calcularPuntajeDistribuido(
            ArticuloDocente relacion,
            BigDecimal puntajeBaseAutor,
            BigDecimal puntajeBaseCoautor,
            long cantidadAutores,
            long cantidadCoautores,
            Integer idProceso) {

        boolean esAutor = esAutor(relacion.getRolParticipante());
        boolean esCoautor = esCoautor(relacion.getRolParticipante());

        if (!esAutor && !esCoautor) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        String escenario = determinarEscenario(
                cantidadAutores,
                cantidadCoautores);

        if (escenario == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        ReglaDistribucionAutoria regla = obtenerReglaDistribucion(idProceso, escenario);

        if (regla == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal porcentaje;

        /*
         * ESCENARIOS CON AUTOR
         *
         * El porcentaje se aplica sobre el puntaje base del AUTOR.
         */
        if (cantidadAutores > 0) {

            if (esAutor) {

                porcentaje = regla.getPorcentajeAutor();

                return redondear(
                        puntajeBaseAutor
                                .multiply(porcentaje)
                                .divide(
                                        new BigDecimal("100"),
                                        10,
                                        RoundingMode.HALF_UP));
            }

            if (esCoautor) {

                porcentaje = regla.getPorcentajeCoautor();

                return redondear(
                        puntajeBaseAutor
                                .multiply(porcentaje)
                                .divide(
                                        new BigDecimal("100"),
                                        10,
                                        RoundingMode.HALF_UP));
            }
        }

        /*
         * ESCENARIOS SOLO COAUTORES
         *
         * Aquí el porcentaje se aplica sobre el puntaje
         * base del COAUTOR.
         *
         * Ejemplo:
         *
         * Q3 = 5 puntos
         * 2 coautores
         * escenario 6 = 50%
         *
         * 5 × 50% = 2.50
         */
        if (cantidadAutores == 0 && cantidadCoautores > 0) {

            if (esCoautor) {

                porcentaje = regla.getPorcentajeCoautor();

                return redondear(
                        puntajeBaseCoautor
                                .multiply(porcentaje)
                                .divide(
                                        new BigDecimal("100"),
                                        10,
                                        RoundingMode.HALF_UP));
            }
        }

        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    /* --------------------------------------------------------- */
    private ReglaDistribucionAutoria obtenerReglaDistribucion(
            Integer idProceso,
            String escenario) {

        return reglaDistribucionAutoriaRepository
                .findByIdProcesoAndEscenarioAndEstadoTrue(
                        idProceso,
                        escenario)
                .orElse(null);
    }

    private String determinarEscenario(
            long cantidadAutores,
            long cantidadCoautores) {

        /*
         * AUTOR + COAUTORES
         */
        if (cantidadAutores > 0) {

            if (cantidadCoautores == 0) {
                return "1";
            }

            if (cantidadCoautores == 1) {
                return "2";
            }

            if (cantidadCoautores == 2) {
                return "3";
            }

            if (cantidadCoautores == 3) {
                return "4";
            }
        }

        /*
         * SOLO COAUTORES
         */
        if (cantidadAutores == 0) {

            if (cantidadCoautores == 1) {
                return "5";
            }

            if (cantidadCoautores == 2) {
                return "6";
            }

            if (cantidadCoautores == 3) {
                return "7";
            }

            if (cantidadCoautores >= 4) {
                return "8";
            }
        }

        return null;
    }

    /* --------------------------------------------------------- */
    private void consolidarPuntajeArticulosEnValoraciones(Long idProceso) {

        List<Object[]> puntajesPorDocente = articuloDocenteRepository
                .sumarPuntajeArticulosPorDocente(idProceso);

        for (Object[] fila : puntajesPorDocente) {

            Long idDocente = ((Number) fila[0]).longValue();
            BigDecimal puntajeArticulos = (BigDecimal) fila[1];

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
                                SET puntaje_articulos = ?
                                WHERE id_proceso = ?
                                AND id_docente = ?
                                """,
                        puntajeArticulos,
                        idProceso,
                        idDocente);
            } else {
                jdbcTemplate.update(
                        """
                                INSERT INTO valoraciones
                                (id_proceso, id_docente, puntaje_articulos)
                                VALUES (?, ?, ?)
                                """,
                        idProceso,
                        idDocente,
                        puntajeArticulos);
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