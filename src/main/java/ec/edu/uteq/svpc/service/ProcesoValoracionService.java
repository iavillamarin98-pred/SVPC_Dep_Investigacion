package ec.edu.uteq.svpc.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.uteq.svpc.dto.ProcesoValoracionDTO;
import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import ec.edu.uteq.svpc.entity.ProcesoValoracion;
import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import ec.edu.uteq.svpc.repository.ConfiguracionPuntajeRepository;
import ec.edu.uteq.svpc.repository.ProcesoValoracionRepository;
import ec.edu.uteq.svpc.repository.ReglaDistribucionAutoriaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProcesoValoracionService {

    private final ProcesoValoracionRepository repository;
    private final ConfiguracionPuntajeRepository configuracionRepository;
    private final ReglaDistribucionAutoriaRepository reglaRepository;

    public ProcesoValoracionService(
            ProcesoValoracionRepository repository,
            ConfiguracionPuntajeRepository configuracionRepository,
            ReglaDistribucionAutoriaRepository reglaRepository) {

        this.repository = repository;
        this.configuracionRepository = configuracionRepository;
        this.reglaRepository = reglaRepository;
    }

    public List<ProcesoValoracionDTO> listar() {

        return repository.findAll()
                .stream()
                .map(this::convertirDTO)
                .toList();
    }

    private ProcesoValoracionDTO convertirDTO(
            ProcesoValoracion proceso) {

        ProcesoValoracionDTO dto = new ProcesoValoracionDTO();

        dto.setIdProceso(proceso.getIdProceso());
        dto.setNombre(proceso.getNombre());
        dto.setDescripcion(proceso.getDescripcion());
        dto.setPeriodo(proceso.getPeriodo());
        dto.setEstado(proceso.getEstado());
        dto.setFechaCreacion(proceso.getFechaCreacion());

        return dto;
    }

    public ProcesoValoracionDTO obtener(Integer id) {

        ProcesoValoracion proceso = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        return convertirDTO(proceso);
    }

    public ProcesoValoracionDTO guardar(
            ProcesoValoracionDTO dto) {

        if (repository.existsByNombreAndPeriodo(
                dto.getNombre(),
                dto.getPeriodo())) {

            throw new RuntimeException(
                    "Ya existe un proceso con ese nombre y período.");
        }

        ProcesoValoracion proceso = new ProcesoValoracion();

        proceso.setNombre(dto.getNombre());
        proceso.setDescripcion(dto.getDescripcion());
        proceso.setPeriodo(dto.getPeriodo());
        proceso.setEstado(dto.getEstado());
        proceso.setFechaCreacion(LocalDateTime.now());
        proceso.setIdUsuario(1);

        /*
         * Primero guardamos el proceso para obtener
         * el ID generado por PostgreSQL.
         */
        proceso = repository.save(proceso);
        crearConfiguracionPredeterminada(proceso.getIdProceso().longValue());
        return convertirDTO(proceso);
    }

    private void crearConfiguracionPredeterminada(Long idProceso) {

        // ==================================================
        // ARTÍCULOS
        // ==================================================

        crearPuntaje(idProceso, "ARTICULO", "Q1", "AUTOR", "10");
        crearPuntaje(idProceso, "ARTICULO", "Q1", "COAUTOR", "7");

        crearPuntaje(idProceso, "ARTICULO", "Q2", "AUTOR", "9");
        crearPuntaje(idProceso, "ARTICULO", "Q2", "COAUTOR", "6");

        crearPuntaje(idProceso, "ARTICULO", "Q3", "AUTOR", "8");
        crearPuntaje(idProceso, "ARTICULO", "Q3", "COAUTOR", "5");

        crearPuntaje(idProceso, "ARTICULO", "Q4", "AUTOR", "7");
        crearPuntaje(idProceso, "ARTICULO", "Q4", "COAUTOR", "4");

        crearPuntaje(idProceso, "ARTICULO", "Q0", "AUTOR", "6");
        crearPuntaje(idProceso, "ARTICULO", "Q0", "COAUTOR", "3");

        crearPuntaje(idProceso, "ARTICULO", "REGIONAL", "AUTOR", "2");
        crearPuntaje(idProceso, "ARTICULO", "REGIONAL", "COAUTOR", "1");

        // ==================================================
        // PROCEEDINGS
        // ==================================================

        crearPuntaje(
                idProceso,
                "ARTICULO",
                "PROCEEDING",
                "AUTOR",
                "7");

        crearPuntaje(
                idProceso,
                "ARTICULO",
                "PROCEEDING",
                "COAUTOR",
                "4");

        // ==================================================
        // LIBROS
        // ==================================================

        crearPuntaje(
                idProceso,
                "LIBRO",
                "LIBRO",
                "AUTOR",
                "8");

        crearPuntaje(
                idProceso,
                "LIBRO",
                "LIBRO",
                "COAUTOR",
                "5");

        // ==================================================
        // CAPÍTULOS DE LIBRO
        // ==================================================

        crearPuntaje(
                idProceso,
                "CAPITULO_LIBRO",
                "CAPITULO DE LIBRO",
                "AUTOR",
                "5");

        crearPuntaje(
                idProceso,
                "CAPITULO_LIBRO",
                "CAPITULO DE LIBRO",
                "COAUTOR",
                "3");

        // ==================================================
        // PROYECTOS DE INVESTIGACIÓN
        // ==================================================

        // Financiamiento externo
        crearPuntaje(
                idProceso,
                "PROYECTO",
                "EXTERNO",
                "DIRECTOR",
                "15");

        crearPuntaje(
                idProceso,
                "PROYECTO",
                "EXTERNO",
                "INTEGRANTE",
                "7");

        // Financiamiento conjunto
        crearPuntaje(
                idProceso,
                "PROYECTO",
                "CONJUNTO",
                "DIRECTOR",
                "10");

        crearPuntaje(
                idProceso,
                "PROYECTO",
                "CONJUNTO",
                "INTEGRANTE",
                "5");

        // Financiamiento interno
        crearPuntaje(
                idProceso,
                "PROYECTO",
                "INTERNO",
                "DIRECTOR",
                "7");

        crearPuntaje(
                idProceso,
                "PROYECTO",
                "INTERNO",
                "INTEGRANTE",
                "3");

        // ==================================================
        // REGLAS DE DISTRIBUCIÓN DE AUTORÍA
        // ==================================================

        crearRegla(
                idProceso,
                "1",
                "100",
                "0");

        crearRegla(
                idProceso,
                "2",
                "60",
                "40");

        crearRegla(
                idProceso,
                "3",
                "50",
                "25");

        crearRegla(
                idProceso,
                "4",
                "50",
                "16.67");

        crearRegla(
                idProceso,
                "5",
                "0",
                "100");

        crearRegla(
                idProceso,
                "6",
                "0",
                "50");

        crearRegla(
                idProceso,
                "7",
                "0",
                "33.33");

        crearRegla(
                idProceso,
                "8",
                "0",
                "25");

        crearRegla(
                idProceso,
                "9",
                "20",
                "10");

        crearRegla(
                idProceso,
                "10",
                "20",
                "8.89");
    }

    private void crearPuntaje(
            Long idProceso,
            String categoria,
            String criterio,
            String rol,
            String puntaje) {

        ConfiguracionPuntaje config = new ConfiguracionPuntaje();

        config.setIdProceso(idProceso);
        config.setCategoria(categoria);
        config.setCriterio(criterio);
        config.setRol(rol);
        config.setPuntajeBase(new BigDecimal(puntaje));
        config.setEstado(true);

        configuracionRepository.save(config);
    }

    private void crearRegla(
            Long idProceso,
            String escenario,
            String autor,
            String coautor) {

        ReglaDistribucionAutoria regla = new ReglaDistribucionAutoria();

        regla.setIdProceso(idProceso.intValue());
        regla.setEscenario(escenario);
        regla.setPorcentajeAutor(new BigDecimal(autor));
        regla.setPorcentajeCoautor(new BigDecimal(coautor));
        regla.setEstado(true);

        reglaRepository.save(regla);
    }

    public ProcesoValoracionDTO actualizar(
            Integer id,
            ProcesoValoracionDTO dto) {

        ProcesoValoracion proceso = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Proceso no encontrado."));

        proceso.setNombre(dto.getNombre());
        proceso.setDescripcion(dto.getDescripcion());
        proceso.setPeriodo(dto.getPeriodo());
        proceso.setEstado(dto.getEstado());

        repository.save(proceso);

        return convertirDTO(proceso);
    }

    public void eliminar(Integer id) {

        if (!repository.existsById(id)) {

            throw new RuntimeException(
                    "Proceso no encontrado.");
        }

        repository.deleteById(id);
    }

    public void activarProceso(Integer idProceso) {

        repository.findFirstByEstado("ACTIVO")
                .ifPresent(proceso -> {

                    proceso.setEstado("INACTIVO");

                    repository.save(proceso);
                });

        ProcesoValoracion proceso = repository.findById(idProceso)
                .orElseThrow(() -> new RuntimeException(
                        "Proceso no encontrado."));

        proceso.setEstado("ACTIVO");

        repository.save(proceso);
    }

    public ProcesoValoracionDTO obtenerProcesoActivo() {

        return repository.findFirstByEstado("ACTIVO")
                .map(this::convertirDTO)
                .orElse(null);
    }
}