let PROCESO_ACTIVO = null;

async function cargarProcesoActivo() {

    const respuesta = await fetch("/api/procesos/activo");

    // No existe proceso activo
    if (respuesta.status === 204) {

        PROCESO_ACTIVO = null;

        const procesoActual =
            document.getElementById("procesoActivoActual");

        if (procesoActual) {

            procesoActual.innerHTML = `
    <div class="proceso-activo-info">

        <div class="proceso-nombre">
            Sin proceso activo
        </div>

        <span class="badge badge-warning">
            INACTIVO
        </span>

    </div>
`;

        }

        [
            "nombreProcesoActivo",
            "nombreProcesoActivoArticulos",
            "nombreProcesoActivoProceedings",
            "nombreProcesoActivoLibros",
            "nombreProcesoActivoCapitulos",
            "nombreProcesoActivoProyectos"
        ].forEach(id => {

            const e = document.getElementById(id);

            if (e) {

                e.textContent = "Sin proceso activo";

            }

        });

        actualizarModoProceso();

        return;

    }

    if (!respuesta.ok) {

        throw new Error("Error al consultar el proceso activo.");

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
    <div class="proceso-activo-info">

        <div class="proceso-nombre">
            ${PROCESO_ACTIVO.nombre}
        </div>

        <div class="proceso-periodo">
            <i class="fa-solid fa-calendar-days"></i>
            ${PROCESO_ACTIVO.periodo}
        </div>

        <span class="badge badge-success">
            ${PROCESO_ACTIVO.estado}
        </span>

    </div>
`;
    }

    actualizarModoProceso();
}

function obtenerIdProceso() {

    if (!PROCESO_ACTIVO) {

        throw new Error(
            "No existe un proceso activo."
        );

    }

    return PROCESO_ACTIVO.idProceso;

}

function procesoEstaCerrado() {

    return !PROCESO_ACTIVO ||
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