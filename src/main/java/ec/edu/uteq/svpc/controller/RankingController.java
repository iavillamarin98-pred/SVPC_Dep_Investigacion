package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.dto.RankingDocenteView;
import ec.edu.uteq.svpc.service.RankingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/general")
    public List<RankingDocenteView> general(@RequestParam Integer idProceso) {
        return rankingService.obtenerRankingGeneral(idProceso);
    }

    @GetMapping("/facultad")
    public List<RankingDocenteView> porFacultad(@RequestParam Integer idProceso,
            @RequestParam String facultad) {
        return rankingService.obtenerRankingPorFacultad(idProceso, facultad);
    }

    @GetMapping("/facultades")
    public List<String> facultadesDisponibles() {
        return rankingService.obtenerFacultadesDisponibles();
    }
}