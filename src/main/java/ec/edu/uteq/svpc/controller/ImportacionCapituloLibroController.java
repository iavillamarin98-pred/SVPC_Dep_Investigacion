package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.service.ImportacionCapituloLibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importaciones/capitulos-libro")
public class ImportacionCapituloLibroController {

    private final ImportacionCapituloLibroService importacionCapituloLibroService;

    public ImportacionCapituloLibroController(
            ImportacionCapituloLibroService importacionCapituloLibroService) {
        this.importacionCapituloLibroService = importacionCapituloLibroService;
    }

    @PostMapping
    public ResponseEntity<String> importarCapitulosLibro(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idProceso") Integer idProceso) {
        String resultado = importacionCapituloLibroService.importarCapitulosLibro(archivo, idProceso);
        return ResponseEntity.ok(resultado);
    }
}