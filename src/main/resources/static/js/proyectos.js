const API = "/api/proyectos";

let participantes = [];



let idProyectoEditar = null;




function agregarParticipante(){

    abrirBuscadorDocente(function(docente){

        if(participantes.some(p => p.idDocente === docente.idDocente)){

            alert("El docente ya fue agregado.");

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

        alert("Ya existe un Director.");

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

    if (participantes.length === 0) {

        alert("Debe agregar al menos un participante.");

        return;

    }

    if (!participantes.some(p => p.rol === "DIRECTOR")) {

        alert("Debe existir un Director.");

        return;

    }

    const dto = {

        proyecto: {

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

        alert(
            idProyectoEditar == null
                ? "Proyecto registrado correctamente."
                : "Proyecto actualizado correctamente."
        );

        limpiarFormulario();

        listarProyectos();

    } catch (error) {

        alert(error.message);

    }

}

function limpiarFormulario() {

    idProyectoEditar = null;

    document.getElementById("formProyecto").reset();

    participantes = [];

    actualizarTablaParticipantes();

    document.getElementById("btnGuardarProyecto").textContent =
        "Guardar Proyecto";

}

window.onload = async function () {

    await cargarBuscadorDocente();

    await cargarProcesos();

    listarProyectos();

};

async function listarProyectos() {

    const respuesta = await fetch(API);

    const proyectos = await respuesta.json();

    const tbody =
        document.getElementById("tablaProyectos");

    tbody.innerHTML = "";

    proyectos.forEach(proyecto => {

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
                    onclick="eliminarProyecto(${proyecto.idProyecto})">

                    Eliminar

                </button>

            </td>

        </tr>

        `;

    });

}


async function eliminarProyecto(idProyecto) {

    if (!confirm("¿Desea eliminar este proyecto?")) {

        return;

    }

    await fetch(API + "/" + idProyecto, {

        method: "DELETE"

    });

    listarProyectos();

}



async function editarProyecto(idProyecto){

    const respuestaProyecto =
        await fetch(API + "/" + idProyecto);

    const proyecto =
        await respuestaProyecto.json();
    
    

    idProyectoEditar = proyecto.idProyecto;

    document.getElementById("nombre").value =
        proyecto.nombre;

    document.getElementById("descripcion").value =
        proyecto.descripcion ?? "";

    document.getElementById("periodo").value =
        proyecto.periodo ?? "";

    document.getElementById("tipoFinanciamiento").value =
        proyecto.tipoFinanciamiento;

    document.getElementById("estado").value =
        proyecto.estado;
    
    document.getElementById("btnGuardarProyecto").textContent =
    "Actualizar Proyecto";

    const respuestaParticipantes =
        await fetch(API + "/" + idProyecto + "/participantes");

    participantes =
        await respuestaParticipantes.json();

    actualizarTablaParticipantes();

}


async function importarProyectosInternos() {

    const archivo =
        document.getElementById("archivoProyecto").files[0];

    if (!archivo) {

        alert("Seleccione un archivo.");

        return;

    }

    const formData = new FormData();

    formData.append("archivo", archivo);

    formData.append(
    "idProceso",
    document.getElementById("idProceso").value
);

    const respuesta = await fetch(
        "/api/importaciones/proyectos",
        {
            method: "POST",
            body: formData
        });

    const mensaje = await respuesta.text();

    alert(mensaje);

    listarProyectos();

}


async function cargarRankingProyectos() {

    const idProceso = document.getElementById("idProceso").value;

    const respuesta = await fetch(

        `/api/calculos/proyectos/ranking?idProceso=${idProceso}`

    );

    const ranking = await respuesta.json();

    const tbody = document.getElementById("tablaRankingProyectos");

    tbody.innerHTML = "";

    ranking.forEach((docente, indice) => {

        tbody.innerHTML += `
            <tr>

                <td>${indice + 1}</td>

                <td>${docente.cedula}</td>

                <td>${docente.docente}</td>

                <td>${docente.facultad}</td>

                <td>${docente.carrera}</td>

                <td>${docente.puntaje.toFixed(2)}</td>

            </tr>
        `;

    });

}

async function calcularPuntajesProyectos() {

    const idProceso = document.getElementById("idProceso").value;

    const respuesta = await fetch(

        `/api/calculos/proyectos?idProceso=${idProceso}`,

        {
            method: "POST"
        }

    );

    const mensaje = await respuesta.text();

    alert(mensaje);

}

async function cargarProcesos() {

    const respuesta = await fetch("/api/procesos");

    const procesos = await respuesta.json();

    const combo = document.getElementById("idProceso");

    combo.innerHTML = "";

    procesos.forEach(proceso => {

        combo.innerHTML += `
            <option value="${proceso.idProceso}">
                ${proceso.nombre}
            </option>
        `;

    });

}

