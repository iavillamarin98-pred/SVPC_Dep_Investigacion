package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import ec.edu.uteq.svpc.entity.Libro;
import ec.edu.uteq.svpc.entity.LibroDocente;
import ec.edu.uteq.svpc.repository.CapituloLibroDocenteRepository;
import ec.edu.uteq.svpc.repository.CapituloLibroRepository;
import ec.edu.uteq.svpc.repository.ConfiguracionPuntajeRepository;
import ec.edu.uteq.svpc.repository.ValoracionCapituloLibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import ec.edu.uteq.svpc.repository.ReglaDistribucionAutoriaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CalculoPuntajeCapituloLibroService {

    private final CapituloLibroRepository capituloLibroRepository;
    private final CapituloLibroDocenteRepository capituloLibroDocenteRepository;
    private final ConfiguracionPuntajeRepository configuracionPuntajeRepository;
    private final ValoracionCapituloLibroRepository valoracionCapituloLibroRepository;
    private final ReglaDistribucionAutoriaRepository reglaDistribucionAutoriaRepository;

    public CalculoPuntajeCapituloLibroService(
            CapituloLibroRepository capituloLibroRepository,
            CapituloLibroDocenteRepository capituloLibroDocenteRepository,
            ConfiguracionPuntajeRepository configuracionPuntajeRepository,
            ValoracionCapituloLibroRepository valoracionCapituloLibroRepository,
            ReglaDistribucionAutoriaRepository reglaDistribucionAutoriaRepository) {
        this.capituloLibroRepository = capituloLibroRepository;
        this.capituloLibroDocenteRepository = capituloLibroDocenteRepository;
        this.configuracionPuntajeRepository = configuracionPuntajeRepository;
        this.valoracionCapituloLibroRepository = valoracionCapituloLibroRepository;
        this.reglaDistribucionAutoriaRepository = reglaDistribucionAutoriaRepository;
    }

    @Transactional
    public String calcularPuntajesCapitulosLibro(Integer idProceso) {

        List<Libro> capitulos = capituloLibroRepository.buscarCapitulosPorProceso(idProceso);

        BigDecimal puntajeBaseAutor = obtenerPuntajeBaseCapituloLibro(
                idProceso,
                "AUTOR");

        BigDecimal puntajeBaseCoautor = obtenerPuntajeBaseCapituloLibro(
                idProceso,
                "COAUTOR");

        if (puntajeBaseAutor == null || puntajeBaseCoautor == null) {
            return "No existe configuración para CAPITULO_LIBRO. Revise configuracion_puntaje.";
        }

        int capitulosProcesados = 0;
        int relacionesProcesadas = 0;

        for (Libro capitulo : capitulos) {

            List<LibroDocente> participantes = capituloLibroDocenteRepository.findByIdIdLibro(capitulo.getIdLibro());

            if (participantes.isEmpty()) {
                continue;
            }

            long cantidadAutores = participantes.stream()
                    .filter(p -> normalizarRol(p.getRolParticipante()).equals("AUTOR"))
                    .count();

            long cantidadCoautores = participantes.stream()
                    .filter(p -> normalizarRol(p.getRolParticipante()).equals("COAUTOR"))
                    .count();

            for (LibroDocente participante : participantes) {

                String rol = normalizarRol(participante.getRolParticipante());

                String escenario = determinarEscenario(
                        cantidadAutores,
                        cantidadCoautores);

                ReglaDistribucionAutoria regla = obtenerReglaDistribucion(
                        idProceso,
                        escenario);

                BigDecimal puntaje;

                if (regla == null) {

                    puntaje = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

                } else {

                    puntaje = calcularPuntaje(
                            rol,
                            puntajeBaseAutor,
                            puntajeBaseCoautor,
                            regla,
                            cantidadCoautores);
                }

                participante.setPuntajeObtenido(puntaje);
                capituloLibroDocenteRepository.save(participante);

                relacionesProcesadas++;
            }

            capitulosProcesados++;
        }

        consolidarCapitulosEnValoraciones(idProceso);

        return "Cálculo de capítulos de libro finalizado. " +
                "Capítulos procesados: " + capitulosProcesados +
                ", relaciones procesadas: " + relacionesProcesadas + ".";
    }
/*------------------------------------------- */
    private BigDecimal calcularPuntaje(
        String rol,
        BigDecimal puntajeBaseAutor,
        BigDecimal puntajeBaseCoautor,
        ReglaDistribucionAutoria regla,
        long cantidadCoautores) {

    if ("AUTOR".equals(rol)) {

        return puntajeBaseAutor
                .multiply(regla.getPorcentajeAutor())
                .divide(
                        new BigDecimal("100"),
                        10,
                        RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    if ("COAUTOR".equals(rol)) {

        /*
         * Si existe porcentaje de coautor,
         * se aplica sobre el puntaje base del autor.
         */
        if (regla.getPorcentajeCoautor()
                .compareTo(BigDecimal.ZERO) > 0) {

            return puntajeBaseAutor
                    .multiply(regla.getPorcentajeCoautor())
                    .divide(
                            new BigDecimal("100"),
                            10,
                            RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        /*
         * Si no existe autor, el puntaje base
         * corresponde al conjunto de coautores.
         */
        if (cantidadCoautores > 0) {

            return puntajeBaseCoautor
                    .divide(
                            BigDecimal.valueOf(cantidadCoautores),
                            10,
                            RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
}

    /*------------------------------------------- */

    private String determinarEscenario(
            long cantidadAutores,
            long cantidadCoautores) {

        long totalParticipantes = cantidadAutores + cantidadCoautores;

        if (totalParticipantes <= 0) {
            return null;
        }

        return String.valueOf(
                Math.min(totalParticipantes, 10));
    }


    private ReglaDistribucionAutoria obtenerReglaDistribucion(
            Integer idProceso,
            String escenario) {

        if (escenario == null) {
            return null;
        }

        return reglaDistribucionAutoriaRepository
                .findByIdProcesoAndEscenarioAndEstadoTrue(
                        idProceso,
                        escenario)
                .orElse(null);
    }

    /*------------------------------------------- */
    private void consolidarCapitulosEnValoraciones(Integer idProceso) {

        List<Object[]> resultados = capituloLibroDocenteRepository.obtenerPuntajeCapitulosPorDocente(idProceso);

        for (Object[] fila : resultados) {

            Integer idDocente = ((Number) fila[0]).intValue();
            BigDecimal puntajeCapitulos = (BigDecimal) fila[1];

            valoracionCapituloLibroRepository.insertarSiNoExiste(idProceso, idDocente);

            valoracionCapituloLibroRepository.actualizarPuntajeCapitulos(
                    idProceso,
                    idDocente,
                    puntajeCapitulos);
        }
    }

    private BigDecimal obtenerPuntajeBaseCapituloLibro(
            Integer idProceso,
            String rol) {
        return configuracionPuntajeRepository
                .findConfiguracionCapituloLibro(idProceso, rol)
                .map(ConfiguracionPuntaje::getPuntajeBase)
                .orElse(null);
    }

    private String normalizarRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return "COAUTOR";
        }

        String valor = rol.trim().toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U");

        if (valor.equals("AUTOR")) {
            return "AUTOR";
        }

        return "COAUTOR";
    }
}