package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.service.DocenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docentes")
@CrossOrigin(origins = "*")
public class DocenteController {

    private final DocenteService docenteService;

    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    /**
     * Listar todos los docentes.
     */
    @GetMapping
    public ResponseEntity<List<Docente>> listar() {
        return ResponseEntity.ok(docenteService.listar());
    }

    /**
     * Obtener docente por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Docente> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(docenteService.obtener(id));
    }

    /**
     * Buscar docentes por:
     * - Cédula
     * - Nombres
     * - Apellidos
     * - Facultad
     * - Carrera
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Docente>> buscar(
            @RequestParam(required = false, defaultValue = "") String texto) {

        return ResponseEntity.ok(
                docenteService.buscar(texto));
    }

    @GetMapping("/filtros")
    public ResponseEntity<List<Docente>> buscarConFiltros(
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String facultad) {

        return ResponseEntity.ok(
                docenteService.buscarConFiltros(
                        cedula,
                        nombre,
                        facultad));
    }

}