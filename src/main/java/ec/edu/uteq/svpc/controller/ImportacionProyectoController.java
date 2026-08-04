package ec.edu.uteq.svpc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ec.edu.uteq.svpc.service.ImportacionProyectoService;

@RestController
@RequestMapping("/api/importaciones/proyectos")
@CrossOrigin(origins = "*")
public class ImportacionProyectoController {

    private final ImportacionProyectoService service;

    public ImportacionProyectoController(
            ImportacionProyectoService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> importar(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam Integer idProceso) throws Exception {

        return ResponseEntity.ok(
                service.importar(archivo, idProceso));
    }

}
