(() => {

    // ==================================================
    // MÓDULO CAPÍTULOS DE LIBRO
    // ==================================================

    let rankingCapitulos = [];


    // ==================================================
    // ELEMENTOS DEL DOM
    // ==================================================

    function obtenerResultadoCapitulos() {

        return document.getElementById(
            "resultadoCapitulos"
        );

    }


    function obtenerTablaRankingCapitulos() {

        return document.getElementById(
            "tablaRankingCapitulos"
        );

    }


    function obtenerArchivoCapitulos() {

        const input =
            document.getElementById(
                "archivoCapitulos"
            );

        return input?.files?.[0];

    }


    // ==================================================
    // IMPORTAR CAPÍTULOS
    // ==================================================

   async function importarCapitulos() {

    try {

        // ==========================================
        // VALIDAR PROCESO
        // ==========================================

        if (!validarProcesoEditable()) {
            return;
        }


        // ==========================================
        // ASEGURAR PROCESO ACTIVO
        // ==========================================

        if (!PROCESO_ACTIVO) {

            await cargarProcesoActivo();

        }

        if (!PROCESO_ACTIVO) {

            mostrarResultadoCapitulos(
                "No existe un proceso de valoración activo.",
                false
            );

            return;
        }


        // ==========================================
        // ARCHIVO
        // ==========================================

        const archivo =
            obtenerArchivoCapitulos();

        if (!archivo) {

            mostrarResultadoCapitulos(
                "Debe seleccionar un archivo Excel.",
                false
            );

            return;
        }


        const idProceso =
            PROCESO_ACTIVO.idProceso;


        console.log(
            "Importando capítulos:",
            archivo.name
        );

        console.log(
            "Proceso:",
            idProceso
        );


        // ==========================================
        // FORMDATA
        // ==========================================

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


        mostrarResultadoCapitulos(
            "Importando capítulos de libro...",
            true
        );


        // ==========================================
        // PETICIÓN
        // ==========================================

        const response =
            await fetch(
                "/api/importaciones/capitulos-libro",
                {
                    method: "POST",
                    body: formData
                }
            );


        const texto =
            await response.text();


        console.log(
            "Respuesta importación capítulos:",
            response.status,
            texto
        );


        // ==========================================
        // MOSTRAR RESULTADO DE IMPORTACIÓN
        // ==========================================

        mostrarResultadoCapitulos(
            texto,
            response.ok
        );


        // ==========================================
        // IMPORTACIÓN EXITOSA
        // ==========================================

        if (response.ok) {

            const input =
                document.getElementById(
                    "archivoCapitulos"
                );

            if (input) {
                input.value = "";
            }


            // ======================================
            // ACTUALIZAR RANKING
            // SIN SOBRESCRIBIR LA RETROALIMENTACIÓN
            // ======================================

            await cargarRankingCapitulos(false);

        }

    } catch (error) {

        console.error(
            "Error importando capítulos:",
            error
        );


        mostrarResultadoCapitulos(
            "Error al importar capítulos: " +
            error.message,
            false
        );

    }

}


    // ==================================================
    // CALCULAR PUNTAJES
    // ==================================================

    async function calcularPuntajesCapitulos() {

        try {

            if (!validarProcesoEditable()) {

                return;

            }


            if (!PROCESO_ACTIVO) {

                await cargarProcesoActivo();

            }


            if (!PROCESO_ACTIVO) {

                mostrarResultadoCapitulos(
                    "No existe un proceso de valoración activo.",
                    false
                );

                return;

            }


            const idProceso =
                PROCESO_ACTIVO.idProceso;


            mostrarResultadoCapitulos(
                "Calculando puntajes...",
                true
            );


            const response =
                await fetch(

                    "/api/calculos/capitulos-libro?idProceso=" +
                    encodeURIComponent(idProceso),

                    {
                        method: "POST"
                    }

                );


            const texto =
                await response.text();


            mostrarResultadoCapitulos(
                texto,
                response.ok
            );


            if (response.ok) {

                await cargarRankingCapitulos();

            }

        } catch (error) {

            console.error(
                "Error calculando puntajes:",
                error
            );


            mostrarResultadoCapitulos(
                "Error al calcular puntajes: " +
                error.message,
                false
            );

        }

    }


    // ==================================================
    // CARGAR RANKING
    // ==================================================

 async function cargarRankingCapitulos(
    mostrarMensaje = true
) {

    try {

        // ==========================================
        // ASEGURAR PROCESO ACTIVO
        // ==========================================

        if (!PROCESO_ACTIVO) {

            await cargarProcesoActivo();

        }


        if (!PROCESO_ACTIVO) {

            if (mostrarMensaje) {

                mostrarResultadoCapitulos(
                    "No existe un proceso de valoración activo.",
                    false
                );

            }

            rankingCapitulos = [];

            mostrarRankingCapitulos([]);

            return;

        }


        const idProceso =
            PROCESO_ACTIVO.idProceso;


        // ==========================================
        // MENSAJE
        // ==========================================

        if (mostrarMensaje) {

            mostrarResultadoCapitulos(
                "Cargando ranking...",
                true
            );

        }


        // ==========================================
        // CONSULTAR BACKEND
        // ==========================================

        const response =
            await fetch(

                "/api/calculos/capitulos-libro/ranking?idProceso=" +
                encodeURIComponent(idProceso)

            );


        if (!response.ok) {

            const error =
                await response.text();

            throw new Error(
                error ||
                "Error al cargar ranking."
            );

        }


        // ==========================================
        // RECIBIR JSON
        // ==========================================

        const datos =
            await response.json();


        console.log(
            "Ranking de capítulos recibido:",
            datos
        );


        // ==========================================
        // GUARDAR RANKING
        // ==========================================

        rankingCapitulos =
            Array.isArray(datos)
                ? datos
                : [];


        console.log(
            "Total registros ranking capítulos:",
            rankingCapitulos.length
        );


        // ==========================================
        // CARGAR FACULTADES
        // ==========================================

        cargarFacultadesRankingCapitulos();


        // ==========================================
        // MOSTRAR FILTROS
        // ==========================================

        const filtros =
            document.getElementById(
                "filtrosRankingCapitulos"
            );

        if (filtros) {

            filtros.classList.remove(
                "oculto"
            );

        }


        // ==========================================
        // MOSTRAR TABLA
        // ==========================================

        const contenedor =
            document.getElementById(
                "tablaRankingContenedorCapitulos"
            );

        if (contenedor) {

            contenedor.classList.remove(
                "oculto"
            );

        }


        // ==========================================
        // MOSTRAR DIRECTAMENTE EL RANKING
        // ==========================================

        mostrarRankingCapitulos(
            rankingCapitulos
        );


        // ==========================================
        // MENSAJE FINAL
        // ==========================================

        if (mostrarMensaje) {

            mostrarResultadoCapitulos(
                "Ranking cargado correctamente.",
                true
            );

        }

    } catch (error) {

        console.error(
            "Error cargando ranking de capítulos:",
            error
        );


        if (mostrarMensaje) {

            mostrarResultadoCapitulos(
                "Error al cargar ranking: " +
                error.message,
                false
            );

        }

    }

}





    // ==================================================
    // CARGAR CARRERAS
    // ==================================================
    function cargarCarrerasRankingCapitulos() {

    const select =
        document.getElementById(
            "filtroCarreraCapitulos"
        );


    if (!select) {

        return;

    }


    const carreras = [
        ...new Set(

            rankingCapitulos
                .map(item =>
                    item.carrera
                )
                .filter(carrera =>
                    carrera &&
                    carrera.trim() !== ""
                )

        )
    ].sort();


    select.innerHTML = `

        <option value="">
            Todas las carreras
        </option>

    `;


    carreras.forEach(
        carrera => {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                carrera;


            option.textContent =
                carrera;


            select.appendChild(
                option
            );

        }
    );

}


    // ==================================================
    // CARGAR FACULTADES
    // ==================================================

  function cargarFacultadesRankingCapitulos() {

    const select =
        document.getElementById(
            "filtroCapituloFacultad"
        );


    if (!select) {

        console.warn(
            "No existe #filtroCapituloFacultad"
        );

        return;

    }


    const facultades =
        [
            ...new Set(

                rankingCapitulos
                    .map(item => {

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

                        return facultad;

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
    // CÉDULA + NOMBRE + FACULTAD
    // ==================================================

 function filtrarRankingCapitulos() {

    const input =
        document.getElementById(
            "filtroCapituloDocente"
        );


    const select =
        document.getElementById(
            "filtroCapituloFacultad"
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
        rankingCapitulos.filter(
            item => {

                // ==================================
                // CÉDULA
                // ==================================

                const cedula =
                    String(
                        item.cedula ?? ""
                    )
                    .toUpperCase();


                // ==================================
                // NOMBRE COMPLETO
                // ==================================

                const nombre =
                    `${item.apellidos ?? ""} ${item.nombres ?? ""}`
                        .trim()
                        .toUpperCase();


                // ==================================
                // FACULTAD
                // ==================================

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


                // ==================================
                // TEXTO
                // ==================================

                const coincideTexto =

                    texto === "" ||

                    cedula.includes(
                        texto
                    ) ||

                    nombre.includes(
                        texto
                    );


                // ==================================
                // FACULTAD
                // ==================================

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


    // ==========================================
    // MOSTRAR RESULTADO
    // ==========================================

    mostrarRankingCapitulos(
        filtrados
    );

}


    // ==================================================
    // MOSTRAR RANKING
    // ==================================================

  function mostrarRankingCapitulos(lista) {

    const tabla =
        obtenerTablaRankingCapitulos();

    if (!tabla) {

        console.error(
            "No existe #tablaRankingCapitulos"
        );

        return;
    }

    console.log(
        "Mostrando ranking en tabla:",
        lista.length,
        "registros"
    );

    tabla.innerHTML = "";

    if (
        !Array.isArray(lista) ||
        lista.length === 0
    ) {

        tabla.innerHTML = `
            <tr>
                <td
                    colspan="7"
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
                `${item.apellidos ?? ""} ${item.nombres ?? ""}`
                    .trim();

            const carrera =
                item.carrera ||
                "Sin carrera";

            // ==========================================
            // FACULTAD
            // ==========================================

            let facultad =
                item.facultad || "";

            if (
                !facultad &&
                typeof obtenerFacultad === "function"
            ) {

                facultad =
                    obtenerFacultad(carrera);
            }

            if (!facultad) {

                facultad =
                    "Sin facultad";
            }

            // ==========================================
            // PUNTAJE
            // ==========================================

            const puntaje =
                Number(
                    item.puntajeCapitulos ??
                    item.puntaje ??
                    0
                );

            // ==========================================
            // PUESTO
            // ==========================================

            const puesto =
                item.posicion ??
                item.puesto ??
                item.posicionRanking ??
                (indice + 1);

            // ==========================================
            // ID DOCENTE
            // ==========================================

            const idDocente =
                item.idDocente;

            // ==========================================
            // CREAR FILA
            // ==========================================

            const fila =
                document.createElement("tr");

            // ==========================================
            // ESCAPAR TEXTO PARA HTML
            // ==========================================

            const escapar =
                (texto) => {

                    return String(texto ?? "")
                        .replace(/&/g, "&amp;")
                        .replace(/</g, "&lt;")
                        .replace(/>/g, "&gt;")
                        .replace(/"/g, "&quot;")
                        .replace(/'/g, "&#039;");

                };

            fila.innerHTML = `

                <td>
                    ${puesto}
                </td>

                <td>
                    ${escapar(item.cedula)}
                </td>

                <td>
                    <strong>
                        ${escapar(nombreCompleto)}
                    </strong>
                </td>

                <td>
                    ${escapar(facultad)}
                </td>

                <td>
                    ${escapar(carrera)}
                </td>

                <td>
                    <strong>
                        ${puntaje.toFixed(2)}
                    </strong>
                </td>

                <td>

                    <button
                        type="button"
                        class="btn btn-primary btn-editar-docente"
                        data-id-docente="${idDocente}"
                        data-facultad="${escapar(facultad)}"
                        data-carrera="${escapar(carrera)}"
                    >

                        <i class="fa-solid fa-pen"></i>

                        Editar

                    </button>

                </td>

            `;

            // ==========================================
            // EVENTO EDITAR
            // ==========================================

            const botonEditar =
                fila.querySelector(
                    ".btn-editar-docente"
                );

            if (botonEditar) {

                botonEditar.addEventListener(
                    "click",
                    function () {

                        editarDatosAcademicos(
                            idDocente,
                            facultad,
                            carrera
                        );

                    }
                );

            }

            tabla.appendChild(fila);

        }
    );

    }
    

    
    
function cargarFacultadesModal() {

    const select =
        document.getElementById(
            "editarFacultad"
        );

    if (!select) {

        console.error(
            "No existe #editarFacultad"
        );

        return;
    }

    // ==========================================
    // OBTENER FACULTADES DESDE MAPA_FACULTADES
    // ==========================================

    const facultades =
        [
            ...new Set(
                Object.values(
                    MAPA_FACULTADES
                )
            )
        ]
        .sort();

    // ==========================================
    // LIMPIAR SELECT
    // ==========================================

    select.innerHTML = `
        <option value="">
            Seleccione una facultad
        </option>
    `;

    // ==========================================
    // AGREGAR FACULTADES
    // ==========================================

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
    
function editarDatosAcademicos(
    idDocente,
    facultadActual,
    carreraActual
) {

    const id =
        document.getElementById(
            "editarIdDocente"
        );

    const facultad =
        document.getElementById(
            "editarFacultad"
        );

    const carrera =
        document.getElementById(
            "editarCarrera"
        );

    if (!id || !facultad || !carrera) {

        console.error(
            "No se encontró el formulario de edición."
        );

        return;
    }

    // ==========================================
    // CARGAR FACULTADES
    // ==========================================

    cargarFacultadesModal();

    // ==========================================
    // CARGAR DATOS ACTUALES
    // ==========================================

    id.value =
        idDocente;

    facultad.value =
        facultadActual &&
        facultadActual !== "Sin facultad"
            ? facultadActual
            : "";

    carrera.value =
        carreraActual &&
        carreraActual !== "Sin carrera"
            ? carreraActual
            : "";

    // ==========================================
    // ABRIR MODAL PERSONALIZADO
    // ==========================================

    if (
        typeof Modal !== "undefined" &&
        typeof Modal.abrir === "function"
    ) {

        Modal.abrir(
            "modalEditarDatosAcademicos"
        );

    } else {

        console.error(
            "El objeto Modal no está disponible."
        );

    }

}
    

async function guardarDatosAcademicos() {

    try {

        const idDocente =
            document.getElementById(
                "editarIdDocente"
            )?.value;

        const facultad =
            document.getElementById(
                "editarFacultad"
            )?.value;

        const carrera =
            document.getElementById(
                "editarCarrera"
            )?.value
            .trim();

        // ==========================================
        // VALIDAR DOCENTE
        // ==========================================

        if (!idDocente) {

            mostrarResultadoCapitulos(
                "No se encontró el docente.",
                false
            );

            return;
        }

        // ==========================================
        // VALIDAR FACULTAD
        // ==========================================

        if (!facultad) {

            mostrarResultadoCapitulos(
                "Debe seleccionar una facultad.",
                false
            );

            return;
        }

        // ==========================================
        // VALIDAR CARRERA
        // ==========================================

        if (!carrera) {

            mostrarResultadoCapitulos(
                "Debe ingresar una carrera.",
                false
            );

            return;
        }

        // ==========================================
        // ACTUALIZAR BACKEND
        // ==========================================

        const response =
            await fetch(
                `/api/docentes/${idDocente}/datos-academicos`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        facultad:
                            facultad,

                        carrera:
                            carrera

                    })
                }
            );

        if (!response.ok) {

            const error =
                await response.text();

            throw new Error(
                error ||
                "No se pudieron actualizar los datos."
            );
        }

        // ==========================================
        // CERRAR MODAL
        // ==========================================

        Modal.cerrar(
            "modalEditarDatosAcademicos"
        );

        // ==========================================
        // ACTUALIZAR RANKING
        // ==========================================

        await cargarRankingCapitulos(false);

        // ==========================================
        // MENSAJE
        // ==========================================

        mostrarResultadoCapitulos(
            "Datos académicos actualizados correctamente.",
            true
        );

    } catch (error) {

        console.error(
            "Error actualizando datos académicos:",
            error
        );

        mostrarResultadoCapitulos(
            "Error al actualizar datos académicos: " +
            error.message,
            false
        );

    }

}

    // ==================================================
    // CONFIGURAR FILTROS
    // ==================================================

   function configurarFiltrosCapitulos() {

    const input =
        document.getElementById(
            "filtroCapituloDocente"
        );


    const select =
        document.getElementById(
            "filtroCapituloFacultad"
        );


    if (input) {

        input.addEventListener(
            "input",
            filtrarRankingCapitulos
        );

    }


    if (select) {

        select.addEventListener(
            "change",
            filtrarRankingCapitulos
        );

    }

}


    // ==================================================
    // CONFIGURAR FORMULARIO
    // ==================================================

    function configurarFormularioCapitulos() {

        const formulario =
            document.getElementById(
                "formImportacionCapitulos"
            );


        if (!formulario) {

            console.warn(
                "No existe #formImportacionCapitulos"
            );

            return;

        }


        formulario.addEventListener(
            "submit",
            async function (event) {

                // --------------------------------------
                // EVITAR RECARGA DE INDEX.HTML
                // --------------------------------------

                event.preventDefault();


                console.log(
                    "Submit de importación de capítulos capturado."
                );


                await importarCapitulos();

            }
        );

    }


    // ==================================================
    // RETROALIMENTACIÓN
    // ==================================================

   function mostrarResultadoCapitulos(
    mensaje,
    exito
) {

    const resultado =
        obtenerResultadoCapitulos();


    if (!resultado) {
        return;
    }


    resultado.classList.remove(
        "oculto",
        "exito",
        "error"
    );


    // ==========================================
    // RETROALIMENTACIÓN DE IMPORTACIÓN
    // ==========================================

    if (
        exito &&
        mensaje.includes(
            "Importación de capítulos de libro finalizada"
        )
    ) {

        const extraerNumero =
            (texto) => {

                const regex =
                    new RegExp(
                        texto +
                        "\\s*:\\s*(\\d+)",
                        "i"
                    );

                const coincidencia =
                    mensaje.match(regex);

                return coincidencia
                    ? Number(coincidencia[1])
                    : 0;
            };


        const capitulosInsertados =
            extraerNumero(
                "Capítulos insertados"
            );


        const capitulosActualizados =
            extraerNumero(
                "capítulos actualizados"
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


        resultado.classList.add(
            "exito"
        );


        resultado.innerHTML = `

            <div class="retroalimentacion-importacion">

                <div class="retro-titulo">

                    <i class="fa-solid fa-circle-check"></i>

                    Importación de capítulos de libro finalizada

                </div>


                <div class="retro-grid">


                    <div class="retro-item">

                        <span>
                            Capítulos insertados
                        </span>

                        <strong>
                            ${capitulosInsertados}
                        </strong>

                    </div>


                    <div class="retro-item">

                        <span>
                            Capítulos actualizados
                        </span>

                        <strong>
                            ${capitulosActualizados}
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


    // ==========================================
    // MENSAJE NORMAL
    // ==========================================

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

   window.importarCapitulos =
    importarCapitulos;

window.calcularPuntajesCapitulos =
    calcularPuntajesCapitulos;

window.cargarRankingCapitulos =
    cargarRankingCapitulos;

window.filtrarRankingCapitulos =
    filtrarRankingCapitulos;

window.editarDatosAcademicos =
    editarDatosAcademicos;

window.guardarDatosAcademicos =
    guardarDatosAcademicos;


    // ==================================================
    // INICIALIZACIÓN
    // ==================================================

    (async function inicializarCapitulos() {

        try {

            console.log(
                "Inicializando módulo Capítulos de Libro..."
            );


            // ------------------------------------------
            // CONFIGURAR UNA SOLA VEZ
            // ------------------------------------------

            configurarFiltrosCapitulos();

            configurarFormularioCapitulos();


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

                await cargarRankingCapitulos();

            } else {

                mostrarRankingCapitulos([]);

            }

        } catch (error) {

            console.error(
                "Error inicializando módulo Capítulos:",
                error
            );


            mostrarResultadoCapitulos(
                "Error inicializando Capítulos: " +
                error.message,
                false
            );

        }

    })();

})();