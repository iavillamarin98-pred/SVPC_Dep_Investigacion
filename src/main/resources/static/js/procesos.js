(() => {

const API_PROCESOS = "/api/procesos";

let procesos = [];
let editando = false;

//=========================================
// INICIALIZAR
//=========================================

(async function () {

    await cargarProcesoActivo();

    await listarProcesos();

})();

async function listarProcesos() {

    const response = await fetch(API_PROCESOS);

    procesos = await response.json();

    pintarTabla(procesos);

}

function pintarTabla(datos){

    const tabla = document.getElementById("tablaProcesos");

    tabla.innerHTML = "";

    datos.forEach(p=>{

        tabla.innerHTML += `

        <tr>

            <td>${p.idProceso}</td>

            <td>${p.nombre}</td>

            <td>${p.periodo}</td>

            <td>${p.estado}</td>

            <td>${p.fechaCreacion.substring(0,10)}</td>

            <td>

                <button
                    class="btn btn-warning"
                    onclick="editarProceso(${p.idProceso})">

                    Editar

                </button>

                <button
                    class="btn btn-success"
                    onclick="activarProceso(${p.idProceso})"
                    ${p.estado==="ACTIVO"?"disabled":""}>

                    ${p.estado==="ACTIVO"?"Activo":"Activar"}

                </button>

                <button
                    class="btn btn-danger"
                    onclick="eliminarProceso(${p.idProceso})">

                    Eliminar

                </button>

            </td>

        </tr>

        `;

    });

}

async function guardarProceso(){

    const dto={

        nombre:document.getElementById("nombreProceso").value,

        descripcion:document.getElementById("descripcionProceso").value,

        periodo:document.getElementById("periodoProceso").value,

        estado:document.getElementById("estadoProceso").value

    };

    const id=document.getElementById("idProceso").value;

    let url=API_PROCESOS;
    let metodo="POST";

    if(id){

        url+="/"+id;

        metodo="PUT";

    }

    const response=await fetch(url,{

        method:metodo,

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify(dto)

    });

    if(response.ok){

        limpiarFormularioProceso();

        await listarProcesos();

        await cargarProcesoActivo();

    }

    else{

        alert(await response.text());

    }

}


async function editarProceso(id){

    const response=await fetch(API_PROCESOS+"/"+id);

    const p=await response.json();

    document.getElementById("idProceso").value=p.idProceso;

    document.getElementById("nombreProceso").value=p.nombre;

    document.getElementById("descripcionProceso").value=p.descripcion;

    document.getElementById("periodoProceso").value=p.periodo;

    document.getElementById("estadoProceso").value=p.estado;

}

async function activarProceso(id){

    if(!confirm("¿Activar este proceso?")){

        return;

    }

    const response=await fetch(

        API_PROCESOS+"/"+id+"/activar",

        {

            method:"PUT"

        }

    );

    if(response.ok){

        await listarProcesos();

        await cargarProcesoActivo();

    }

    else{

        alert(await response.text());

    }

}

async function eliminarProceso(id){

    if(!confirm("¿Eliminar este proceso?")){

        return;

    }

    const response=await fetch(

        API_PROCESOS+"/"+id,

        {

            method:"DELETE"

        }

    );

    if(response.ok){

        await listarProcesos();

        await cargarProcesoActivo();

    }

}

function limpiarFormularioProceso(){

    document.getElementById("formProceso").reset();

    document.getElementById("idProceso").value="";

}

function filtrarProcesos(){

    const texto=document
        .getElementById("buscarProceso")
        .value
        .toLowerCase();

    const filtrados=procesos.filter(p=>

        p.nombre.toLowerCase().includes(texto)

    );

    pintarTabla(filtrados);

    }

    //=========================================
// FUNCIONES PÚBLICAS
//=========================================

window.guardarProceso = guardarProceso;
window.editarProceso = editarProceso;
window.activarProceso = activarProceso;
window.eliminarProceso = eliminarProceso;
window.limpiarFormularioProceso = limpiarFormularioProceso;
window.filtrarProcesos = filtrarProcesos;

})();
    
