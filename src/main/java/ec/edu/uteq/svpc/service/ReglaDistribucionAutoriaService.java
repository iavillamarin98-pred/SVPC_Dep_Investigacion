package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import ec.edu.uteq.svpc.repository.ReglaDistribucionAutoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReglaDistribucionAutoriaService {

    private final ReglaDistribucionAutoriaRepository repository;

    public ReglaDistribucionAutoriaService(
            ReglaDistribucionAutoriaRepository repository) {

        this.repository = repository;
    }

    /*
     * Obtener todas las reglas activas de un proceso.
     */
    public List<ReglaDistribucionAutoria> listarPorProceso(
            Integer idProceso) {

        return repository
                .findByIdProcesoAndEstadoTrueOrderByEscenarioAsc(
                        idProceso);
    }

    /*
     * Obtener una regla específica.
     */
    public ReglaDistribucionAutoria obtenerRegla(
            Integer idProceso,
            String escenario) {

        return repository
                .findByIdProcesoAndEscenarioAndEstadoTrue(
                        idProceso,
                        escenario)
                .orElseThrow(() -> new RuntimeException(
                        "No existe una regla de distribución activa "
                                + "para el escenario " + escenario));
    }

    /*
     * Actualizar una regla existente.
     */
    public ReglaDistribucionAutoria actualizar(
            Integer idRegla,
            ReglaDistribucionAutoria datos) {

        ReglaDistribucionAutoria regla = repository
                .findById(idRegla)
                .orElseThrow(() -> new RuntimeException(
                        "No existe la regla con ID: " + idRegla));

        regla.setPorcentajeAutor(
                datos.getPorcentajeAutor());

        regla.setPorcentajeCoautor(
                datos.getPorcentajeCoautor());

        return repository.save(regla);
    }
}