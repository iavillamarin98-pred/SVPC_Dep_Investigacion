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

        try {

            // ------------------------------------------
            // VALIDAR PROCESO
            // ------------------------------------------

            if (!validarProcesoEditable()) {
                return;
            }


            // ------------------------------------------
            // ASEGURAR PROCESO ACTIVO
            // ------------------------------------------

            if (!PROCESO_ACTIVO) {

                await cargarProcesoActivo();

            }


            if (!PROCESO_ACTIVO) {

                mostrarResultadoLibros(
                    "No existe un proceso de valoración activo.",
                    false
                );

                return;

            }


            // ------------------------------------------
            // VALIDAR ARCHIVO
            // ------------------------------------------

            const archivo =
                obtenerArchivoLibros();


            if (!archivo) {

                mostrarResultadoLibros(
                    "Debe seleccionar un archivo Excel.",
                    false
                );

                return;

            }


            // ------------------------------------------
            // ID DEL PROCESO
            // ------------------------------------------

            const idProceso =
                PROCESO_ACTIVO.idProceso;


            console.log(
                "Importando libros:",
                archivo.name
            );

            console.log(
                "Proceso:",
                idProceso
            );


            // ------------------------------------------
            // FORMDATA
            // ------------------------------------------

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


            // ------------------------------------------
            // PETICIÓN
            // ------------------------------------------

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


            console.log(
                "Respuesta importación libros:",
                response.status,
                texto
            );


            // ------------------------------------------
            // RESULTADO
            // ------------------------------------------

            mostrarResultadoLibros(
                texto,
                response.ok
            );


            // ------------------------------------------
            // IMPORTACIÓN EXITOSA
            // ------------------------------------------

            if (response.ok) {

    const input =
        document.getElementById(
            "archivoLibros"
        );

    if (input) {

        input.value = "";

    }


    // ------------------------------------------
    // ACTUALIZAR RANKING SIN BORRAR
    // LA RETROALIMENTACIÓN DE IMPORTACIÓN
    // ------------------------------------------

    await cargarRankingLibros(false);

}

        } catch (error) {

            console.error(
                "Error importando libros:",
                error
            );


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

        try {

            if (!validarProcesoEditable()) {
                return;
            }


            if (!PROCESO_ACTIVO) {

                await cargarProcesoActivo();

            }


            if (!PROCESO_ACTIVO) {

                mostrarResultadoLibros(
                    "No existe un proceso de valoración activo.",
                    false
                );

                return;

            }


            const idProceso =
                PROCESO_ACTIVO.idProceso;


            mostrarResultadoLibros(
                "Calculando puntajes...",
                true
            );


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

                await cargarRankingLibros(false);

            }

        } catch (error) {

            console.error(
                "Error calculando puntajes:",
                error
            );


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

async function cargarRankingLibros(mostrarMensaje = true) {

    try {

        // ------------------------------------------
        // ASEGURAR PROCESO
        // ------------------------------------------

        if (!PROCESO_ACTIVO) {

            await cargarProcesoActivo();

        }


        if (!PROCESO_ACTIVO) {

            if (mostrarMensaje) {

                mostrarResultadoLibros(
                    "No existe un proceso de valoración activo.",
                    false
                );

            }

            mostrarRankingLibros([]);

            return;

        }


        const idProceso =
            PROCESO_ACTIVO.idProceso;


        // ------------------------------------------
        // MENSAJE DE CARGA
        // ------------------------------------------

        if (mostrarMensaje) {

            mostrarResultadoLibros(
                "Cargando ranking...",
                true
            );

        }


        // ------------------------------------------
        // CONSULTAR BACKEND
        // ------------------------------------------

        const response =
            await fetch(
                "/api/calculos/libros/ranking?idProceso=" +
                encodeURIComponent(idProceso)
            );


        if (!response.ok) {

            const error =
                await response.text();

            throw new Error(
                error || "Error al cargar ranking."
            );

        }


        // ------------------------------------------
        // JSON
        // ------------------------------------------

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


        // ------------------------------------------
        // FACULTADES
        // ------------------------------------------

        cargarFacultadesRankingLibros();


        // ------------------------------------------
        // MOSTRAR TABLA
        // ------------------------------------------

        filtrarRankingLibros();


        // ------------------------------------------
        // MENSAJE FINAL
        // ------------------------------------------

        if (mostrarMensaje) {

            mostrarResultadoLibros(
                "Ranking cargado correctamente.",
                true
            );

        }

    } catch (error) {

        console.error(
            "Error cargando ranking de libros:",
            error
        );


        if (mostrarMensaje) {

            mostrarResultadoLibros(
                "Error al cargar ranking: " +
                error.message,
                false
            );

        }

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
            ]
            .sort();


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

                    // ----------------------------------
                    // CÉDULA
                    // ----------------------------------

                    const cedula =
                        String(
                            item.cedula ?? ""
                        )
                        .toUpperCase();


                    // ----------------------------------
                    // NOMBRE
                    // ----------------------------------

                    const nombre =
                        `${item.nombres ?? ""} ${item.apellidos ?? ""}`
                            .trim()
                            .toUpperCase();


                    // ----------------------------------
                    // FACULTAD
                    // ----------------------------------

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


                    // ----------------------------------
                    // FILTRO TEXTO
                    // ----------------------------------

                    const coincideTexto =

                        texto === "" ||

                        cedula.includes(texto) ||

                        nombre.includes(texto);


                    // ----------------------------------
                    // FILTRO FACULTAD
                    // ----------------------------------

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

        const tabla =
            obtenerTablaRankingLibros();


        if (!tabla) {

            console.error(
                "No existe #tablaRankingLibros"
            );

            return;

        }


        tabla.innerHTML = "";


        if (
            !Array.isArray(lista) ||
            lista.length === 0
        ) {

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


        lista.forEach(
            (item, indice) => {

                // ----------------------------------
                // NOMBRE
                // ----------------------------------

                const nombreCompleto =
                    `${item.apellidos ?? ""} ${item.nombres ?? ""}`
                        .trim();


                // ----------------------------------
                // CARRERA
                // ----------------------------------

                const carrera =
                    item.carrera ??
                    "Sin carrera";


                // ----------------------------------
                // FACULTAD
                // ----------------------------------

                let facultad =
                    item.facultad ?? "";


                if (
                    !facultad &&
                    typeof obtenerFacultad ===
                    "function"
                ) {

                    facultad =
                        obtenerFacultad(
                            carrera
                        );

                }


                if (!facultad) {

                    facultad =
                        "Sin facultad";

                }


                // ----------------------------------
                // PUNTAJE
                // ----------------------------------

                const puntaje =
                    Number(
                        item.puntajeLibros ??
                        item.puntaje ??
                        0
                    );


                // ----------------------------------
                // PUESTO
                // ----------------------------------

                const puesto =
                    item.puesto ??
                    item.posicion ??
                    (indice + 1);


                // ----------------------------------
                // FILA
                // ----------------------------------

                const fila =
                    document.createElement(
                        "tr"
                    );


                fila.innerHTML = `

                    <td>
                        ${puesto}
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


                tabla.appendChild(
                    fila
                );

            }
        );

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
    // CONFIGURAR FORMULARIO
    // ==================================================

    function configurarFormularioLibros() {

        const formulario =
            document.getElementById(
                "formImportacionLibros"
            );


        if (!formulario) {

            console.warn(
                "No existe #formImportacionLibros"
            );

            return;

        }


        formulario.addEventListener(
            "submit",
            async function (event) {

                // --------------------------------------
                // EVITAR RECARGA DE INDEX
                // --------------------------------------

                event.preventDefault();


                console.log(
                    "Submit de importación de libros capturado."
                );


                await importarLibros();

            }
        );

    }


    // ==================================================
    // MENSAJES
    // ==================================================

 // ==================================================
// MENSAJES / RETROALIMENTACIÓN
// ==================================================

function mostrarResultadoLibros(mensaje, exito) {

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


    // ==================================================
    // RETROALIMENTACIÓN DE IMPORTACIÓN
    // ==================================================

    if (
        exito &&
        typeof mensaje === "string" &&
        mensaje.includes(
            "Importación de libros finalizada"
        )
    ) {


        // ------------------------------------------
        // FUNCIÓN PARA EXTRAER CANTIDADES
        // ------------------------------------------

        const extraerNumero = (campo) => {

            const regex =
                new RegExp(
                    campo +
                    "\\s*:\\s*(\\d+)",
                    "i"
                );

            const coincidencia =
                mensaje.match(regex);

            return coincidencia
                ? Number(coincidencia[1])
                : 0;

        };


        // ------------------------------------------
        // DATOS
        // ------------------------------------------

        const librosInsertados =
            extraerNumero(
                "Libros insertados"
            );


        const librosActualizados =
            extraerNumero(
                "libros actualizados"
            );


        const docentesInsertados =
            extraerNumero(
                "docentes insertados"
            );


        const docentesActualizados =
            extraerNumero(
                "docentes actualizados"
            );


        const relacionesGuardadas =
            extraerNumero(
                "relaciones guardadas"
            );


        const relacionesActualizadas =
            extraerNumero(
                "relaciones actualizadas"
            );


        const filasOmitidas =
            extraerNumero(
                "filas omitidas"
            );


        // ------------------------------------------
        // CLASE
        // ------------------------------------------

        resultado.classList.add(
            "exito"
        );


        // ------------------------------------------
        // HTML
        // ------------------------------------------

        resultado.innerHTML = `

            <div class="retroalimentacion-importacion">

                <div class="retro-titulo">

                    <i class="fa-solid fa-circle-check"></i>

                    Importación de libros finalizada

                </div>


                <div class="retro-grid">

                    <div class="retro-item">

                        <span>
                            Libros insertados
                        </span>

                        <strong>
                            ${librosInsertados}
                        </strong>

                    </div>


                    <div class="retro-item">

                        <span>
                            Libros actualizados
                        </span>

                        <strong>
                            ${librosActualizados}
                        </strong>

                    </div>


                    <div class="retro-item">

                        <span>
                            Docentes insertados
                        </span>

                        <strong>
                            ${docentesInsertados}
                        </strong>

                    </div>


                    <div class="retro-item">

                        <span>
                            Docentes actualizados
                        </span>

                        <strong>
                            ${docentesActualizados}
                        </strong>

                    </div>


                    <div class="retro-item">

                        <span>
                            Relaciones guardadas
                        </span>

                        <strong>
                            ${relacionesGuardadas}
                        </strong>

                    </div>


                    <div class="retro-item">

                        <span>
                            Relaciones actualizadas
                        </span>

                        <strong>
                            ${relacionesActualizadas}
                        </strong>

                    </div>


                    <div class="retro-item retro-omitidas">

                        <span>
                            Filas omitidas
                        </span>

                        <strong>
                            ${filasOmitidas}
                        </strong>

                    </div>

                </div>

            </div>

        `;


        return;

    }


    // ==================================================
    // MENSAJE NORMAL
    // ==================================================

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
    // INICIALIZACIÓN ÚNICA
    // ==================================================

    (async function inicializarLibros() {

        try {

            console.log(
                "Inicializando módulo Libros..."
            );


            // ------------------------------------------
            // CONFIGURAR UNA SOLA VEZ
            // ------------------------------------------

            configurarFiltrosLibros();

            configurarFormularioLibros();


            // ------------------------------------------
            // PROCESO ACTIVO
            // ------------------------------------------

            if (
                typeof cargarProcesoActivo !==
                "function"
            ) {

                throw new Error(
                    "No está cargado proceso-activo.js"
                );

            }


            await cargarProcesoActivo();


            // ------------------------------------------
            // RANKING
            // ------------------------------------------

            if (PROCESO_ACTIVO) {

                await cargarRankingLibros();

            } else {

                mostrarRankingLibros([]);

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