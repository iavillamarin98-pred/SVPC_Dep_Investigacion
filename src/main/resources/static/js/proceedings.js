//=========================================
// MÓDULO PROCEEDINGS
//=========================================

function obtenerResultadoProceedings() {
    return document.getElementById("resultadoProceedings");
}

function obtenerTablaRankingProceedings() {
    return document.getElementById("tablaRankingProceedings");
}

function obtenerProcesoProceedings() {
    return document.getElementById("idProcesoProceedings").value;
}

function obtenerArchivoProceedings() {
    return document.getElementById("archivoProceedings").files[0];
}

//=========================================
// INICIALIZAR
//=========================================
// Envuelto en IIFE para no declarar "form" en el scope global
// del documento (evita el SyntaxError al recargar el módulo).
// El dataset.listenerAgregado evita agregar el listener duplicado
// si el nodo del formulario persiste entre cargas del módulo.

(function inicializarFormProceedings() {

    const form = document.getElementById("formImportacionProceedings");

    if (form && !form.dataset.listenerAgregado) {

        form.addEventListener("submit", async (e) => {

            e.preventDefault();

            await importarProceedings();

        });

        form.dataset.listenerAgregado = "true";

    }

})();

//=========================================
// IMPORTAR
//=========================================

async function importarProceedings() {

    const archivo = obtenerArchivoProceedings();
    const idProceso = obtenerProcesoProceedings();

    if (!archivo) {

        mostrarResultadoProceedings(
            "Seleccione un archivo.",
            false
        );

        return;

    }

    const formData = new FormData();

    formData.append("archivo", archivo);
    formData.append("idProceso", idProceso);

    mostrarResultadoProceedings(
        "Importando proceedings...",
        true
    );

    try {

        const response = await fetch(
            "/api/importaciones/proceedings",
            {
                method: "POST",
                body: formData
            }
        );

        const texto = await response.text();

        mostrarResultadoProceedings(
            texto,
            response.ok
        );

    } catch (e) {

        mostrarResultadoProceedings(
            e.message,
            false
        );

    }

}

//=========================================
// CALCULAR
//=========================================

async function calcularPuntajesProceedings() {

    const idProceso = obtenerProcesoProceedings();

    if (!idProceso) {

        mostrarResultadoProceedings(
            "Seleccione un proceso de valoración.",
            false
        );

        return;
    }

    mostrarResultadoProceedings(
        "Calculando puntajes...",
        true
    );

    try {

        const response = await fetch(

            "/api/calculos/proceedings?idProceso=" +
            encodeURIComponent(idProceso),

            {
                method: "POST"
            }

        );

        const texto = await response.text();

        mostrarResultadoProceedings(
            texto,
            response.ok
        );

        if (response.ok) {

            await cargarRankingProceedings();

        }

    } catch (e) {

        mostrarResultadoProceedings(
            e.message,
            false
        );

    }

}

//=========================================
// RANKING
//=========================================

async function cargarRankingProceedings() {

    const idProceso = obtenerProcesoProceedings();

    if (!idProceso) {
        mostrarResultadoProceedings(
            "Seleccione un proceso.",
            false
        );
        return;
    }

    mostrarResultadoProceedings(
        "Cargando ranking...",
        true
    );

    try {

        const response = await fetch(
            "/api/calculos/proceedings/ranking?idProceso=" +
            encodeURIComponent(idProceso)
        );

        if (!response.ok) {
            const error = await response.text();
            mostrarResultadoProceedings(error, false);
            return;
        }

        const datos = await response.json();

        const tabla = obtenerTablaRankingProceedings();
        tabla.innerHTML = "";

        if (datos.length === 0) {
            mostrarResultadoProceedings(
                "No existen puntajes para este proceso.",
                false
            );
            return;
        }

        datos.forEach(item => {

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
                        ${Number(item.puntajeArticulos ?? 0).toFixed(2)}
                    </strong>
                </td>
            `;

            tabla.appendChild(fila);

        });

        const contenedor = document.getElementById("tablaRankingContenedorProceedings");
        if (contenedor) {
            contenedor.classList.remove("oculto");
        }

        mostrarResultadoProceedings(
            "Ranking cargado correctamente.",
            true
        );

    } catch (error) {

        mostrarResultadoProceedings(
            "Error al cargar ranking: " + error.message,
            false
        );

    }

}

//=========================================
// MENSAJES
//=========================================

function mostrarResultadoProceedings(mensaje, exito) {

    const resultado = obtenerResultadoProceedings();

    if (!resultado) {
        console.error("No existe el elemento resultadoProceedings");
        return;
    }

    resultado.classList.remove("oculto");
    resultado.classList.remove("exito");
    resultado.classList.remove("error");

    resultado.textContent = mensaje;

    resultado.classList.add(exito ? "exito" : "error");
}