package ec.edu.uteq.svpc.controller;

import ec.edu.uteq.svpc.dto.RankingProceedingDTO;
import ec.edu.uteq.svpc.dto.RankingProyectoDTO;
import ec.edu.uteq.svpc.service.CalculoPuntajeProceedingService;
import ec.edu.uteq.svpc.service.RankingProceedingService;
import ec.edu.uteq.svpc.service.RankingProyectoService;
import ec.edu.uteq.svpc.dto.RankingArticuloDTO;
import ec.edu.uteq.svpc.service.CalculoPuntajeArticuloService;
import ec.edu.uteq.svpc.service.CalculoPuntajeLibroService;
import ec.edu.uteq.svpc.service.CalculoPuntajeCapituloLibroService;
import ec.edu.uteq.svpc.service.RankingArticuloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ec.edu.uteq.svpc.service.CalculoPuntajeProyectoService;
import ec.edu.uteq.svpc.dto.RankingLibroDTO;
import ec.edu.uteq.svpc.service.RankingLibroService;
import ec.edu.uteq.svpc.dto.RankingCapituloDTO;
import ec.edu.uteq.svpc.service.RankingCapituloService;

import java.util.List;

@RestController
@RequestMapping("/api/calculos")
public class CalculoPuntajeController {

    private final CalculoPuntajeArticuloService calculoPuntajeArticuloService;
    private final CalculoPuntajeLibroService calculoPuntajeLibroService;
    private final CalculoPuntajeCapituloLibroService calculoPuntajeCapituloLibroService;
    private final RankingArticuloService rankingArticuloService;
    private final CalculoPuntajeProceedingService calculoPuntajeProceedingService;
    private final RankingProceedingService rankingProceedingService;
    private final CalculoPuntajeProyectoService calculoPuntajeProyectoService;
    private final RankingProyectoService rankingProyectoService;
    private final RankingLibroService rankingLibroService;
    private final RankingCapituloService rankingCapituloService;

    public CalculoPuntajeController(
            CalculoPuntajeArticuloService calculoPuntajeArticuloService,
            CalculoPuntajeLibroService calculoPuntajeLibroService,
            CalculoPuntajeCapituloLibroService calculoPuntajeCapituloLibroService,
            RankingArticuloService rankingArticuloService,
            CalculoPuntajeProceedingService calculoPuntajeProceedingService,
            RankingProceedingService rankingProceedingService,
            CalculoPuntajeProyectoService calculoPuntajeProyectoService,
            RankingProyectoService rankingProyectoService,
            RankingLibroService rankingLibroService,
            RankingCapituloService rankingCapituloService) {

        this.calculoPuntajeArticuloService = calculoPuntajeArticuloService;
        this.calculoPuntajeLibroService = calculoPuntajeLibroService;
        this.calculoPuntajeCapituloLibroService = calculoPuntajeCapituloLibroService;
        this.rankingArticuloService = rankingArticuloService;

        this.calculoPuntajeProceedingService = calculoPuntajeProceedingService;
        this.rankingProceedingService = rankingProceedingService;
        this.calculoPuntajeProyectoService = calculoPuntajeProyectoService;
        this.rankingProyectoService = rankingProyectoService;
        this.rankingLibroService = rankingLibroService;
        this.rankingCapituloService = rankingCapituloService;
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

    @GetMapping("/libros/ranking")
    public List<RankingLibroDTO> rankingLibros(
            @RequestParam Long idProceso) {

        return rankingLibroService.obtenerRanking(idProceso);
    }

    @PostMapping("/capitulos-libro")
    public ResponseEntity<String> calcularPuntajesCapitulosLibro(
            @RequestParam("idProceso") Integer idProceso) {
        String resultado = calculoPuntajeCapituloLibroService.calcularPuntajesCapitulosLibro(idProceso);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/capitulos-libro/ranking")
    public ResponseEntity<List<RankingCapituloDTO>> obtenerRankingCapitulosLibro(
            @RequestParam Integer idProceso) {

        return ResponseEntity.ok(
                rankingCapituloService.obtenerRanking(idProceso));
    }

    @GetMapping("/articulos/ranking")
    public ResponseEntity<List<RankingArticuloDTO>> obtenerRankingArticulos(
            @RequestParam("idProceso") Long idProceso) {
        List<RankingArticuloDTO> ranking = rankingArticuloService.obtenerRankingArticulos(idProceso);
        return ResponseEntity.ok(ranking);
    }

    @PostMapping("/proceedings")
    public ResponseEntity<String> calcularPuntajesProceedings(
            @RequestParam("idProceso") Long idProceso) {

        String resultado = calculoPuntajeProceedingService.calcularPuntajesProceedings(idProceso);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/proceedings/ranking")
    public ResponseEntity<List<RankingArticuloDTO>> obtenerRankingProceedings(
            @RequestParam Long idProceso) {

        return ResponseEntity.ok(
                rankingProceedingService.obtenerRankingProceedings(idProceso));
    }

    @PostMapping("/proyectos")
    public ResponseEntity<String> calcularPuntajesProyectos(
            @RequestParam Integer idProceso) {

        return ResponseEntity.ok(
                calculoPuntajeProyectoService.calcular(idProceso));

    }

    @GetMapping("/proyectos/ranking")
    public ResponseEntity<List<RankingProyectoDTO>> obtenerRankingProyectos(
            @RequestParam Integer idProceso) {

        return ResponseEntity.ok(
                rankingProyectoService.obtenerRankingProyectos(idProceso));

    }

}