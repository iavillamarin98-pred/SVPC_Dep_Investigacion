(() => {

    // =========================================
    // MÓDULO PROCEEDINGS
    // SVPC
    // =========================================

    let rankingProceedings = [];


    // =========================================
    // ELEMENTOS DEL DOM
    // =========================================

    function obtenerResultadoProceedings() {

        return document.getElementById(
            "resultadoProceedings"
        );

    }


    function obtenerTablaRankingProceedings() {

        return document.getElementById(
            "tablaRankingProceedings"
        );

    }


    function obtenerArchivoProceedings() {

        const input =
            document.getElementById(
                "archivoProceedings"
            );

        return input?.files?.[0];

    }


    // =========================================
    // INICIALIZAR FORMULARIO
    // =========================================

    (function inicializarFormProceedings() {

        const form =
            document.getElementById(
                "formImportacionProceedings"
            );

        if (
            form &&
            !form.dataset.listenerAgregado
        ) {

            form.addEventListener(
                "submit",
                async function (e) {

                    e.preventDefault();

                    await importarProceedings();

                }
            );

            form.dataset.listenerAgregado =
                "true";

        }

    })();


    // =========================================
    // IMPORTAR
    // =========================================

    async function importarProceedings() {

        if (!validarProcesoEditable()) {
            return;
        }

        const archivo =
            obtenerArchivoProceedings();

        const idProceso =
            obtenerIdProceso();


        if (!archivo) {

            mostrarResultadoProceedings(
                "Seleccione un archivo.",
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


        mostrarResultadoProceedings(
            "Importando Proceedings...",
            true
        );


        try {

            const response =
                await fetch(
                    "/api/importaciones/proceedings",
                    {
                        method: "POST",
                        body: formData
                    }
                );


            const texto =
                await response.text();


            mostrarResultadoProceedings(
                texto,
                response.ok
            );


        } catch (error) {

            console.error(error);

            mostrarResultadoProceedings(
                "Error al conectar con el servidor: " +
                error.message,
                false
            );

        }

    }


    // =========================================
    // CALCULAR PUNTAJES
    // =========================================

    async function calcularPuntajesProceedings() {

        if (!validarProcesoEditable()) {
            return;
        }

        const idProceso =
            obtenerIdProceso();


        mostrarResultadoProceedings(
            "Calculando puntajes...",
            true
        );


        try {

            const response =
                await fetch(

                    "/api/calculos/proceedings?idProceso=" +
                    encodeURIComponent(idProceso),

                    {
                        method: "POST"
                    }

                );


            const texto =
                await response.text();


            if (!response.ok) {

                mostrarResultadoProceedings(
                    texto,
                    false
                );

                return;

            }


            mostrarResultadoProceedings(
                texto,
                true
            );


            await cargarRankingProceedings();


        } catch (error) {

            console.error(error);

            mostrarResultadoProceedings(
                "Error al conectar con el servidor: " +
                error.message,
                false
            );

        }

    }


    // =========================================
    // CARGAR RANKING
    // =========================================

    async function cargarRankingProceedings() {

        const idProceso =
            obtenerIdProceso();


        mostrarResultadoProceedings(
            "Cargando ranking...",
            true
        );


        try {

            const response =
                await fetch(

                    "/api/calculos/proceedings/ranking?idProceso=" +
                    encodeURIComponent(idProceso)

                );


            if (!response.ok) {

                const error =
                    await response.text();

                mostrarResultadoProceedings(
                    error,
                    false
                );

                return;

            }


            const datos =
                await response.json();


            // Guardar ranking original
            rankingProceedings =
                datos;


            // Cargar facultades
            cargarFacultadesRankingProceedings();


            // Mostrar ranking
            mostrarRankingProceedings(
                rankingProceedings
            );


            const contenedor =
                document.getElementById(
                    "tablaRankingContenedorProceedings"
                );


            if (contenedor) {

                contenedor.classList.remove(
                    "oculto"
                );

            }


            mostrarResultadoProceedings(
                "Ranking cargado correctamente.",
                true
            );


        } catch (error) {

            console.error(error);

            mostrarResultadoProceedings(
                "Error al cargar ranking: " +
                error.message,
                false
            );

        }

    }


    // =========================================
    // CARGAR FACULTADES
    // =========================================

    function cargarFacultadesRankingProceedings() {

        const select =
            document.getElementById(
                "filtroProceedingFacultad"
            );


        if (!select) {
            return;
        }


        const facultades = [

            ...new Set(

                rankingProceedings

                    .map(item =>
                        obtenerFacultad(
                            item.carrera
                        )
                    )

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

                select.innerHTML += `

                    <option value="${facultad}">
                        ${facultad}
                    </option>

                `;

            }
        );

    }


    // =========================================
    // FILTRAR RANKING
    // CÉDULA + NOMBRE + FACULTAD
    // =========================================

    function filtrarRankingProceedings() {

        const input =
            document.getElementById(
                "filtroProceedingDocente"
            );


        const select =
            document.getElementById(
                "filtroProceedingFacultad"
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
            rankingProceedings.filter(
                item => {


                    const cedula =
                        String(
                            item.cedula ?? ""
                        )
                        .toUpperCase();


                    const nombre =
                        `${item.nombres ?? ""} ${
                            item.apellidos ?? ""
                        }`
                        .trim()
                        .toUpperCase();


                    const facultad =
                        obtenerFacultad(
                            item.carrera
                        );


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


        mostrarRankingProceedings(
            filtrados
        );

    }


    // =========================================
    // MOSTRAR RANKING
    // =========================================

    function mostrarRankingProceedings(lista) {

        const tabla =
            obtenerTablaRankingProceedings();


        if (!tabla) {
            return;
        }


        tabla.innerHTML = "";


        if (
            !lista ||
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


                const nombreCompleto =
                    `${item.nombres ?? ""} ${
                        item.apellidos ?? ""
                    }`
                    .trim();


                const facultad =
                    obtenerFacultad(
                        item.carrera
                    );


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
                            ${facultad}
                        </td>

                        <td>
                            ${item.carrera ?? ""}
                        </td>

                        <td>

                            <strong>

                                ${
                                    Number(
                                        item.puntajeProceedings ??
                                        item.puntajeArticulos ??
                                        0
                                    ).toFixed(2)
                                }

                            </strong>

                        </td>

                    </tr>

                `;

            }
        );

    }


    // =========================================
    // CONFIGURAR FILTROS
    // =========================================

    function configurarFiltrosRankingProceedings() {

        const texto =
            document.getElementById(
                "filtroProceedingDocente"
            );


        const facultad =
            document.getElementById(
                "filtroProceedingFacultad"
            );


        if (texto) {

            texto.addEventListener(
                "input",
                filtrarRankingProceedings
            );

        }


        if (facultad) {

            facultad.addEventListener(
                "change",
                filtrarRankingProceedings
            );

        }

    }


    // =========================================
    // MENSAJES
    // =========================================

    function mostrarResultadoProceedings(
        mensaje,
        exito
    ) {

        const resultado =
            obtenerResultadoProceedings();


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


    // =========================================
    // EXPONER FUNCIONES
    // =========================================

    window.importarProceedings =
        importarProceedings;

    window.calcularPuntajesProceedings =
        calcularPuntajesProceedings;

    window.cargarRankingProceedings =
        cargarRankingProceedings;

    window.filtrarRankingProceedings =
        filtrarRankingProceedings;


    // =========================================
    // INICIALIZACIÓN
    // =========================================

    (async () => {

        try {

            configurarFiltrosRankingProceedings();

            await cargarProcesoActivo();

        } catch (e) {

            console.error(e);

        }

    })();


})();