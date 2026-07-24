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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CalculoPuntajeCapituloLibroService {

    private final CapituloLibroRepository capituloLibroRepository;
    private final CapituloLibroDocenteRepository capituloLibroDocenteRepository;
    private final ConfiguracionPuntajeRepository configuracionPuntajeRepository;
    private final ValoracionCapituloLibroRepository valoracionCapituloLibroRepository;

    public CalculoPuntajeCapituloLibroService(
            CapituloLibroRepository capituloLibroRepository,
            CapituloLibroDocenteRepository capituloLibroDocenteRepository,
            ConfiguracionPuntajeRepository configuracionPuntajeRepository,
            ValoracionCapituloLibroRepository valoracionCapituloLibroRepository) {
        this.capituloLibroRepository = capituloLibroRepository;
        this.capituloLibroDocenteRepository = capituloLibroDocenteRepository;
        this.configuracionPuntajeRepository = configuracionPuntajeRepository;
        this.valoracionCapituloLibroRepository = valoracionCapituloLibroRepository;
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

                BigDecimal puntaje = calcularPuntaje(
                        rol,
                        cantidadAutores,
                        cantidadCoautores,
                        puntajeBaseAutor,
                        puntajeBaseCoautor);

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

    private BigDecimal calcularPuntaje(
            String rol,
            long cantidadAutores,
            long cantidadCoautores,
            BigDecimal puntajeBaseAutor,
            BigDecimal puntajeBaseCoautor) {
        BigDecimal puntaje = BigDecimal.ZERO;

        if (cantidadAutores > 0) {

            if (rol.equals("AUTOR")) {

                if (cantidadCoautores == 0) {
                    puntaje = puntajeBaseAutor;
                } else if (cantidadCoautores == 1) {
                    puntaje = puntajeBaseAutor.multiply(new BigDecimal("0.60"));
                } else {
                    puntaje = puntajeBaseAutor.multiply(new BigDecimal("0.50"));
                }

            } else if (rol.equals("COAUTOR")) {

                if (cantidadCoautores == 1) {
                    puntaje = puntajeBaseAutor.multiply(new BigDecimal("0.40"));
                } else if (cantidadCoautores > 1) {
                    puntaje = puntajeBaseAutor
                            .multiply(new BigDecimal("0.50"))
                            .divide(BigDecimal.valueOf(cantidadCoautores), 2, RoundingMode.HALF_UP);
                }
            }

        } else {

            if (rol.equals("COAUTOR") && cantidadCoautores > 0) {
                puntaje = puntajeBaseCoautor
                        .divide(BigDecimal.valueOf(cantidadCoautores), 2, RoundingMode.HALF_UP);
            }
        }

        return puntaje.setScale(2, RoundingMode.HALF_UP);
    }

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