package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.service.ImportacionLibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importaciones")
public class ImportacionLibroController {

    private final ImportacionLibroService importacionLibroService;

    public ImportacionLibroController(ImportacionLibroService importacionLibroService) {
        this.importacionLibroService = importacionLibroService;
    }

    @PostMapping("/libros")
    public ResponseEntity<String> importarLibros(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idProceso") Integer idProceso) {
        String resultado = importacionLibroService.importarLibros(archivo, idProceso);
        return ResponseEntity.ok(resultado);
    }
}