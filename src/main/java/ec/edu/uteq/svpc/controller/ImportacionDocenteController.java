package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.service.ImportacionDocenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importaciones/docentes")
@CrossOrigin(origins = "*")
public class ImportacionDocenteController {

    private final ImportacionDocenteService importacionDocenteService;

    public ImportacionDocenteController(
            ImportacionDocenteService importacionDocenteService) {

        this.importacionDocenteService = importacionDocenteService;
    }

    @PostMapping
    public ResponseEntity<String> importar(
            @RequestParam("archivo") MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Debe seleccionar un archivo Excel.");
        }

        return ResponseEntity.ok(
                importacionDocenteService.importarDocentes(archivo));
    }
}