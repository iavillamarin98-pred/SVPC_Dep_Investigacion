package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.service.ImportacionProceedingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importaciones")
public class ImportacionProceedingController {

    private final ImportacionProceedingService service;

    public ImportacionProceedingController(ImportacionProceedingService service) {
        this.service = service;
    }

    @PostMapping("/proceedings")
    public ResponseEntity<String> importarProceedings(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idProceso") Integer idProceso) {

        return ResponseEntity.ok(
                service.importar(archivo, idProceso));
    }
}