(() => {

// ==================================================
// MÓDULO ARTÍCULOS
// SVPC
// ==================================================

let rankingArticulos = [];


// ==================================================
// ELEMENTOS DEL DOM
// ==================================================

function obtenerResultado() {

    return document.getElementById(
        "resultadoArticulos"
    );

}

function obtenerTablaRanking() {

    return document.getElementById(
        "tablaRankingArticulos"
    );

}

function obtenerArchivo() {

    const input =
        document.getElementById("archivoArticulos");

    return input?.files?.[0];

}


// ==================================================
// IMPORTAR ARTÍCULOS
// ==================================================

async function importarArticulos() {

    if (!validarProcesoEditable()) {
        return;
    }

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

        const texto =
            await response.text();

        if (!response.ok) {

            mostrarResultadoArticulos(
                texto,
                false
            );

            return;
        }

        mostrarResultadoArticulos(
            texto,
            true
        );

    } catch (error) {

        console.error(error);

        mostrarResultadoArticulos(
            "Error al conectar con el servidor: " +
            error.message,
            false
        );

    }

}


// ==================================================
// CALCULAR PUNTAJES
// ==================================================

async function calcularPuntajesArticulos() {

    if (!validarProcesoEditable()) {
        return;
    }

    const idProceso =
        obtenerIdProceso();

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

        const texto =
            await response.text();

        if (!response.ok) {

            mostrarResultadoArticulos(
                texto,
                false
            );

            return;
        }

        mostrarResultadoArticulos(
            texto,
            true
        );

        // Después de calcular,
        // cargar nuevamente el ranking.
        await cargarRankingArticulos();

    } catch (error) {

        console.error(error);

        mostrarResultadoArticulos(

            "Error al conectar con el servidor: " +
            error.message,

            false

        );

    }

}


// ==================================================
// CARGAR RANKING
// ==================================================

async function cargarRankingArticulos() {

    const idProceso =
        obtenerIdProceso();

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

            const error =
                await response.text();

            mostrarResultadoArticulos(
                error,
                false
            );

            return;
        }

        const ranking =
            await response.json();

        // Guardar ranking original
        rankingArticulos = ranking;

        // Cargar facultades
        cargarFacultadesRankingArticulos();

        // Mostrar ranking completo
        mostrarRankingArticulos(
            rankingArticulos
        );

        mostrarResultadoArticulos(
            "Ranking cargado correctamente.",
            true
        );

    } catch (error) {

        console.error(error);

        mostrarResultadoArticulos(

            "Error al cargar ranking: " +
            error.message,

            false

        );

    }

}


// ==================================================
// CARGAR FACULTADES DEL RANKING
// ==================================================

function cargarFacultadesRankingArticulos() {

    const select =
        document.getElementById("filtroArticuloFacultad");

    if (!select) {
        return;
    }

    const facultades = [
        ...new Set(
            rankingArticulos
                .map(item => obtenerFacultad(item.carrera))
                .filter(facultad => facultad !== "")
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


// ==================================================
// FILTRAR RANKING
// CÉDULA + NOMBRE + FACULTAD
// ==================================================

function filtrarRankingArticulos() {

    const input =
        document.getElementById(
            "filtroArticuloDocente"
        );

    const select =
        document.getElementById(
            "filtroArticuloFacultad"
        );

    const texto =
        input
            ? input.value
                .trim()
                .toUpperCase()
            : "";

    const facultad =
        select
            ? select.value
            : "";

    const filtrados =
        rankingArticulos.filter(item => {

            const cedula =
                String(
                    item.cedula ?? ""
                ).toUpperCase();

            const nombre =
                `${item.nombres ?? ""} ${item.apellidos ?? ""}`
                    .trim()
                    .toUpperCase();

            const coincideTexto =

                texto === "" ||

                cedula.includes(texto) ||

                nombre.includes(texto);

            const facultadDocente =
    obtenerFacultad(item.carrera);

const coincideFacultad =
    facultad === "" ||
    facultadDocente === facultad;

            return (
                coincideTexto &&
                coincideFacultad
            );

        });

    mostrarRankingArticulos(
        filtrados
    );

}


// ==================================================
// MOSTRAR RANKING
// ==================================================

function mostrarRankingArticulos(lista) {

    const tabla =
        obtenerTablaRanking();

    if (!tabla) {
        return;
    }

    tabla.innerHTML = "";

    if (!lista || lista.length === 0) {

        tabla.innerHTML = `

            <tr>

                <td
                    colspan="6"
                    style="text-align:center;"
                >

                    No se encontraron docentes.

                </td>

            </tr>

        `;

        return;
    }

    lista.forEach((item, indice) => {

        const nombreCompleto =

            `${item.nombres ?? ""} ${item.apellidos ?? ""}`
                .trim();

        tabla.innerHTML += `

            <tr>

                <td>
                    ${indice + 1}
                </td>

                <td>
                    ${item.cedula ?? ""}
                </td>

                <td>

                    <strong>
                        ${nombreCompleto}
                    </strong>

                </td>

                <td>
    ${obtenerFacultad(item.carrera)}
</td>

                <td>
                    ${item.carrera ?? ""}
                </td>

                <td>

                    <strong>
                        ${
                            Number(
                                item.puntajeArticulos
                            ).toFixed(2)
                        }
                    </strong>

                </td>

            </tr>

        `;

    });

}


// ==================================================
// CONFIGURAR FILTROS
// ==================================================

function configurarFiltrosRankingArticulos() {

    const texto =
        document.getElementById(
            "filtroArticuloDocente"
        );

    const facultad =
        document.getElementById(
            "filtroArticuloFacultad"
        );

    if (texto) {

        texto.addEventListener(
            "input",
            filtrarRankingArticulos
        );

    }

    if (facultad) {

        facultad.addEventListener(
            "change",
            filtrarRankingArticulos
        );

    }

}


// ==================================================
// MENSAJES
// ==================================================

function mostrarResultadoArticulos(
    mensaje,
    exito
) {

    const resultado =
        obtenerResultado();

    if (!resultado) {
        return;
    }

    resultado.classList.remove(
        "oculto",
        "exito",
        "error"
    );

    resultado.textContent =
        mensaje;

    if (exito) {

        resultado.classList.add(
            "exito"
        );

    } else {

        resultado.classList.add(
            "error"
        );

    }

}


// ==================================================
// EXPONER FUNCIONES AL HTML
// ==================================================

window.importarArticulos =
    importarArticulos;

window.calcularPuntajesArticulos =
    calcularPuntajesArticulos;

window.cargarRankingArticulos =
    cargarRankingArticulos;

window.filtrarRankingArticulos =
    filtrarRankingArticulos;


// ==================================================
// INICIALIZACIÓN
// ==================================================

(async () => {

    try {

        configurarFiltrosRankingArticulos();

        await cargarProcesoActivo();

    } catch (e) {

        console.error(e);

    }

})();

})();