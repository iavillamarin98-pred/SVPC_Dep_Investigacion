(() => {

const API = "/api/proyectos";

let participantes = [];
    let proyectos = [];
    let rankingProyectos = [];
let idProyectoEditar = null;


function agregarParticipante(){

    abrirBuscadorDocente(function(docente){

        if(participantes.some(p => p.idDocente === docente.idDocente)){

            Notificaciones.advertencia("El docente ya fue agregado.");

            return;

        }

        abrirModalRol(docente);

    });

}

window.onRolSeleccionado = function(docente, rol){

    if(
        rol==="DIRECTOR" &&
        participantes.some(p=>p.rol==="DIRECTOR")
    ){

       Notificaciones.error("Ya existe un Director.");

        return;

    }

    participantes.push({

        ...docente,

        rol

    });

    actualizarTablaParticipantes();

};

function actualizarTablaParticipantes() {

    const tbody =
        document.getElementById("tablaParticipantes");

    tbody.innerHTML = "";

    participantes.forEach((p, index) => {

        tbody.innerHTML += `

        <tr>

            <td>${p.cedula}</td>

            <td>${p.nombres} ${p.apellidos}</td>

            <td>${p.facultad ?? ""}</td>

            <td>${p.carrera ?? ""}</td>

            <td>${p.rol}</td>

            <td>

                <button
                    class="btn btn-warning"
                    onclick="eliminarParticipante(${index})">

                    Quitar

                </button>

            </td>

        </tr>

        `;

    });

}

function eliminarParticipante(index) {

    participantes.splice(index, 1);

    actualizarTablaParticipantes();

}

    async function guardarProyecto() {
    
if (!validarProcesoEditable()) return;
    if (participantes.length === 0) {

        Notificaciones.advertencia("Debe agregar al menos un participante.");

        return;

    }

    if (!participantes.some(p => p.rol === "DIRECTOR")) {

        Notificaciones.error("Debe existir un Director.");

        return;

    }

    const dto = {

    proyecto: {

        idProceso: obtenerIdProceso(),

        nombre: document.getElementById("nombre").value.trim(),

        descripcion: document.getElementById("descripcion").value.trim(),

        periodo: document.getElementById("periodo").value.trim(),

        tipoFinanciamiento: document.getElementById("tipoFinanciamiento").value,

        estado: document.getElementById("estado").value

    },

    participantes: participantes.map(p => ({

        idDocente: p.idDocente,

        rol: p.rol

    }))
};

    Loader.mostrar(
        idProyectoEditar == null
            ? "Guardando proyecto..."
            : "Actualizando proyecto..."
    );

    try {

        let url = API + "/completo";

        let metodo = "POST";

        if (idProyectoEditar != null) {

            url = API + "/completo/" + idProyectoEditar;

            metodo = "PUT";

        }

        const respuesta = await fetch(url, {

            method: metodo,

            headers: {

                "Content-Type": "application/json"

            },

            body: JSON.stringify(dto)

        });

        if (!respuesta.ok) {

            throw new Error("No se pudo guardar el proyecto.");

        }

        Notificaciones.exito(

            idProyectoEditar == null
                ? "Proyecto registrado correctamente."
                : "Proyecto actualizado correctamente."

        );

        limpiarFormulario();

        await listarProyectos();

    } catch (error) {

        Notificaciones.error(error.message);

    } finally {

        Loader.ocultar();

    }

}

function limpiarFormulario() {

    idProyectoEditar = null;

    document.getElementById("formProyecto").reset();

    participantes = [];

    actualizarTablaParticipantes();

    document.getElementById("btnGuardarProyecto").textContent =
        "Guardar Proyecto";
    document.getElementById("tipoFinanciamiento").disabled = false;

}



(async () => {

    try {

        await cargarProcesoActivo();

        await cargarBuscadorDocente();
        configurarFiltrosRankingProyectos();

        await listarProyectos();

    } catch (e) {

        console.error(e);

    }

})();

async function listarProyectos() {

    const respuesta = await fetch(
        API + "?idProceso=" + obtenerIdProceso()
    );

    proyectos = await respuesta.json();

    renderizarProyectos(proyectos);

}

function renderizarProyectos(lista){

    const tbody =
        document.getElementById("tablaProyectos");

    tbody.innerHTML = "";

    lista.forEach(proyecto => {

        tbody.innerHTML += `

        <tr>

            <td>${proyecto.idProyecto}</td>

            <td>${proyecto.nombre}</td>

            <td>${proyecto.tipoFinanciamiento}</td>

            <td>${proyecto.periodo ?? ""}</td>

            <td>${proyecto.estado}</td>

            <td>

                <button
                    class="btn btn-primary"
                    onclick="editarProyecto(${proyecto.idProyecto})">

                    Editar

                </button>

                <button
                    class="btn btn-warning"
                    data-requiere-edicion
                    onclick="eliminarProyecto(${proyecto.idProyecto})">

                    Eliminar

                </button>

            </td>

        </tr>

        `;

    });

}

function filtrarProyectos(){

    const texto =
        document
            .getElementById("buscarProyecto")
            .value
            .toLowerCase()
            .trim();

    const filtrados = proyectos.filter(p =>

        p.nombre.toLowerCase().includes(texto)

    );

    renderizarProyectos(filtrados);

}




    function eliminarProyecto(idProyecto) {
    
        if (!validarProcesoEditable()) return;

    Confirmacion.mostrar(

        "Eliminar proyecto",

        "¿Está seguro de eliminar este proyecto?",

        async () => {

            Loader.mostrar("Eliminando proyecto...");

            try {

                const respuesta = await fetch(API + "/" + idProyecto, {

                    method: "DELETE"

                });

                if (!respuesta.ok) {

                    throw new Error("No se pudo eliminar el proyecto.");

                }

                Notificaciones.exito("Proyecto eliminado correctamente.");

                await listarProyectos();

            } catch (error) {

                Notificaciones.error(error.message);

            } finally {

                Loader.ocultar();

            }

        }

    );

}



async function editarProyecto(idProyecto){

    const respuestaProyecto =
        await fetch(API + "/" + idProyecto);

    const proyecto =
        await respuestaProyecto.json();
    if (!respuestaProyecto.ok) {

    Notificaciones.error("No se pudo obtener el proyecto.");

    return;

}
    

    idProyectoEditar = proyecto.idProyecto;

    document.getElementById("nombre").value =
        proyecto.nombre;

    document.getElementById("descripcion").value =
        proyecto.descripcion ?? "";

    document.getElementById("periodo").value =
        proyecto.periodo ?? "";

    document.getElementById("tipoFinanciamiento").value =
        proyecto.tipoFinanciamiento;
    const cmbTipo =
    document.getElementById("tipoFinanciamiento");

if (proyecto.tipoFinanciamiento === "INTERNO") {

    cmbTipo.disabled = true;

} else {

    cmbTipo.disabled = false;

}
    

    document.getElementById("estado").value =
        proyecto.estado;
    
    document.getElementById("btnGuardarProyecto").textContent =
    "Actualizar Proyecto";

    const respuestaParticipantes =
        await fetch(API + "/" + idProyecto + "/participantes");

    participantes =
        await respuestaParticipantes.json();

    actualizarTablaParticipantes();

    // Cambiar automáticamente a la pestaña Registro
Tabs.activar("tabRegistro");

// Llevar la vista al inicio del formulario
window.scrollTo({
    top: 0,
    behavior: "smooth"
});



}


async function importarProyectosInternos() {

    const archivo =
        document.getElementById("archivoProyecto").files[0];

    if (!archivo) {

        Notificaciones.advertencia("Seleccione un archivo.");

        return;

    }

    const formData = new FormData();

    formData.append("archivo", archivo);

    formData.append("idProceso", obtenerIdProceso());

    Loader.mostrar("Importando proyectos...");

    try {

        const respuesta = await fetch(

            "/api/importaciones/proyectos",

            {
                method: "POST",
                body: formData
            }

        );

        const mensaje = await respuesta.text();

        if (!respuesta.ok) {

            throw new Error(mensaje);

        }

        Notificaciones.exito(mensaje);

        await listarProyectos();

    } catch (error) {

        Notificaciones.error(error.message);

    } finally {

        Loader.ocultar();

    }

}

async function cargarRankingProyectos() {

    const idProceso = obtenerIdProceso();

    try {

        Loader.mostrar("Cargando ranking de proyectos...");

        const respuesta = await fetch(
            `/api/calculos/proyectos/ranking?idProceso=${idProceso}`
        );

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar el ranking.");
        }

        rankingProyectos = await respuesta.json();

        cargarFiltroFacultadesRanking();

        mostrarRankingProyectos();

        Tabs.activar("tabRanking");

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {

        console.error(error);

        Notificaciones.error(
            "Error al cargar el ranking de proyectos."
        );

    } finally {

        Loader.ocultar();

    }
    }

    function mostrarRankingProyectos() {

    const texto =
        document
            .getElementById("filtroRankingProyecto")
            .value
            .trim()
            .toUpperCase();

    const facultad =
        document
            .getElementById("filtroRankingFacultad")
            .value;

    const filtrados =
        rankingProyectos.filter(docente => {

            const cedula =
                (docente.cedula || "")
                    .toUpperCase();

            const nombre =
                (docente.docente || "")
                    .toUpperCase();

            const coincideTexto =
                texto === "" ||
                cedula.includes(texto) ||
                nombre.includes(texto);

            const coincideFacultad =
                facultad === "" ||
                docente.facultad === facultad;

            return coincideTexto &&
                   coincideFacultad;

        });

    renderizarRankingProyectos(filtrados);

    }
    
    function renderizarRankingProyectos(lista) {

    const tbody =
        document.getElementById("tablaRankingProyectos");

    tbody.innerHTML = "";

    if (lista.length === 0) {

        tbody.innerHTML = `
            <tr>

                <td colspan="6"
                    style="text-align:center;">

                    No se encontraron docentes.

                </td>

            </tr>
        `;

        return;
    }

    lista.forEach((docente, indice) => {

        tbody.innerHTML += `

            <tr>

                <td>
                    ${indice + 1}
                </td>

                <td>
                    ${docente.cedula || ""}
                </td>

                <td>
                    <strong>
                        ${docente.docente || ""}
                    </strong>
                </td>

                <td>
                    ${docente.facultad || ""}
                </td>

                <td>
                    ${docente.carrera || ""}
                </td>

                <td>
                    <strong>
                        ${Number(docente.puntaje || 0).toFixed(2)}
                    </strong>
                </td>

            </tr>

        `;

    });

}
    
    function cargarFiltroFacultadesRanking() {

    const select =
        document.getElementById("filtroRankingFacultad");

    if (!select) {
        return;
    }

    const facultades = [
        ...new Set(
            rankingProyectos
                .map(d => d.facultad)
                .filter(f => f && f.trim() !== "")
        )
    ].sort();

    select.innerHTML = `
        <option value="">
            Todas las facultades
        </option>
    `;

    facultades.forEach(facultad => {

        select.innerHTML += `
            <option value="${facultad}">
                ${facultad}
            </option>
        `;

    });

}

    function calcularPuntajesProyectos() {
    
        if (!validarProcesoEditable()) return;

    Confirmacion.mostrar(

        "Calcular puntajes",

        "Se recalcularán los puntajes de todos los proyectos del proceso seleccionado. ¿Desea continuar?",

        ejecutarCalculoProyectos

    );

}

async function ejecutarCalculoProyectos() {

    Loader.mostrar("Calculando puntajes...");

    try {

        const respuesta = await fetch(

            `/api/calculos/proyectos?idProceso=${obtenerIdProceso()}`,

            {
                method: "POST"
            }

        );

        const mensaje = await respuesta.text();

        if (!respuesta.ok) {

            throw new Error(mensaje);

        }

        Notificaciones.exito(mensaje);

        await cargarRankingProyectos();

    } catch (error) {

        Notificaciones.error(error.message);

    } finally {

        Loader.ocultar();

    }

}
/*
async function cargarProcesos() {

    const respuesta = await fetch("/api/procesos");

    const procesos = await respuesta.json();

    const combo = document.getElementById("ID_PROCESO");

    combo.innerHTML = "";

    procesos.forEach(proceso => {

        combo.innerHTML += `
            <option value="${proceso.idProceso}">
                ${proceso.nombre}
            </option>
        `;

    });

}*/

function confirmarLimpiarFormulario(){

    Confirmacion.mostrar(

        "Limpiar formulario",

        "Se perderá toda la información ingresada. ¿Desea continuar?",

        limpiarFormulario

    );

    }
    function configurarFiltrosRankingProyectos() {

    const texto =
        document.getElementById("filtroRankingProyecto");

    const facultad =
        document.getElementById("filtroRankingFacultad");

    if (texto) {

        texto.addEventListener(
            "input",
            mostrarRankingProyectos
        );

    }

    if (facultad) {

        facultad.addEventListener(
            "change",
            mostrarRankingProyectos
        );

    }

}

function mostrarNombreArchivo() {

    const archivo =
        document.getElementById("archivoProyecto").files[0];

    document.getElementById("nombreArchivo").textContent =
        archivo ? archivo.name : "Ningún archivo seleccionado";

}

window.agregarParticipante = agregarParticipante;
window.eliminarParticipante = eliminarParticipante;

window.guardarProyecto = guardarProyecto;

window.editarProyecto = editarProyecto;
window.eliminarProyecto = eliminarProyecto;

window.importarProyectosInternos = importarProyectosInternos;

window.calcularPuntajesProyectos = calcularPuntajesProyectos;
    window.cargarRankingProyectos = cargarRankingProyectos;
    window.mostrarRankingProyectos = mostrarRankingProyectos;

window.confirmarLimpiarFormulario = confirmarLimpiarFormulario;
window.mostrarNombreArchivo = mostrarNombreArchivo;

window.filtrarProyectos = filtrarProyectos;



})();