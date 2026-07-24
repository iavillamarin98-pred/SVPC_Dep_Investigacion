package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.dto.RankingArticuloDTO;
import ec.edu.uteq.svpc.service.CalculoPuntajeArticuloService;
import ec.edu.uteq.svpc.service.CalculoPuntajeLibroService;
import ec.edu.uteq.svpc.service.CalculoPuntajeCapituloLibroService;
import ec.edu.uteq.svpc.service.RankingArticuloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calculos")
public class CalculoPuntajeController {

    private final CalculoPuntajeArticuloService calculoPuntajeArticuloService;
    private final CalculoPuntajeLibroService calculoPuntajeLibroService;
    private final CalculoPuntajeCapituloLibroService calculoPuntajeCapituloLibroService;
    private final RankingArticuloService rankingArticuloService;

    public CalculoPuntajeController(
            CalculoPuntajeArticuloService calculoPuntajeArticuloService,
            CalculoPuntajeLibroService calculoPuntajeLibroService,
            CalculoPuntajeCapituloLibroService calculoPuntajeCapituloLibroService,
            RankingArticuloService rankingArticuloService) {
        this.calculoPuntajeArticuloService = calculoPuntajeArticuloService;
        this.calculoPuntajeLibroService = calculoPuntajeLibroService;
        this.calculoPuntajeCapituloLibroService = calculoPuntajeCapituloLibroService;
        this.rankingArticuloService = rankingArticuloService;
    }

    @PostMapping("/articulos")
    public ResponseEntity<String> calcularPuntajesArticulos(
            @RequestParam("idProceso") Long idProceso) {
        String resultado = calculoPuntajeArticuloService.calcularPuntajesArticulos(idProceso);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/libros")
    public ResponseEntity<String> calcularPuntajesLibros(
            @RequestParam("idProceso") Integer idProceso) {
        String resultado = calculoPuntajeLibroService.calcularPuntajesLibros(idProceso);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/capitulos-libro")
    public ResponseEntity<String> calcularPuntajesCapitulosLibro(
            @RequestParam("idProceso") Integer idProceso) {
        String resultado = calculoPuntajeCapituloLibroService.calcularPuntajesCapitulosLibro(idProceso);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/articulos/ranking")
    public ResponseEntity<List<RankingArticuloDTO>> obtenerRankingArticulos(
            @RequestParam("idProceso") Long idProceso) {
        List<RankingArticuloDTO> ranking = rankingArticuloService.obtenerRankingArticulos(idProceso);
        return ResponseEntity.ok(ranking);
    }
}