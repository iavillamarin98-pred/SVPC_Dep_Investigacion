let PROCESO_ACTIVO = null;

async function cargarProcesoActivo() {

    const respuesta = await fetch("/api/procesos/activo");

    if (!respuesta.ok) {
        throw new Error("No existe un proceso activo.");
    }

    PROCESO_ACTIVO = await respuesta.json();

    console.log("Proceso activo:", PROCESO_ACTIVO);

    const texto =
        `${PROCESO_ACTIVO.nombre} (${PROCESO_ACTIVO.periodo})`;

    //=========================================
    // ACTUALIZA TODOS LOS LABELS DEL PROCESO
    //=========================================

    const etiquetas = [

        "nombreProcesoActivo",
        "nombreProcesoActivoArticulos",
        "nombreProcesoActivoProceedings",
        "nombreProcesoActivoLibros",
        "nombreProcesoActivoCapitulos",
        "nombreProcesoActivoProyectos"

    ];

    etiquetas.forEach(id => {

        const elemento = document.getElementById(id);

        if (elemento) {
            elemento.textContent = texto;
        }

    });

    //=========================================
    // MÓDULO PROCESOS
    //=========================================

    const procesoActual = document.getElementById("procesoActivoActual");

    if (procesoActual) {

        procesoActual.innerHTML = `
            <strong>${PROCESO_ACTIVO.nombre}</strong><br>
            Período: ${PROCESO_ACTIVO.periodo}<br>
            Estado:
            <span class="badge badge-success">
                ${PROCESO_ACTIVO.estado}
            </span>
        `;

    }

    actualizarModoProceso();
}

function obtenerIdProceso() {

    if (!PROCESO_ACTIVO) {
        throw new Error("Proceso activo no cargado.");
    }

    return PROCESO_ACTIVO.idProceso;

}

function procesoEstaCerrado() {

    return PROCESO_ACTIVO &&
           PROCESO_ACTIVO.estado === "CERRADO";

}

function validarProcesoEditable() {

    if (procesoEstaCerrado()) {

        alert(
            "El proceso de valoración está CERRADO. Solo se permite la consulta."
        );

        return false;

    }

    return true;

}
function actualizarModoProceso() {

    const bloqueado = procesoEstaCerrado();

    document.querySelectorAll(

        "[data-requiere-edicion]"

    ).forEach(boton => {

        boton.disabled = bloqueado;

    });

}