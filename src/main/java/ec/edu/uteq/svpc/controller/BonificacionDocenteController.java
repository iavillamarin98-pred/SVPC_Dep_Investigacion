package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.entity.BonificacionDocente;
import ec.edu.uteq.svpc.service.BonificacionDocenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bonificaciones")
@CrossOrigin
public class BonificacionDocenteController {

        private final BonificacionDocenteService service;

        public BonificacionDocenteController(
                        BonificacionDocenteService service) {

                this.service = service;
        }

        // =========================================================
        // LISTAR
        // =========================================================

        @GetMapping
        public ResponseEntity<?> listar() {

                return ResponseEntity.ok(
                                service.listar());
        }

        // =========================================================
        // BUSCAR POR DOCENTE
        // =========================================================

        @GetMapping("/docente/{idDocente}")
        public ResponseEntity<?> buscarPorDocente(
                        @PathVariable Long idDocente) {

                return ResponseEntity.ok(
                                service.buscarPorDocente(idDocente));
        }

        // =========================================================
        // CREAR
        // =========================================================

        @PostMapping
        public ResponseEntity<?> crear(
                        @RequestBody Map<String, Object> datos) {

                try {

                        // -----------------------------------------
                        // OBTENER DOCENTE
                        // -----------------------------------------

                        Long idDocente = Long.valueOf(
                                        datos.get("idDocente").toString());

                        // -----------------------------------------
                        // OBTENER CRITERIO
                        // -----------------------------------------

                        String criterio = datos
                                        .get("criterioAsignacion")
                                        .toString();

                        // -----------------------------------------
                        // OBTENER PUNTAJE
                        // -----------------------------------------

                        BigDecimal puntaje = new BigDecimal(
                                        datos.get("puntajeAsignado")
                                                        .toString());

                        // -----------------------------------------
                        // CREAR
                        // -----------------------------------------

                        BonificacionDocente resultado = service.crear(
                                        idDocente,
                                        criterio,
                                        puntaje);

                        return ResponseEntity.ok(resultado);

                } catch (Exception e) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        Map.of(
                                                                        "error",
                                                                        e.getMessage()));
                }
        }

        // =========================================================
        // ACTUALIZAR
        // =========================================================

        @PutMapping("/{idBonificacion}")
        public ResponseEntity<?> actualizar(
                        @PathVariable Integer idBonificacion,
                        @RequestBody Map<String, Object> datos) {

                try {

                        // -----------------------------------------
                        // OBTENER DOCENTE
                        // -----------------------------------------

                        Long idDocente = Long.valueOf(
                                        datos.get("idDocente").toString());

                        // -----------------------------------------
                        // OBTENER CRITERIO
                        // -----------------------------------------

                        String criterio = datos
                                        .get("criterioAsignacion")
                                        .toString();

                        // -----------------------------------------
                        // OBTENER PUNTAJE
                        // -----------------------------------------

                        BigDecimal puntaje = new BigDecimal(
                                        datos.get("puntajeAsignado")
                                                        .toString());

                        // -----------------------------------------
                        // ACTUALIZAR
                        // -----------------------------------------

                        BonificacionDocente resultado = service.actualizar(
                                        idBonificacion,
                                        idDocente,
                                        criterio,
                                        puntaje);

                        return ResponseEntity.ok(resultado);

                } catch (Exception e) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        Map.of(
                                                                        "error",
                                                                        e.getMessage()));
                }
        }

        // =========================================================
        // ELIMINAR
        // =========================================================

        @DeleteMapping("/{idBonificacion}")
        public ResponseEntity<?> eliminar(
                        @PathVariable Integer idBonificacion) {

                try {

                        service.eliminar(idBonificacion);

                        return ResponseEntity.ok(
                                        Map.of(
                                                        "mensaje",
                                                        "Bonificación eliminada correctamente."));

                } catch (Exception e) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        Map.of(
                                                                        "error",
                                                                        e.getMessage()));
                }
        }
}