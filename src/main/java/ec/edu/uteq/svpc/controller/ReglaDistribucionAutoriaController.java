package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import ec.edu.uteq.svpc.service.ReglaDistribucionAutoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reglas-distribucion")
@CrossOrigin(origins = "*")
public class ReglaDistribucionAutoriaController {

    private final ReglaDistribucionAutoriaService service;

    public ReglaDistribucionAutoriaController(
            ReglaDistribucionAutoriaService service) {

        this.service = service;
    }

    /*
     * GET
     * /api/reglas-distribucion/proceso/1
     */
    @GetMapping("/proceso/{idProceso}")
    public ResponseEntity<List<ReglaDistribucionAutoria>> listar(
            @PathVariable Integer idProceso) {

        return ResponseEntity.ok(
                service.listarPorProceso(idProceso));
    }

    /*
     * GET
     * /api/reglas-distribucion/proceso/1/escenario/3
     */
    @GetMapping("/proceso/{idProceso}/escenario/{escenario}")
    public ResponseEntity<ReglaDistribucionAutoria> obtener(
            @PathVariable Integer idProceso,
            @PathVariable String escenario) {

        return ResponseEntity.ok(
                service.obtenerRegla(
                        idProceso,
                        escenario));
    }

    /*
     * PUT
     * /api/reglas-distribucion/1
     */
    @PutMapping("/{idRegla}")
    public ResponseEntity<ReglaDistribucionAutoria> actualizar(
            @PathVariable Integer idRegla,
            @RequestBody ReglaDistribucionAutoria datos) {

        return ResponseEntity.ok(
                service.actualizar(
                        idRegla,
                        datos));
    }
}