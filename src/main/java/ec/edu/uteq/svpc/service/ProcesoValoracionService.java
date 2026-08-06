package ec.edu.uteq.svpc.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.uteq.svpc.dto.ProcesoValoracionDTO;
import ec.edu.uteq.svpc.entity.ProcesoValoracion;
import ec.edu.uteq.svpc.repository.ProcesoValoracionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProcesoValoracionService {

    private final ProcesoValoracionRepository repository;

    public ProcesoValoracionService(
            ProcesoValoracionRepository repository) {

        this.repository = repository;

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

        dto.setFechaCreacion(
                proceso.getFechaCreacion());

        return dto;

    }

    public ProcesoValoracionDTO obtener(Integer id) {

        ProcesoValoracion proceso = repository.findById(id)

                .orElseThrow(() ->

                new RuntimeException("Proceso no encontrado."));

        return convertirDTO(proceso);

    }

    public ProcesoValoracionDTO guardar(ProcesoValoracionDTO dto) {

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

        proceso.setIdUsuario(1); // temporal

        repository.save(proceso);

        return convertirDTO(proceso);

    }

    public ProcesoValoracionDTO actualizar(
            Integer id,
            ProcesoValoracionDTO dto) {

        ProcesoValoracion proceso = repository.findById(id)

                .orElseThrow(() ->

                new RuntimeException("Proceso no encontrado."));

        proceso.setNombre(dto.getNombre());

        proceso.setDescripcion(dto.getDescripcion());

        proceso.setPeriodo(dto.getPeriodo());

        proceso.setEstado(dto.getEstado());

        repository.save(proceso);

        return convertirDTO(proceso);

    }

    public void eliminar(Integer id) {

        if (!repository.existsById(id)) {

            throw new RuntimeException("Proceso no encontrado.");

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
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        proceso.setEstado("ACTIVO");

        repository.save(proceso);

    }

    public ProcesoValoracionDTO obtenerProcesoActivo() {

        ProcesoValoracion proceso = repository

                .findFirstByEstado("ACTIVO")

                .orElseThrow(() ->

                new RuntimeException("No existe un proceso activo."));

        return convertirDTO(proceso);

    }

}