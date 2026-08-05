package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.dto.RankingCapituloDTO;
import ec.edu.uteq.svpc.repository.ValoracionCapituloLibroRepository;
import ec.edu.uteq.svpc.repository.ValoracionCapituloLibroRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankingCapituloService {

    private final ValoracionCapituloLibroRepository repository;

    public RankingCapituloService(
            ValoracionCapituloLibroRepository repository) {

        this.repository = repository;
    }

    public List<RankingCapituloDTO> obtenerRanking(Integer idProceso) {

        List<Object[]> datos = repository.obtenerRankingCapitulos(idProceso);

        List<RankingCapituloDTO> ranking = new ArrayList<>();

        int puesto = 1;

        for (Object[] fila : datos) {

            RankingCapituloDTO dto = new RankingCapituloDTO();

            dto.setPosicion(puesto++);

            dto.setCedula((String) fila[0]);
            dto.setApellidos((String) fila[1]);
            dto.setNombres((String) fila[2]);
            dto.setCarrera((String) fila[3]);

            dto.setPuntajeCapitulos(
                    fila[4] == null
                            ? 0.0
                            : ((Number) fila[4]).doubleValue());

            ranking.add(dto);
        }

        return ranking;
    }

}