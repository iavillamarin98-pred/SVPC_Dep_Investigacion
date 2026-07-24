package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.service.ImportacionArticuloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importaciones")
public class ImportacionArticuloController {

    private final ImportacionArticuloService importacionArticuloService;

    public ImportacionArticuloController(ImportacionArticuloService importacionArticuloService) {
        this.importacionArticuloService = importacionArticuloService;
    }

    @PostMapping("/articulos")
    public ResponseEntity<String> importarArticulos(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idProceso") Long idProceso
    ) {

        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Debe seleccionar un archivo Excel.");
        }

        String nombreArchivo = archivo.getOriginalFilename();

        if (nombreArchivo == null ||
                !(nombreArchivo.endsWith(".xls") || nombreArchivo.endsWith(".xlsx"))) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos .xls o .xlsx");
        }

        String resultado = importacionArticuloService.importarArticulos(archivo, idProceso);

        return ResponseEntity.ok(resultado);
    }
}