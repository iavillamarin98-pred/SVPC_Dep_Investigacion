package ec.edu.uteq.svpc.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ec.edu.uteq.svpc.dto.ProcesoValoracionDTO;
import ec.edu.uteq.svpc.service.ProcesoValoracionService;

@RestController
@RequestMapping("/api/procesos")
@CrossOrigin(origins = "*")
public class ProcesoValoracionController {

    private final ProcesoValoracionService service;

    public ProcesoValoracionController(
            ProcesoValoracionService service) {

        this.service = service;

    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<String> activar(

            @PathVariable Integer id) {

        service.activarProceso(id);

        return ResponseEntity.ok("Proceso activado correctamente.");

    }

    @GetMapping("/activo")
    public ResponseEntity<ProcesoValoracionDTO> activo() {

        return ResponseEntity.ok(

                service.obtenerProcesoActivo());

    }

    @GetMapping
    public List<ProcesoValoracionDTO> listar() {

        return service.listar();

    }

    @GetMapping("/{id}")
    public ProcesoValoracionDTO obtener(
            @PathVariable Integer id) {

        return service.obtener(id);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcesoValoracionDTO guardar(
            @RequestBody ProcesoValoracionDTO dto) {

        return service.guardar(dto);

    }

    @PutMapping("/{id}")
    public ProcesoValoracionDTO actualizar(

            @PathVariable Integer id,

            @RequestBody ProcesoValoracionDTO dto) {

        return service.actualizar(id, dto);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @PathVariable Integer id) {

        service.eliminar(id);

    }

}