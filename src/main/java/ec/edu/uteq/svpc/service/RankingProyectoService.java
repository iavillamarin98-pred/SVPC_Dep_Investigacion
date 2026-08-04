package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.dto.RankingProyectoDTO;
import ec.edu.uteq.svpc.repository.ProyectoDocenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingProyectoService {

    private final ProyectoDocenteRepository repository;

    public RankingProyectoService(
            ProyectoDocenteRepository repository) {

        this.repository = repository;
    }

    public List<RankingProyectoDTO> obtenerRankingProyectos(
            Integer idProceso) {

        return repository.obtenerRankingProyectos(idProceso);

    }

}