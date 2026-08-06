(() => {


let rankingCompletoCapitulos = [];
//=========================================
// MÓDULO CAPITULOS
//=========================================

function obtenerResultadoCapitulos(){

    return document.getElementById("resultadoCapitulos");

}

function obtenerTablaRankingCapitulos(){

    return document.getElementById("tablaRankingCapitulos");

}



function obtenerArchivoCapitulos(){

    return document.getElementById("archivoCapitulos").files[0];

}

//=========================================
// INICIALIZAR
//=========================================

const form = document.getElementById("formImportacionCapitulos");

if(form){

    form.addEventListener("submit",async e=>{

        e.preventDefault();

        await importarCapitulos();

    });

}

//=========================================
// IMPORTAR
//=========================================

async function importarCapitulos() {

    if (!validarProcesoEditable()) return;


    const archivo = obtenerArchivoCapitulos();
const idProceso = obtenerIdProceso();

    if (!archivo) {

        mostrarResultadoCapitulos(
            "Seleccione un archivo.",
            false
        );

        return;

    }

    const formData = new FormData();

    formData.append("archivo", archivo);
    formData.append("idProceso", idProceso);

    mostrarResultadoCapitulos(
        "Importando capítulos de libro...",
        true
    );

    try {

        const response = await fetch(
    "/api/importaciones/capitulos-libro",
    {
        method: "POST",
        body: formData
    }
);

        const texto = await response.text();

        mostrarResultadoCapitulos(
            texto,
            response.ok
        );

    }

    catch (e) {

        mostrarResultadoCapitulos(
            e.message,
            false
        );

    }

}

//=========================================
// CALCULAR
//=========================================

    async function calcularPuntajesCapitulos() {
    if (!validarProcesoEditable()) return;

    const idProceso = obtenerIdProceso();

    mostrarResultadoCapitulos(
        "Calculando puntajes...",
        true
    );

    try {

        const response = await fetch(

            "/api/calculos/capitulos-libro?idProceso=" +
            encodeURIComponent(idProceso),

            {
                method: "POST"
            }

        );

        const texto = await response.text();

        mostrarResultadoCapitulos(
            texto,
            response.ok
        );

        if (response.ok) {

            await cargarRankingCapitulos();

        }

    }

    catch (e) {

        mostrarResultadoCapitulos(
            e.message,
            false
        );

    }

}

//=========================================
// RANKING
//=========================================

 async function cargarRankingCapitulos() {

    const idProceso = obtenerIdProceso();

    try {

        const response = await fetch(

            "/api/calculos/capitulos-libro/ranking?idProceso=" +
            encodeURIComponent(idProceso)

        );

        if (!response.ok) {

            mostrarResultadoCapitulos(
                await response.text(),
                false
            );

            return;

        }

        const datos = await response.json();

rankingCompletoCapitulos = datos;

cargarCarreras();

aplicarFiltros();

document
.getElementById("filtrosRankingCapitulos")
.classList.remove("oculto");

document
.getElementById("tablaRankingContenedorCapitulos")
.classList.remove("oculto");

    }

    catch (e) {

        mostrarResultadoCapitulos(
            e.message,
            false
        );

    }

}

//=========================================
// MENSAJES
//=========================================

function mostrarResultadoCapitulos(mensaje, exito) {

    const resultado = obtenerResultadoCapitulos();

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

    const tabla = obtenerTablaRankingCapitulos();

    let html = "";

    datos.forEach(item => {

        html += `

        <tr>

            <td>${item.posicion ?? ""}</td>

            <td>${item.cedula ?? ""}</td>

            <td>${item.apellidos ?? ""} ${item.nombres ?? ""}</td>

            <td>${item.carrera ?? "Sin carrera"}</td>

            <td><strong>${Number(item.puntajeCapitulos ?? 0).toFixed(2)}</strong></td>

        </tr>

        `;

    });

    tabla.innerHTML = html;

}


function cargarCarreras(){

    const select = document.getElementById("filtroCarreraCapitulos");

    if(!select) return;

    select.innerHTML = "<option value=''>Todas las carreras</option>";

    const carreras = [
        ...new Set(
            rankingCompletoCapitulos.map(x => x.carrera)
                .filter(c => c)
        )
    ];

    carreras.sort();

    carreras.forEach(c=>{

        select.innerHTML += `<option value="${c}">${c}</option>`;

    });

}

function aplicarFiltros(){

    const txtCedula=document.getElementById("filtroCedulaCapitulos");
    const txtDocente=document.getElementById("filtroDocenteCapitulos");
    const cmbCarrera=document.getElementById("filtroCarreraCapitulos");
    const cmbFacultad=document.getElementById("filtroFacultadCapitulos");
    const cmbTop=document.getElementById("filtroTopCapitulos");

    if(!txtCedula) return;

    const cedula = txtCedula.value.toLowerCase();
    const docente = txtDocente.value.toLowerCase();
    const carrera = cmbCarrera.value;
    const facultad = cmbFacultad.value;
    const top = parseInt(cmbTop.value);

    let datos = rankingCompletoCapitulos.filter(item=>{

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
        e.target.id==="filtroCedulaCapitulos" ||
        e.target.id==="filtroDocenteCapitulos"
    ){

        aplicarFiltros();

    }

});

document.addEventListener("change", function(e){

    if(
        e.target.id==="filtroCarreraCapitulos" ||
        e.target.id==="filtroFacultadCapitulos" ||
        e.target.id==="filtroTopCapitulos"
    ){

        aplicarFiltros();

    }

});


window.importarCapitulos = importarCapitulos;
window.calcularPuntajesCapitulos = calcularPuntajesCapitulos;
window.cargarRankingCapitulos = cargarRankingCapitulos;

(async () => {
    await cargarProcesoActivo();
})();

})();