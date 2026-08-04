package ec.edu.uteq.svpc.dto;

import ec.edu.uteq.svpc.entity.Proyecto;

import java.util.List;

public class ProyectoCompletoDTO {

    private Proyecto proyecto;

    private List<ParticipanteProyectoDTO> participantes;

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public List<ParticipanteProyectoDTO> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipanteProyectoDTO> participantes) {
        this.participantes = participantes;
    }

}