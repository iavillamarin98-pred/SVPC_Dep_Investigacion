let procesoSeleccionado = null;


/* ==================================================
   INICIALIZACIÓN
================================================== */

function inicializarConfiguracionPuntajes() {

    const selectProceso =
        document.getElementById("selectProceso");

    if (!selectProceso) {
        console.error(
            "No se encontró #selectProceso"
        );
        return;
    }

    // Evitar registrar eventos dos veces
    if (selectProceso.dataset.inicializado === "true") {
        return;
    }

    selectProceso.dataset.inicializado = "true";


    // Cargar procesos
    cargarProcesos();


    // Cambio de proceso
    selectProceso.addEventListener("change", () => {

        procesoSeleccionado = selectProceso.value;

        if (!procesoSeleccionado) {

            limpiarTablas();

            return;

        }

        cargarConfiguracion();

    });


    // Inicializar pestañas
    inicializarPestanasConfiguracion();

}

/* ==================================================
   PESTAÑAS
================================================== */

function inicializarPestanasConfiguracion() {

    const botones =
        document.querySelectorAll(
            ".config-tab-btn"
        );

    const contenidos =
        document.querySelectorAll(
            ".config-tab-content"
        );


    botones.forEach(boton => {

        if (boton.dataset.inicializado === "true") {
            return;
        }

        boton.dataset.inicializado = "true";


        boton.addEventListener("click", () => {

            const tabId =
                boton.dataset.tab;


            // Quitar activo de botones
            botones.forEach(btn => {

                btn.classList.remove("activo");

            });


            // Ocultar contenidos
            contenidos.forEach(tab => {

                tab.classList.remove("activo");

            });


            // Activar botón
            boton.classList.add("activo");


            // Activar contenido
            const contenido =
                document.getElementById(tabId);

            if (contenido) {

                contenido.classList.add("activo");

            }

        });

    });

}



/* ==================================================
   CARGAR PROCESOS
   ================================================== */

async function cargarProcesos() {

    try {

        const response =
            await fetch("/api/procesos");

        if (!response.ok) {
            throw new Error(
                "No se pudieron cargar los procesos");
        }

        const procesos = await response.json();

        const select =
            document.getElementById("selectProceso");

        procesos.forEach(proceso => {

            const option =
                document.createElement("option");

            option.value = proceso.idProceso;

            option.textContent =
                proceso.nombre ||
                proceso.descripcion ||
                `Proceso ${proceso.idProceso}`;

            select.appendChild(option);

        });

    } catch (error) {

        console.error(error);

        mostrarMensaje(
            "mensajePuntajes",
            "Error al cargar los procesos.",
            "danger"
        );
    }
}


/* ==================================================
   CARGAR CONFIGURACIÓN
   ================================================== */

async function cargarConfiguracion() {

    await Promise.all([
        cargarPuntajes(),
        cargarReglas()
    ]);

}


/* ==================================================
   PUNTAJES
   ================================================== */

async function cargarPuntajes() {

    const tbody =
        document.getElementById("tablaPuntajes");

    tbody.innerHTML = `
        <tr>
            <td colspan="6"
                class="text-center">
                Cargando...
            </td>
        </tr>
    `;

    try {

        const response =
            await fetch(
                `/api/configuracion-puntajes/puntajes/${procesoSeleccionado}`
            );

        if (!response.ok) {
            throw new Error(
                "Error al consultar puntajes");
        }

        const configuraciones =
            await response.json();

        tbody.innerHTML = "";

        if (configuraciones.length === 0) {

            tbody.innerHTML = `
                <tr>
                    <td colspan="6"
                        class="text-center text-muted">
                        No existen configuraciones.
                    </td>
                </tr>
            `;

            return;
        }

        configuraciones.forEach(config => {

            const fila =
                document.createElement("tr");

            fila.innerHTML = `

                <td>
                    ${escapeHtml(config.categoria)}
                </td>

                <td>
                    ${escapeHtml(config.criterio)}
                </td>

                <td>
                    ${escapeHtml(config.rol)}
                </td>

                <td style="width: 160px">

                    <input
                        type="number"
                        class="form-control"
                        step="0.01"
                        min="0"
                        value="${config.puntajeBase}"
                        id="puntaje-${config.idConfiguracion}">
                </td>

                <td>

                    ${
                        config.estado
                            ? `<span class="badge bg-success">
                                Activo
                               </span>`
                            : `<span class="badge bg-secondary">
                                Inactivo
                               </span>`
                    }

                </td>

                <td>

                    <button
                        class="btn btn-primary btn-sm"
                        onclick="guardarPuntaje(
                            ${config.idConfiguracion}
                        )">

                        Guardar

                    </button>

                </td>
            `;

            tbody.appendChild(fila);

        });

    } catch (error) {

        console.error(error);

        tbody.innerHTML = `
            <tr>
                <td colspan="6"
                    class="text-center text-danger">
                    Error al cargar las configuraciones.
                </td>
            </tr>
        `;
    }
}


/* ==================================================
   GUARDAR PUNTAJE
   ================================================== */

async function guardarPuntaje(idConfiguracion) {

    const input =
        document.getElementById(
            `puntaje-${idConfiguracion}`
        );

    const puntaje =
        parseFloat(input.value);

    if (isNaN(puntaje) || puntaje < 0) {

        Notificaciones.advertencia(
            "Ingrese un puntaje válido."
        );

        return;
    }

    try {

        const response =
            await fetch(
                `/api/configuracion-puntajes/puntajes/${idConfiguracion}?puntaje=${puntaje}`,
                {
                    method: "PUT"
                }
            );

        if (!response.ok) {

            throw new Error(
                "No se pudo actualizar el puntaje"
            );
        }

        Notificaciones.exito(
            "Puntaje actualizado correctamente."
        );

    } catch (error) {

        console.error(error);

        Notificaciones.error(
            "Error al actualizar el puntaje."
        );
    }
}


/* ==================================================
   REGLAS
   ================================================== */

async function cargarReglas() {

    const tbody =
        document.getElementById("tablaReglas");

    tbody.innerHTML = `
        <tr>
            <td colspan="5"
                class="text-center">
                Cargando...
            </td>
        </tr>
    `;

    try {

        const response =
            await fetch(
                `/api/configuracion-puntajes/reglas/${procesoSeleccionado}`
            );

        if (!response.ok) {
            throw new Error(
                "Error al consultar reglas");
        }

        const reglas =
            await response.json();

        tbody.innerHTML = "";

        if (reglas.length === 0) {

            tbody.innerHTML = `
                <tr>
                    <td colspan="5"
                        class="text-center text-muted">
                        No existen reglas configuradas.
                    </td>
                </tr>
            `;

            return;
        }

        reglas.forEach(regla => {

            const fila =
                document.createElement("tr");

            fila.innerHTML = `

                <td>
                    <strong>
                        Escenario ${regla.escenario}
                    </strong>
                </td>

                <td style="width: 160px">

                    <input
                        type="number"
                        class="form-control"
                        step="0.01"
                        min="0"
                        max="100"
                        value="${regla.porcentajeAutor}"
                        id="autor-${regla.idRegla}">

                </td>

                <td style="width: 160px">

                    <input
                        type="number"
                        class="form-control"
                        step="0.01"
                        min="0"
                        max="100"
                        value="${regla.porcentajeCoautor}"
                        id="coautor-${regla.idRegla}">

                </td>

                <td>

                    ${
                        regla.estado
                            ? `<span class="badge bg-success">
                                Activo
                               </span>`
                            : `<span class="badge bg-secondary">
                                Inactivo
                               </span>`
                    }

                </td>

                <td>

                    <button
                        class="btn btn-primary btn-sm"
                        onclick="guardarRegla(
                            ${regla.idRegla}
                        )">

                        Guardar

                    </button>

                </td>
            `;

            tbody.appendChild(fila);

        });

    } catch (error) {

        console.error(error);

        tbody.innerHTML = `
            <tr>
                <td colspan="5"
                    class="text-center text-danger">
                    Error al cargar las reglas.
                </td>
            </tr>
        `;
    }
}


/* ==================================================
   GUARDAR REGLA
   ================================================== */

async function guardarRegla(idRegla) {

    const autor =
        parseFloat(
            document.getElementById(
                `autor-${idRegla}`
            ).value
        );

    const coautor =
        parseFloat(
            document.getElementById(
                `coautor-${idRegla}`
            ).value
        );

    if (
        isNaN(autor) ||
        isNaN(coautor) ||
        autor < 0 ||
        coautor < 0 ||
        autor > 100 ||
        coautor > 100
    ) {

        Notificaciones.advertencia(
            "Los porcentajes deben estar entre 0 y 100."
        );

        return;
    }

    try {

        const response =
            await fetch(
                `/api/configuracion-puntajes/reglas/${idRegla}` +
                `?porcentajeAutor=${autor}` +
                `&porcentajeCoautor=${coautor}`,
                {
                    method: "PUT"
                }
            );

        if (!response.ok) {

            throw new Error(
                "No se pudo actualizar la regla"
            );
        }

        Notificaciones.exito(
            "Regla actualizada correctamente."
        );

    } catch (error) {

        console.error(error);

        Notificaciones.error(
            "Error al actualizar la regla."
        );
    }
}


/* ==================================================
   LIMPIAR TABLAS
   ================================================== */

function limpiarTablas() {

    document.getElementById("tablaPuntajes").innerHTML = `
        <tr>
            <td colspan="6"
                class="text-center text-muted">
                Seleccione un proceso
            </td>
        </tr>
    `;

    document.getElementById("tablaReglas").innerHTML = `
        <tr>
            <td colspan="5"
                class="text-center text-muted">
                Seleccione un proceso
            </td>
        </tr>
    `;
}


/* ==================================================
   MENSAJES
   ================================================== */

function mostrarMensaje(id, mensaje, tipo) {

    const contenedor =
        document.getElementById(id);

    contenedor.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show">
            ${mensaje}

            <button
                type="button"
                class="btn-close"
                data-bs-dismiss="alert">
            </button>
        </div>
    `;

    setTimeout(() => {

        contenedor.innerHTML = "";

    }, 3000);
}


/* ==================================================
   SEGURIDAD HTML
   ================================================== */

function escapeHtml(valor) {

    if (valor === null || valor === undefined) {
        return "";
    }

    return String(valor)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}