(() => {
// ===============================
// MÓDULO ARTÍCULOS MUNDIALES
// SVPC
// ===============================

function obtenerResultado() {
    return document.getElementById("resultadoArticulos");
}

function obtenerTablaRanking() {
    return document.getElementById("tablaRankingArticulos");
}



function obtenerArchivo() {
    return document.getElementById("archivoArticulos").files[0];
}

// =======================================
// IMPORTAR ARTÍCULOS
// =======================================

    async function importarArticulos() {
    if (!validarProcesoEditable()) return;

    const archivo = obtenerArchivo();
    const idProceso = obtenerIdProceso();

    if (!archivo) {
        mostrarResultadoArticulos(
            "Debe seleccionar un archivo Excel.",
            false
        );
        return;
    }

    const formData = new FormData();

    formData.append("archivo", archivo);
    formData.append("idProceso", idProceso);

    mostrarResultadoArticulos(
        "Importando artículos...",
        true
    );

    try {

        const response = await fetch(
            "/api/importaciones/articulos",
            {
                method: "POST",
                body: formData
            }
        );

        const texto = await response.text();

        if (response.ok) {

            mostrarResultadoArticulos(texto, true);

        } else {

            mostrarResultadoArticulos(texto, false);

        }

    } catch (error) {

        mostrarResultadoArticulos(
            "Error al conectar con el servidor: " + error.message,
            false
        );

    }

}

// =======================================
// CALCULAR PUNTAJES
// =======================================

    async function calcularPuntajesArticulos() {
    if (!validarProcesoEditable()) return;

    const idProceso = obtenerIdProceso();

    

    mostrarResultadoArticulos(
        "Calculando puntajes...",
        true
    );

    try {

        const response = await fetch(

            "/api/calculos/articulos?idProceso=" +
            encodeURIComponent(idProceso),

            {
                method: "POST"
            }

        );

        const texto = await response.text();

        if (response.ok) {

            mostrarResultadoArticulos(texto, true);

            await cargarRankingArticulos();

        } else {

            mostrarResultadoArticulos(texto, false);

        }

    } catch (error) {

        mostrarResultadoArticulos(

            "Error al conectar con el servidor: " +
            error.message,

            false

        );

    }

}

// =======================================
// RANKING
// =======================================

async function cargarRankingArticulos() {

    const idProceso = obtenerIdProceso();

    

    mostrarResultadoArticulos(
        "Cargando ranking...",
        true
    );

    try {

        const response = await fetch(

            "/api/calculos/articulos/ranking?idProceso=" +
            encodeURIComponent(idProceso)

        );

        if (!response.ok) {

            const error = await response.text();

            mostrarResultadoArticulos(
                error,
                false
            );

            return;
        }

        const ranking = await response.json();

        const tabla = obtenerTablaRanking();

        tabla.innerHTML = "";

        if (ranking.length === 0) {

            mostrarResultadoArticulos(

                "No existen puntajes para este proceso.",

                false

            );

            return;
        }

        ranking.forEach(item => {

            const fila = document.createElement("tr");

            const nombreCompleto =
                `${item.nombres ?? ""} ${item.apellidos ?? ""}`.trim();

            fila.innerHTML = `

                <td>${item.puesto}</td>

                <td>${item.cedula ?? ""}</td>

                <td>${nombreCompleto}</td>

                <td>${item.carrera ?? ""}</td>

                <td>

                    <strong>

                        ${Number(item.puntajeArticulos).toFixed(2)}

                    </strong>

                </td>

            `;

            tabla.appendChild(fila);

        });

        mostrarResultadoArticulos(

            "Ranking cargado correctamente.",

            true

        );

    }

    catch (error) {

        mostrarResultadoArticulos(

            "Error al cargar ranking: " +
            error.message,

            false

        );

    }

}

// =======================================
// MENSAJES
// =======================================

function mostrarResultadoArticulos(
    mensaje,
    exito
) {

    const resultado = obtenerResultado();

    resultado.classList.remove("oculto");
    resultado.classList.remove("exito");
    resultado.classList.remove("error");

    resultado.textContent = mensaje;

    if (exito) {

        resultado.classList.add("exito");

    } else {

        resultado.classList.add("error");

    }

}


// Exponer solo las funciones que usa el HTML
window.importarArticulos = importarArticulos;
window.calcularPuntajesArticulos = calcularPuntajesArticulos;
window.cargarRankingArticulos = cargarRankingArticulos;

(async () => {
    try {
        await cargarProcesoActivo();
    } catch (e) {
        console.error(e);
    }
})();

})();