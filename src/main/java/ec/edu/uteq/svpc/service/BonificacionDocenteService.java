package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.BonificacionDocente;
import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.repository.BonificacionDocenteRepository;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BonificacionDocenteService {

    private final BonificacionDocenteRepository bonificacionRepository;
    private final DocenteRepository docenteRepository;

    public BonificacionDocenteService(
            BonificacionDocenteRepository bonificacionRepository,
            DocenteRepository docenteRepository) {

        this.bonificacionRepository = bonificacionRepository;
        this.docenteRepository = docenteRepository;
    }

    // =========================================================
    // LISTAR BONIFICACIONES
    // =========================================================

    public List<BonificacionDocente> listar() {

        return bonificacionRepository.findAll();
    }

    // =========================================================
    // BUSCAR POR DOCENTE
    // =========================================================

    public List<BonificacionDocente> buscarPorDocente(Long idDocente) {

        return bonificacionRepository
                .findByDocenteIdDocente(idDocente);
    }

    // =========================================================
    // CREAR BONIFICACIÓN
    // =========================================================

    @Transactional
    public BonificacionDocente crear(
            Long idDocente,
            String criterio,
            BigDecimal puntaje) {

        // -----------------------------------------
        // VALIDAR DOCENTE
        // -----------------------------------------

        Docente docente = docenteRepository
                .findById(idDocente)
                .orElseThrow(() -> new RuntimeException(
                        "Docente no encontrado."));

        // -----------------------------------------
        // VALIDAR CRITERIO
        // -----------------------------------------

        validarCriterio(criterio);

        // -----------------------------------------
        // VALIDAR PUNTAJE
        // -----------------------------------------

        validarPuntaje(puntaje);

        // -----------------------------------------
        // CREAR
        // -----------------------------------------

        BonificacionDocente bonificacion = new BonificacionDocente();

        bonificacion.setDocente(docente);
        bonificacion.setCriterioAsignacion(
                criterio);
        bonificacion.setPuntajeAsignado(
                puntaje);

        return bonificacionRepository.save(
                bonificacion);
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    @Transactional
    public BonificacionDocente actualizar(
            Integer idBonificacion,
            Long idDocente,
            String criterio,
            BigDecimal puntaje) {

        BonificacionDocente bonificacion = bonificacionRepository.findById(
                idBonificacion).orElseThrow(
                        () -> new RuntimeException(
                                "Bonificación no encontrada."));

        Docente docente = docenteRepository.findById(idDocente)
                .orElseThrow(() -> new RuntimeException(
                        "Docente no encontrado."));

        validarCriterio(criterio);
        validarPuntaje(puntaje);

        bonificacion.setDocente(docente);

        bonificacion.setCriterioAsignacion(
                criterio);

        bonificacion.setPuntajeAsignado(
                puntaje);

        return bonificacionRepository.save(
                bonificacion);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Transactional
    public void eliminar(Integer idBonificacion) {

        if (!bonificacionRepository
                .existsById(idBonificacion)) {

            throw new RuntimeException(
                    "Bonificación no encontrada.");
        }

        bonificacionRepository.deleteById(
                idBonificacion);
    }

    // =========================================================
    // VALIDAR CRITERIO
    // =========================================================

    private void validarCriterio(String criterio) {

        if (criterio == null || criterio.isBlank()) {

            throw new RuntimeException(
                    "Debe seleccionar un criterio de asignación.");
        }

        if (!criterio.equals("POR MERITO ACADEMICO")
                && !criterio.equals("RECONOCIMIENTO")
                && !criterio.equals("POR UTILIDAD")) {

            throw new RuntimeException(
                    "Criterio de asignación no válido.");
        }
    }

    // =========================================================
    // VALIDAR PUNTAJE
    // =========================================================

    private void validarPuntaje(BigDecimal puntaje) {

        if (puntaje == null) {

            throw new RuntimeException(
                    "Debe ingresar un puntaje.");
        }

        if (puntaje.compareTo(BigDecimal.ZERO) < 0
                || puntaje.compareTo(
                        new BigDecimal("50.00")) > 0) {

            throw new RuntimeException(
                    "El puntaje debe estar entre 0 y 50.");
        }
    }
}