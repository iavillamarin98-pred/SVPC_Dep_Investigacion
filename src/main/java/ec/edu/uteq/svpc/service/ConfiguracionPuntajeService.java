package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.ConfiguracionPuntaje;
import ec.edu.uteq.svpc.entity.ReglaDistribucionAutoria;
import ec.edu.uteq.svpc.repository.ConfiguracionPuntajeRepository;
import ec.edu.uteq.svpc.repository.ReglaDistribucionAutoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConfiguracionPuntajeService {

    private final ConfiguracionPuntajeRepository configuracionRepository;
    private final ReglaDistribucionAutoriaRepository reglaRepository;

    public ConfiguracionPuntajeService(
            ConfiguracionPuntajeRepository configuracionRepository,
            ReglaDistribucionAutoriaRepository reglaRepository) {

        this.configuracionRepository = configuracionRepository;
        this.reglaRepository = reglaRepository;
    }

    /*
     * ==========================================
     * CONFIGURACIÓN DE PUNTAJES
     * ==========================================
     */

    public List<ConfiguracionPuntaje> listarPuntajes(Long idProceso) {

        return configuracionRepository
                .findByIdProceso(idProceso);
    }

    public ConfiguracionPuntaje actualizarPuntaje(
            Long idConfiguracion,
            java.math.BigDecimal nuevoPuntaje) {

        ConfiguracionPuntaje configuracion = configuracionRepository
                .findById(idConfiguracion)
                .orElseThrow(() -> new RuntimeException(
                        "Configuración de puntaje no encontrada"));

        configuracion.setPuntajeBase(nuevoPuntaje);

        return configuracionRepository.save(configuracion);
    }

    /*
     * ==========================================
     * REGLAS DE DISTRIBUCIÓN
     * ==========================================
     */

    public List<ReglaDistribucionAutoria> listarReglas(
            Integer idProceso) {

        return reglaRepository
                .findByIdProcesoAndEstadoTrueOrderByEscenarioAsc(idProceso);
    }

    public ReglaDistribucionAutoria actualizarRegla(
            Integer idRegla,
            java.math.BigDecimal porcentajeAutor,
            java.math.BigDecimal porcentajeCoautor) {

        ReglaDistribucionAutoria regla = reglaRepository
                .findById(idRegla)
                .orElseThrow(() -> new RuntimeException(
                        "Regla de distribución no encontrada"));

        regla.setPorcentajeAutor(porcentajeAutor);
        regla.setPorcentajeCoautor(porcentajeCoautor);

        return reglaRepository.save(regla);
    }
}