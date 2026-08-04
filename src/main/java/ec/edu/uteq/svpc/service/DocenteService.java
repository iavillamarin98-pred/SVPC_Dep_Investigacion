package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocenteService {

    private final DocenteRepository docenteRepository;

    public DocenteService(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    /**
     * Listar todos los docentes.
     */
    public List<Docente> listar() {
        return docenteRepository.findAll();
    }

    /**
     * Obtener un docente por ID.
     */
    public Docente obtener(Long idDocente) {
        return docenteRepository.findById(idDocente)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado."));
    }

    /**
     * Buscar docentes.
     */
    public List<Docente> buscar(String texto) {

        if (texto == null || texto.isBlank()) {
            return docenteRepository.findAll();
        }

        texto = texto.trim();

        return docenteRepository
                .findByCedulaContainingIgnoreCaseOrNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrFacultadContainingIgnoreCaseOrCarreraContainingIgnoreCase(
                        texto,
                        texto,
                        texto,
                        texto,
                        texto);
    }

}