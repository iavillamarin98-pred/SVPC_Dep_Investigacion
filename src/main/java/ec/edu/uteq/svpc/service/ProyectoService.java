package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Proyecto;
import ec.edu.uteq.svpc.entity.ProyectoDocente;
import ec.edu.uteq.svpc.entity.ProyectoDocenteId;
import ec.edu.uteq.svpc.dto.ParticipanteProyectoDTO;
import ec.edu.uteq.svpc.dto.ProyectoCompletoDTO;
import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.repository.ProyectoRepository;
import ec.edu.uteq.svpc.repository.ProyectoDocenteRepository;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ec.edu.uteq.svpc.dto.ParticipanteProyectoDTO;
import ec.edu.uteq.svpc.dto.ParticipanteProyectoResponseDTO;
import ec.edu.uteq.svpc.dto.ProyectoCompletoDTO;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final ProyectoDocenteRepository proyectoDocenteRepository;
    private final DocenteRepository docenteRepository;

    public ProyectoService(
            ProyectoRepository proyectoRepository,
            ProyectoDocenteRepository proyectoDocenteRepository,
            DocenteRepository docenteRepository) {

        this.proyectoRepository = proyectoRepository;
        this.proyectoDocenteRepository = proyectoDocenteRepository;
        this.docenteRepository = docenteRepository;
    }

    /**
     * Listar todos los proyectos.
     */
    public List<Proyecto> listar() {
        return proyectoRepository.findAll();
    }

    /**
     * Buscar proyecto por id.
     */
    public Proyecto obtener(Integer idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado."));
    }

    /**
     * Crear proyecto.
     */
    public Proyecto crear(Proyecto proyecto) {

        proyecto.setIdProyecto(null);

        validarTipoFinanciamiento(proyecto.getTipoFinanciamiento());

        if (proyecto.getEstado() == null ||
                proyecto.getEstado().isBlank()) {

            proyecto.setEstado("APROBADO");
        }

        return proyectoRepository.save(proyecto);
    }

    /**
     * Actualizar proyecto.
     */
    public Proyecto actualizar(Integer idProyecto,
            Proyecto datos) {

        Proyecto proyecto = obtener(idProyecto);

        proyecto.setNombre(datos.getNombre());
        proyecto.setDescripcion(datos.getDescripcion());
        proyecto.setPeriodo(datos.getPeriodo());

        validarTipoFinanciamiento(datos.getTipoFinanciamiento());

        proyecto.setTipoFinanciamiento(
                datos.getTipoFinanciamiento());

        if (datos.getEstado() == null || datos.getEstado().isBlank()) {
            proyecto.setEstado("APROBADO");
        } else {
            proyecto.setEstado(datos.getEstado().toUpperCase().trim());
        }

        return proyectoRepository.save(proyecto);
    }

    /**
     * Eliminar proyecto.
     */
    public void eliminar(Integer idProyecto) {

        Proyecto proyecto = obtener(idProyecto);

        proyectoDocenteRepository.deleteByIdIdProyecto(idProyecto);

        proyectoRepository.delete(proyecto);
    }

    /**
     * Cambiar estado.
     */
    public Proyecto cambiarEstado(Integer idProyecto,
            String estado) {

        Proyecto proyecto = obtener(idProyecto);

        proyecto.setEstado(
                estado.toUpperCase().trim());

        return proyectoRepository.save(proyecto);
    }

    /**
     * Agregar integrante.
     */
    public void agregarIntegrante(Integer idProyecto,
            Long idDocente,
            String rol) {

        obtener(idProyecto);

        Docente docente = docenteRepository.findById(idDocente)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado."));

        ProyectoDocenteId id = new ProyectoDocenteId(idProyecto,
                docente.getIdDocente());

        if (proyectoDocenteRepository.existsById(id)) {
            return;
        }

        ProyectoDocente relacion = new ProyectoDocente();

        relacion.setId(id);

        validarRol(rol);

        relacion.setRolParticipante(rol.toUpperCase().trim());

        relacion.setPuntajeObtenido(0.0);

        proyectoDocenteRepository.save(relacion);
    }

    /**
     * Eliminar integrante.
     */
    public void eliminarIntegrante(Integer idProyecto,
            Long idDocente) {

        ProyectoDocenteId id = new ProyectoDocenteId(
                idProyecto,
                idDocente);

        proyectoDocenteRepository.deleteById(id);
    }

    /**
     * Obtener integrantes.
     */
    public List<ProyectoDocente> listarIntegrantes(
            Integer idProyecto) {

        return proyectoDocenteRepository
                .findByIdIdProyecto(idProyecto);
    }

    /**
     * Validación del tipo de financiamiento.
     */
    private void validarTipoFinanciamiento(
            String tipo) {

        if (tipo == null) {
            throw new RuntimeException(
                    "Debe indicar el tipo de financiamiento.");
        }

        tipo = tipo.toUpperCase().trim();

        switch (tipo) {

            case "EXTERNO":
            case "CONJUNTO":
            case "INTERNO":
                return;

            default:
                throw new RuntimeException(
                        "Tipo de financiamiento inválido.");
        }
    }

    private void validarRol(String rol) {

        if (rol == null) {
            throw new RuntimeException("Debe indicar el rol.");
        }

        rol = rol.toUpperCase().trim();

        switch (rol) {

            case "DIRECTOR":
            case "INTEGRANTE":
                return;

            default:
                throw new RuntimeException("Rol inválido.");
        }
    }

    @Transactional
    public Proyecto crearCompleto(ProyectoCompletoDTO dto) {

        Proyecto proyecto = crear(dto.getProyecto());

        for (ParticipanteProyectoDTO participante : dto.getParticipantes()) {

            agregarIntegrante(
                    proyecto.getIdProyecto(),
                    participante.getIdDocente(),
                    participante.getRol());

        }

        return proyecto;

    }

    public ProyectoDocente obtenerParticipante(Integer idProyecto, Long idDocente) {

        ProyectoDocenteId id = new ProyectoDocenteId(idProyecto, idDocente);

        return proyectoDocenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado."));

    }

    public List<ParticipanteProyectoResponseDTO> listarParticipantesDTO(Integer idProyecto) {

        List<ProyectoDocente> relaciones = proyectoDocenteRepository.findByIdIdProyecto(idProyecto);

        List<ParticipanteProyectoResponseDTO> lista = new ArrayList<>();

        for (ProyectoDocente r : relaciones) {

            Docente docente = docenteRepository.findById(
                    r.getId().getIdDocente()).orElseThrow();

            ParticipanteProyectoResponseDTO dto = new ParticipanteProyectoResponseDTO();

            dto.setIdDocente(docente.getIdDocente());
            dto.setCedula(docente.getCedula());
            dto.setNombres(docente.getNombres());
            dto.setApellidos(docente.getApellidos());
            dto.setFacultad(docente.getFacultad());
            dto.setCarrera(docente.getCarrera());
            dto.setRol(r.getRolParticipante());

            lista.add(dto);

        }

        return lista;

    }

    @Transactional
    public Proyecto actualizarCompleto(
            Integer idProyecto,
            ProyectoCompletoDTO dto) {

        // Actualizar datos del proyecto
        Proyecto proyecto = actualizar(
                idProyecto,
                dto.getProyecto());

        // Eliminar participantes actuales
        proyectoDocenteRepository.deleteByIdIdProyecto(idProyecto);

        // Registrar nuevamente los participantes
        if (dto.getParticipantes() != null) {

            for (ParticipanteProyectoDTO participante : dto.getParticipantes()) {

                agregarIntegrante(
                        idProyecto,
                        participante.getIdDocente(),
                        participante.getRol());

            }

        }

        return proyecto;

    }

}