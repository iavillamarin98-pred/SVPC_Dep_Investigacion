package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.dto.ParticipanteProyectoResponseDTO;
import ec.edu.uteq.svpc.dto.ProyectoCompletoDTO;
import ec.edu.uteq.svpc.entity.Proyecto;
import ec.edu.uteq.svpc.entity.ProyectoDocente;
import ec.edu.uteq.svpc.service.ProyectoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "*")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    /**
     * Listar proyectos.
     */
    @GetMapping
    public ResponseEntity<List<Proyecto>> listar() {
        return ResponseEntity.ok(proyectoService.listar());
    }

    /**
     * Obtener proyecto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(proyectoService.obtener(id));
    }

    /**
     * Crear proyecto.
     */
    @PostMapping
    public ResponseEntity<Proyecto> crear(
            @RequestBody Proyecto proyecto) {

        if ("INTERNO".equalsIgnoreCase(proyecto.getTipoFinanciamiento())) {
            throw new IllegalArgumentException(
                    "Los proyectos con financiamiento interno únicamente pueden registrarse mediante importación.");
        }

        return ResponseEntity.ok(
                proyectoService.crear(proyecto));
    }

    /**
     * Crear proyecto con participantes.
     */
    @PostMapping("/completo")
    public ResponseEntity<Proyecto> crearCompleto(
            @RequestBody ProyectoCompletoDTO dto) {

        return ResponseEntity.ok(
                proyectoService.crearCompleto(dto));
    }

    /**
     * Actualizar únicamente los datos del proyecto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(
            @PathVariable Integer id,
            @RequestBody Proyecto proyecto) {

        if ("INTERNO".equalsIgnoreCase(proyecto.getTipoFinanciamiento())) {

            throw new IllegalArgumentException(
                    "Los proyectos con financiamiento interno únicamente pueden registrarse mediante importación.");

        }

        return ResponseEntity.ok(
                proyectoService.actualizar(id, proyecto));
    }

    /**
     * Actualizar proyecto y participantes.
     */
    @PutMapping("/completo/{id}")
    public ResponseEntity<Proyecto> actualizarCompleto(
            @PathVariable Integer id,
            @RequestBody ProyectoCompletoDTO dto) {

        return ResponseEntity.ok(
                proyectoService.actualizarCompleto(id, dto));
    }

    /**
     * Eliminar proyecto.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id) {

        proyectoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Cambiar estado.
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Proyecto> cambiarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {

        return ResponseEntity.ok(
                proyectoService.cambiarEstado(
                        id,
                        body.get("estado")));
    }

    /**
     * Agregar participante.
     * (Se mantiene para operaciones individuales)
     */
    @PostMapping("/{id}/integrantes")
    public ResponseEntity<Void> agregarIntegrante(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {

        proyectoService.agregarIntegrante(
                id,
                Long.valueOf(body.get("idDocente").toString()),
                body.get("rol").toString());

        return ResponseEntity.ok().build();
    }

    /**
     * Eliminar participante.
     */
    @DeleteMapping("/{id}/integrantes/{idDocente}")
    public ResponseEntity<Void> eliminarIntegrante(
            @PathVariable Integer id,
            @PathVariable Long idDocente) {

        proyectoService.eliminarIntegrante(
                id,
                idDocente);

        return ResponseEntity.noContent().build();
    }

    /**
     * Listado interno de relaciones.
     */
    @GetMapping("/{id}/integrantes")
    public ResponseEntity<List<ProyectoDocente>> listarIntegrantes(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                proyectoService.listarIntegrantes(id));
    }

    /**
     * Listado para el frontend.
     */
    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<ParticipanteProyectoResponseDTO>> listarParticipantes(@PathVariable Integer id) {

        return ResponseEntity.ok(
                proyectoService.listarParticipantesDTO(id));
    }

}