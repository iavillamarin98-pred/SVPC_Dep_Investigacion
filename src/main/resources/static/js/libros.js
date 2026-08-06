(() => {
let rankingCompleto = [];
//=========================================
// MÓDULO LIBROS
//=========================================

function obtenerResultadoLibros() {
    return document.getElementById("resultadoLibros");
}

function obtenerTablaRankingLibros() {
    return document.getElementById("tablaRankingLibros");
}



function obtenerArchivoLibros() {
    return document.getElementById("archivoLibros").files[0];
}

//=========================================
// INICIALIZAR
//=========================================

const form = document.getElementById("formImportacionLibros");



if (form) {

    form.addEventListener("submit", async function (e) {

    

    e.preventDefault();

    await importarLibros();

    });

}

//=========================================
// IMPORTAR
//=========================================

async function importarLibros() {
if (!validarProcesoEditable()) return;
    


    const archivo = obtenerArchivoLibros();
    const idProceso = obtenerIdProceso();

    if (!archivo) {

        mostrarResultadoLibros(
            "Seleccione un archivo.",
            false
        );

        return;

    }

    const formData = new FormData();

    formData.append("archivo", archivo);
    formData.append("idProceso", idProceso);

    mostrarResultadoLibros(
        "Importando libros...",
        true
    );

    try {

        const response = await fetch(
            "/api/importaciones/libros",
            {
                method: "POST",
                body: formData
            }
        );

        const texto = await response.text();

        mostrarResultadoLibros(
            texto,
            response.ok
        );

    }

    catch (e) {

        mostrarResultadoLibros(
            e.message,
            false
        );

    }

}

//=========================================
// CALCULAR
//=========================================

    async function calcularPuntajesLibros() {
    if (!validarProcesoEditable()) return;

    const idProceso = obtenerIdProceso();

    mostrarResultadoLibros(
        "Calculando puntajes...",
        true
    );

    try {

        const response = await fetch(

            "/api/calculos/libros?idProceso=" +
            encodeURIComponent(idProceso),

            {
                method: "POST"
            }

        );

        const texto = await response.text();

        mostrarResultadoLibros(
            texto,
            response.ok
        );

        if (response.ok) {

            await cargarRankingLibros();

        }

    }

    catch (e) {

        mostrarResultadoLibros(
            e.message,
            false
        );

    }

}

//=========================================
// RANKING
//=========================================

async function cargarRankingLibros() {

    const idProceso = obtenerIdProceso();

    try {

        const response = await fetch(

            "/api/calculos/libros/ranking?idProceso=" +
            encodeURIComponent(idProceso)

        );

        if (!response.ok) {

            mostrarResultadoLibros(
                await response.text(),
                false
            );

            return;

        }

        const datos = await response.json();

rankingCompleto = datos;

cargarCarreras();

aplicarFiltros();

document
.getElementById("filtrosRankingLibros")
.classList.remove("oculto");

    }

    catch (e) {

        mostrarResultadoLibros(
            e.message,
            false
        );

    }

}

//=========================================
// MENSAJES
//=========================================

function mostrarResultadoLibros(mensaje, exito) {

    const resultado = obtenerResultadoLibros();

    if (!resultado) return;

    resultado.classList.remove("oculto");
    resultado.classList.remove("exito");
    resultado.classList.remove("error");

    resultado.textContent = mensaje;

    resultado.classList.add(
        exito ? "exito" : "error"
    );

}
function pintarTabla(datos){

    const tabla = obtenerTablaRankingLibros();

    let html = "";

    datos.forEach(item => {

        html += `

        <tr>

            <td>${item.posicion ?? ""}</td>

            <td>${item.cedula ?? ""}</td>

            <td>${item.apellidos ?? ""} ${item.nombres ?? ""}</td>

            <td>${item.carrera ?? "Sin carrera"}</td>

            <td><strong>${Number(item.puntajeLibros ?? 0).toFixed(2)}</strong></td>

        </tr>

        `;

    });

    tabla.innerHTML = html;

}


function cargarCarreras(){

    const select = document.getElementById("filtroCarrera");

    if(!select) return;

    select.innerHTML = "<option value=''>Todas las carreras</option>";

    const carreras = [
        ...new Set(
            rankingCompleto
                .map(x => x.carrera)
                .filter(c => c)
        )
    ];

    carreras.sort();

    carreras.forEach(c=>{

        select.innerHTML += `<option value="${c}">${c}</option>`;

    });

}

function aplicarFiltros(){

    const txtCedula=document.getElementById("filtroCedula");
    const txtDocente=document.getElementById("filtroDocente");
    const cmbCarrera=document.getElementById("filtroCarrera");
    const cmbFacultad=document.getElementById("filtroFacultad");
    const cmbTop=document.getElementById("filtroTop");

    if(!txtCedula) return;

    const cedula = txtCedula.value.toLowerCase();
    const docente = txtDocente.value.toLowerCase();
    const carrera = cmbCarrera.value;
    const facultad = cmbFacultad.value;
    const top = parseInt(cmbTop.value);

    let datos = rankingCompleto.filter(item=>{

        const nombre=(item.apellidos+" "+item.nombres).toLowerCase();

        return item.cedula.toLowerCase().includes(cedula)

            && nombre.includes(docente)

            && (carrera==="" || item.carrera===carrera)

            && (facultad==="" || obtenerFacultad(item.carrera)===facultad);

    });

    if(top>0){

        datos=datos.slice(0,top);

    }

    pintarTabla(datos);

}
document.addEventListener("input", function(e){

    if(
        e.target.id==="filtroCedula" ||
        e.target.id==="filtroDocente"
    ){

        aplicarFiltros();

    }

});

document.addEventListener("change", function(e){

    if(
        e.target.id==="filtroCarrera" ||
        e.target.id==="filtroFacultad" ||
        e.target.id==="filtroTop"
    ){

        aplicarFiltros();

    }

});


window.importarLibros = importarLibros;
window.calcularPuntajesLibros = calcularPuntajesLibros;
window.cargarRankingLibros = cargarRankingLibros;

(async () => {
    await cargarProcesoActivo();
})();

})();