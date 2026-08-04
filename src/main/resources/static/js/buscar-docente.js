let callbackSeleccionDocente = null;

/**
 * Carga el componente HTML una sola vez.
 */
async function cargarBuscadorDocente() {

    if (document.getElementById("modalDocente")) {
        return;
    }

    const respuesta = await fetch("/modulos/componentes/buscar-docente.html");

    const html = await respuesta.text();

    document
        .getElementById("contenedorBuscadorDocente")
        .innerHTML = html;

    configurarEventosBuscador();
}

/**
 * Abre el modal.
 */
async function abrirBuscadorDocente(callback){

    callbackSeleccionDocente = callback;

    await cargarBuscadorDocente();

    document
        .getElementById("modalDocente")
        .classList
        .remove("oculto");

    document
        .getElementById("txtBuscarDocente")
        .focus();

    buscarDocentes("");

}

/**
 * Cierra el modal.
 */
function cerrarBuscadorDocente(){

    document
        .getElementById("modalDocente")
        .classList
        .add("oculto");

}

function configurarEventosBuscador(){

    const txt =
        document.getElementById("txtBuscarDocente");

    txt.addEventListener("keyup", function(){

        buscarDocentes(txt.value);

    });

}

async function buscarDocentes(texto){

    const respuesta =
        await fetch("/api/docentes/buscar?texto=" + encodeURIComponent(texto));

    const docentes = await respuesta.json();

    llenarTablaDocentes(docentes);

}


function llenarTablaDocentes(docentes){

    const tbody =
        document.getElementById("tbodyDocentes");

    tbody.innerHTML = "";

    docentes.forEach(docente=>{

        tbody.innerHTML += `

        <tr>

            <td>${docente.cedula}</td>

            <td>${docente.nombres} ${docente.apellidos}</td>

            <td>${docente.facultad ?? ""}</td>

            <td>${docente.carrera ?? ""}</td>

            <td>

                <button
                    class="btn-seleccionar"
                    onclick="seleccionarDocente(${docente.idDocente})">

                    Seleccionar

                </button>

            </td>

        </tr>

        `;

    });

}


async function seleccionarDocente(idDocente){

    const respuesta =
        await fetch("/api/docentes/" + idDocente);

    const docente =
        await respuesta.json();

    cerrarBuscadorDocente();

    if(callbackSeleccionDocente){

        callbackSeleccionDocente(docente);

    }

}

let docentePendiente = null;

function abrirModalRol(docente){

    docentePendiente = docente;

    document
        .getElementById("modalRol")
        .classList
        .remove("oculto");

}

function cerrarModalRol(){

    document
        .getElementById("modalRol")
        .classList
        .add("oculto");

}

function confirmarRol(){

    const rol =
        document.getElementById("rolParticipante").value;

    cerrarModalRol();

    if(window.onRolSeleccionado){

        window.onRolSeleccionado(docentePendiente, rol);

    }

}

