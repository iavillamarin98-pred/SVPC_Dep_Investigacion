package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.dto.RankingLibroDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RankingLibroService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RankingLibroDTO> obtenerRanking(Long idProceso) {

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = entityManager.createNativeQuery("""

                    SELECT
                        d.cedula,
                        d.apellidos,
                        d.nombres,
                        d.carrera,
                        v.puntaje_libros
                    FROM valoraciones v
                    INNER JOIN docentes d
                        ON d.id_docente = v.id_docente
                    WHERE v.id_proceso = :idProceso
                    ORDER BY
                        v.puntaje_libros DESC,
                        d.apellidos,
                        d.nombres

                """)
                .setParameter("idProceso", idProceso.intValue())
                .getResultList();

        List<RankingLibroDTO> ranking = new ArrayList<>();

        int posicion = 1;

        for (Object[] fila : resultados) {

            ranking.add(

                    new RankingLibroDTO(
                            posicion++,
                            (String) fila[0],
                            (String) fila[1],
                            (String) fila[2],
                            (String) fila[3],
                            (BigDecimal) fila[4]));

        }

        return ranking;
    }

}