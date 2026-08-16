package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.dto.RankingDocenteView;
import ec.edu.uteq.svpc.repository.RankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {

    private final RankingRepository rankingRepository;

    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    public List<RankingDocenteView> obtenerRankingGeneral(Integer idProceso) {
        return rankingRepository.findRankingGeneral(idProceso);
    }

    public List<RankingDocenteView> obtenerRankingPorFacultad(Integer idProceso, String facultad) {
        return rankingRepository.findRankingPorFacultad(idProceso, facultad);
    }

    public List<String> obtenerFacultadesDisponibles() {
        return rankingRepository.findFacultadesDisponibles();
    }
}