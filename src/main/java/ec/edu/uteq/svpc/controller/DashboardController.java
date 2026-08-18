package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.dto.DashboardResumenDTO;
import ec.edu.uteq.svpc.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> obtenerResumen(
            @RequestParam Integer idProceso) {

        return ResponseEntity.ok(
                dashboardService.obtenerResumen(idProceso));
    }
}