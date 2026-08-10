let docentesListado = [];

async function importarDocentes() {

    const input = document.getElementById("archivoDocentes");

    if (!input.files || input.files.length === 0) {

        Notificaciones.advertencia(
            "Seleccione un archivo Excel."
        );

        return;
    }

    const archivo = input.files[0];

    if (!archivo.name.toLowerCase().endsWith(".xlsx")) {

        Notificaciones.advertencia(
            "El archivo debe tener formato .xlsx."
        );

        return;
    }

    const formData = new FormData();

    formData.append("archivo", archivo);

    try {

        Loader.mostrar("Importando docentes...");

        const respuesta = await fetch(
            "/api/importaciones/docentes",
            {
                method: "POST",
                body: formData
            }
        );

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado);
        }

        document.getElementById(
            "resultadoImportacionDocentes"
        ).innerHTML = `
            <div class="alert alert-success">
                ${resultado}
            </div>
        `;

        Notificaciones.exito(
            "Docentes importados correctamente."
        );

        input.value = "";

    } catch (error) {

        console.error(error);

        document.getElementById(
            "resultadoImportacionDocentes"
        ).innerHTML = `
            <div class="alert alert-danger">
                Error al importar docentes:
                ${error.message}
            </div>
        `;

        Notificaciones.error(
            "Error al importar docentes."
        );

    } finally {

        Loader.ocultar();

    }
}




async function cargarDocentes() {

    try {

        Loader.mostrar("Cargando docentes...");

        const respuesta =
            await fetch("/api/docentes");

        if (!respuesta.ok) {
            throw new Error("No se pudieron cargar los docentes.");
        }

        docentesListado = await respuesta.json();

        cargarFiltroFacultades();

        mostrarDocentes();

    } catch (error) {

        console.error(error);

        Notificaciones.error(
            "Error al cargar docentes."
        );

    } finally {

        Loader.ocultar();

    }
}

function cargarFiltroFacultades() {

    const select =
        document.getElementById("filtroFacultad");

    if (!select) {
        return;
    }

    const facultades = [
        ...new Set(
            docentesListado
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

function mostrarDocentes() {

    const filtroElement =
        document.getElementById("filtroDocente");

    const facultadElement =
        document.getElementById("filtroFacultad");

    const contador =
        document.getElementById("contadorDocentes");

    if (!filtroElement || !facultadElement) {

        console.warn(
            "No se encontraron los filtros de docentes."
        );

        return;
    }

    const texto =
        filtroElement.value
            .trim()
            .toUpperCase();

    const facultad =
        facultadElement.value;

    const docentesFiltrados =
        docentesListado.filter(docente => {

            const nombreCompleto =
                `${docente.nombres || ""} ${docente.apellidos || ""}`
                    .toUpperCase();

            const cedula =
                (docente.cedula || "")
                    .toUpperCase();

            const coincideTexto =
                texto === "" ||
                cedula.includes(texto) ||
                nombreCompleto.includes(texto);

            const coincideFacultad =
                facultad === "" ||
                docente.facultad === facultad;

            return coincideTexto &&
                   coincideFacultad;

        });


    // Actualizar contador

    if (contador) {

        contador.textContent =
            `${docentesFiltrados.length} docentes`;

    }


    llenarTablaListado(docentesFiltrados);

}

function llenarTablaListado(docentes) {

    const tbody =
        document.getElementById(
            "tbodyListadoDocentes"
        );

    if (!tbody) {
        return;
    }

    tbody.innerHTML = "";

    if (docentes.length === 0) {

        tbody.innerHTML = `
            <tr>

                <td
                    colspan="5"
                    style="text-align:center;">

                    No se encontraron docentes.

                </td>

            </tr>
        `;

        return;
    }


    docentes.forEach(docente => {

        tbody.innerHTML += `

            <tr>

                <td>
                    ${docente.cedula || ""}
                </td>

                <td>
                    <strong>
                        ${docente.nombres || ""}
                        ${docente.apellidos || ""}
                    </strong>
                </td>

                <td>
                    ${docente.facultad || ""}
                </td>

                <td>
                    ${docente.carrera || ""}
                </td>

                <td>

                    ${
                        docente.estado
                            ? `
                                <span class="estado-activo">
                                    Activo
                                </span>
                              `
                            : `
                                <span class="estado-inactivo">
                                    Inactivo
                                </span>
                              `
                    }

                </td>

            </tr>

        `;

    });

}

function configurarFiltrosDocentes() {

    const filtro =
        document.getElementById("filtroDocente");

    const facultad =
        document.getElementById("filtroFacultad");

    if (filtro) {

        filtro.addEventListener(
            "input",
            mostrarDocentes
        );

    }

    if (facultad) {

        facultad.addEventListener(
            "change",
            mostrarDocentes
        );

    }
}


/*
 * Inicialización del módulo de docentes.
 *
 * Se ejecuta después de que app.js
 * haya cargado docentes.html.
 */
window.inicializarDocentes = function () {

    configurarFiltrosDocentes();

    cargarDocentes();

};