package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import ec.edu.uteq.svpc.service.ConfiguracionPuntajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/configuracion-puntajes")
@CrossOrigin
public class ConfiguracionPuntajeController {

    private final ConfiguracionPuntajeService service;

    public ConfiguracionPuntajeController(
            ConfiguracionPuntajeService service) {

        this.service = service;
    }

    /*
     * ==========================================
     * PUNTAJES
     * ==========================================
     */

    @GetMapping("/puntajes/{idProceso}")
    public ResponseEntity<List<ConfiguracionPuntaje>> listarPuntajes(
            @PathVariable Long idProceso) {

        return ResponseEntity.ok(
                service.listarPuntajes(idProceso));
    }

    @PutMapping("/puntajes/{idConfiguracion}")
    public ResponseEntity<ConfiguracionPuntaje> actualizarPuntaje(
            @PathVariable Long idConfiguracion,
            @RequestParam BigDecimal puntaje) {

        return ResponseEntity.ok(
                service.actualizarPuntaje(
                        idConfiguracion,
                        puntaje));
    }

    /*
     * ==========================================
     * REGLAS DE DISTRIBUCIÓN
     * ==========================================
     */

    @GetMapping("/reglas/{idProceso}")
    public ResponseEntity<List<ReglaDistribucionAutoria>> listarReglas(
            @PathVariable Integer idProceso) {

        return ResponseEntity.ok(
                service.listarReglas(idProceso));
    }

    @PutMapping("/reglas/{idRegla}")
    public ResponseEntity<ReglaDistribucionAutoria> actualizarRegla(
            @PathVariable Integer idRegla,
            @RequestParam BigDecimal porcentajeAutor,
            @RequestParam BigDecimal porcentajeCoautor) {

        return ResponseEntity.ok(
                service.actualizarRegla(
                        idRegla,
                        porcentajeAutor,
                        porcentajeCoautor));
    }
}