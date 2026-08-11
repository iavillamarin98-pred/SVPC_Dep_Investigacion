(() => {

    // ==================================================
    // MÓDULO LIBROS
    // ==================================================

    let rankingLibros = [];


    // ==================================================
    // ELEMENTOS DEL DOM
    // ==================================================

    function obtenerResultadoLibros() {

        return document.getElementById(
            "resultadoLibros"
        );

    }


    function obtenerTablaRankingLibros() {

        return document.getElementById(
            "tablaRankingLibros"
        );

    }


    function obtenerArchivoLibros() {

        const input =
            document.getElementById(
                "archivoLibros"
            );

        return input?.files?.[0];

    }


    // ==================================================
    // IMPORTAR LIBROS
    // ==================================================

    async function importarLibros() {

        if (!validarProcesoEditable()) {
            return;
        }

        const archivo =
            obtenerArchivoLibros();

        const idProceso =
            obtenerIdProceso();

        if (!archivo) {

            mostrarResultadoLibros(
                "Debe seleccionar un archivo Excel.",
                false
            );

            return;

        }

        const formData =
            new FormData();

        formData.append(
            "archivo",
            archivo
        );

        formData.append(
            "idProceso",
            idProceso
        );

        mostrarResultadoLibros(
            "Importando libros...",
            true
        );

        try {

            const response =
                await fetch(
                    "/api/importaciones/libros",
                    {
                        method: "POST",
                        body: formData
                    }
                );

            const texto =
                await response.text();

            mostrarResultadoLibros(
                texto,
                response.ok
            );

        } catch (error) {

            console.error(error);

            mostrarResultadoLibros(
                "Error al importar libros: " +
                error.message,
                false
            );

        }

    }


    // ==================================================
    // CALCULAR PUNTAJES
    // ==================================================

    async function calcularPuntajesLibros() {

        if (!validarProcesoEditable()) {
            return;
        }

        const idProceso =
            obtenerIdProceso();

        mostrarResultadoLibros(
            "Calculando puntajes...",
            true
        );

        try {

            const response =
                await fetch(

                    "/api/calculos/libros?idProceso=" +
                    encodeURIComponent(idProceso),

                    {
                        method: "POST"
                    }

                );

            const texto =
                await response.text();

            mostrarResultadoLibros(
                texto,
                response.ok
            );

            if (response.ok) {

                await cargarRankingLibros();

            }

        } catch (error) {

            console.error(error);

            mostrarResultadoLibros(
                "Error al calcular puntajes: " +
                error.message,
                false
            );

        }

    }


    // ==================================================
    // CARGAR RANKING
    // ==================================================

    async function cargarRankingLibros() {

    // Asegurar que el proceso esté cargado
    if (!PROCESO_ACTIVO) {

        await cargarProcesoActivo();

    }

    // Si definitivamente no existe proceso,
    // no intentar consultar el ranking
    if (!PROCESO_ACTIVO) {

        mostrarResultadoLibros(
            "No existe un proceso de valoración activo.",
            false
        );

        return;

    }

    const idProceso =
        obtenerIdProceso();

    mostrarResultadoLibros(
        "Cargando ranking...",
        true
    );

    try {

        const response =
            await fetch(
                "/api/calculos/libros/ranking?idProceso=" +
                encodeURIComponent(idProceso)
            );

        if (!response.ok) {

            const error =
                await response.text();

            mostrarResultadoLibros(
                error,
                false
            );

            return;

        }

        const datos =
            await response.json();

        console.log(
            "Ranking de libros recibido:",
            datos
        );

        rankingLibros =
            Array.isArray(datos)
                ? datos
                : [];

        cargarFacultadesRankingLibros();

        filtrarRankingLibros();

        mostrarResultadoLibros(
            "Ranking cargado correctamente.",
            true
        );

    } catch (error) {

        console.error(
            "Error cargando ranking de libros:",
            error
        );

        mostrarResultadoLibros(
            "Error al cargar ranking: " +
            error.message,
            false
        );

    }

}

    // ==================================================
    // CARGAR FACULTADES
    // ==================================================

    function cargarFacultadesRankingLibros() {

        const select =
            document.getElementById(
                "filtroLibroFacultad"
            );


        if (!select) {
            return;
        }


        const facultades =
            [
                ...new Set(

                    rankingLibros
                        .map(item => {

                            if (item.facultad) {

                                return item.facultad;

                            }

                            if (
                                typeof obtenerFacultad ===
                                "function"
                            ) {

                                return obtenerFacultad(
                                    item.carrera
                                );

                            }

                            return "";

                        })

                        .filter(
                            facultad =>
                                facultad &&
                                facultad.trim() !== ""
                        )

                )
            ].sort();


        select.innerHTML = `

            <option value="">
                Todas las facultades
            </option>

        `;


        facultades.forEach(
            facultad => {

                const option =
                    document.createElement(
                        "option"
                    );

                option.value =
                    facultad;

                option.textContent =
                    facultad;

                select.appendChild(
                    option
                );

            }
        );

    }


    // ==================================================
    // FILTRAR RANKING
    // CÉDULA + NOMBRE + FACULTAD
    // ==================================================

    function filtrarRankingLibros() {

        const input =
            document.getElementById(
                "filtroLibroDocente"
            );


        const select =
            document.getElementById(
                "filtroLibroFacultad"
            );


        const texto =
            input
                ? input.value
                    .trim()
                    .toUpperCase()
                : "";


        const facultadSeleccionada =
            select
                ? select.value
                : "";


        const filtrados =
            rankingLibros.filter(
                item => {


                    const cedula =
                        String(
                            item.cedula ?? ""
                        )
                        .toUpperCase();


                    const nombre =
                        `${item.nombres ?? ""} ${item.apellidos ?? ""}`
                            .trim()
                            .toUpperCase();


                    let facultad =
                        item.facultad ?? "";


                    if (
                        !facultad &&
                        typeof obtenerFacultad ===
                        "function"
                    ) {

                        facultad =
                            obtenerFacultad(
                                item.carrera
                            );

                    }


                    const coincideTexto =

                        texto === "" ||

                        cedula.includes(
                            texto
                        ) ||

                        nombre.includes(
                            texto
                        );


                    const coincideFacultad =

                        facultadSeleccionada === "" ||

                        facultad ===
                            facultadSeleccionada;


                    return (

                        coincideTexto &&
                        coincideFacultad

                    );

                }
            );


        mostrarRankingLibros(
            filtrados
        );

    }


    // ==================================================
    // MOSTRAR RANKING
    // ==================================================

    function mostrarRankingLibros(lista) {

    const tabla = obtenerTablaRankingLibros();

    if (!tabla) {
        console.error("No existe #tablaRankingLibros");
        return;
    }

    tabla.innerHTML = "";

    if (!Array.isArray(lista) || lista.length === 0) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6" style="text-align:center;">
                    No se encontraron docentes.
                </td>
            </tr>
        `;

        return;
    }

    lista.forEach((item, indice) => {

        const nombreCompleto =
            `${item.apellidos ?? ""} ${item.nombres ?? ""}`.trim();

        const carrera =
            item.carrera ??
            "Sin carrera";

        const facultad =
            item.facultad ??
            obtenerFacultad(carrera) ??
            "Sin facultad";

        const puntaje =
            Number(
                item.puntajeLibros ??
                item.puntaje ??
                0
            );

        const puesto =
            item.puesto ??
            item.posicion ??
            (indice + 1);

        const fila =
            document.createElement("tr");

        fila.innerHTML = `
            <td>${puesto}</td>

            <td>${item.cedula ?? ""}</td>

            <td>
                <strong>
                    ${nombreCompleto}
                </strong>
            </td>

            <td>
                ${facultad}
            </td>

            <td>
                ${carrera}
            </td>

            <td>
                <strong>
                    ${puntaje.toFixed(2)}
                </strong>
            </td>
        `;

        tabla.appendChild(fila);

    });
}


    // ==================================================
    // CONFIGURAR FILTROS
    // ==================================================

    function configurarFiltrosLibros() {

        const input =
            document.getElementById(
                "filtroLibroDocente"
            );


        const select =
            document.getElementById(
                "filtroLibroFacultad"
            );


        if (input) {

            input.addEventListener(
                "input",
                filtrarRankingLibros
            );

        }


        if (select) {

            select.addEventListener(
                "change",
                filtrarRankingLibros
            );

        }

    }


    // ==================================================
    // MENSAJES
    // ==================================================

    function mostrarResultadoLibros(
        mensaje,
        exito
    ) {

        const resultado =
            obtenerResultadoLibros();


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


        resultado.classList.add(
            exito
                ? "exito"
                : "error"
        );

    }


    // ==================================================
    // EXPONER FUNCIONES
    // ==================================================

    window.importarLibros =
        importarLibros;


    window.calcularPuntajesLibros =
        calcularPuntajesLibros;


    window.cargarRankingLibros =
        cargarRankingLibros;


    window.filtrarRankingLibros =
        filtrarRankingLibros;


 // ==================================================
// INICIALIZACIÓN
// ==================================================

(async () => {

    try {

        configurarFiltrosLibros();

        // Cargar proceso activo primero
        await cargarProcesoActivo();

        // Intentar cargar ranking únicamente
        // cuando el proceso ya fue cargado
        try {

            await cargarRankingLibros();

        } catch (error) {

            console.warn(
                "No se pudo cargar el ranking automáticamente:",
                error
            );

        }

    } catch (error) {

        console.error(
            "Error inicializando módulo Libros:",
            error
        );

        mostrarResultadoLibros(
            "Error inicializando Libros: " +
            error.message,
            false
        );

    }

})();


})();