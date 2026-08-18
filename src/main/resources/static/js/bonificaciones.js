// ==================================================
// BONIFICACIONES POR DOCENTE
// ==================================================

var API_BONIFICACIONES = "/api/bonificaciones";

var bonificaciones = [];
var docentesBonificacion = [];
var idBonificacionEditando = null;


// ==================================================
// INICIALIZAR
// ==================================================

async function inicializarBonificaciones() {

    await cargarBonificaciones();

}


// ==================================================
// CARGAR BONIFICACIONES
// ==================================================

async function cargarBonificaciones() {

    try {

        const response =
            await fetch(API_BONIFICACIONES);

        if (!response.ok) {

            throw new Error(
                "Error al cargar las bonificaciones."
            );

        }

        const datos =
            await response.json();

        bonificaciones =
            Array.isArray(datos)
                ? datos
                : [];

        console.log(
            "Bonificaciones recibidas:",
            bonificaciones
        );

        mostrarBonificaciones(
            bonificaciones
        );

    } catch (error) {

        console.error(
            "Error cargando bonificaciones:",
            error
        );

        mostrarResultadoBonificacion(
            error.message,
            false
        );

    }

}


// ==================================================
// MOSTRAR BONIFICACIONES
// ==================================================

function mostrarBonificaciones(lista) {

    const tabla =
        document.getElementById(
            "tablaBonificaciones"
        );

    if (!tabla) return;

    tabla.innerHTML = "";

    if (
        !Array.isArray(lista) ||
        lista.length === 0
    ) {

        tabla.innerHTML = `

            <tr>

                <td
                    colspan="8"
                    style="text-align:center;"
                >

                    No existen bonificaciones registradas.

                </td>

            </tr>

        `;

        return;
    }


    lista.forEach(
        (item, indice) => {

            const docente =
                item.docente || {};

            const nombre =
                `${docente.apellidos ?? ""}
                 ${docente.nombres ?? ""}`
                    .trim();

            const criterio =
                item.criterioAsignacion || "";

            const puntaje =
                Number(
                    item.puntajeAsignado ?? 0
                );


            const fila =
                document.createElement("tr");


            fila.innerHTML = `

                <td>
                    ${indice + 1}
                </td>

                <td>
                    ${escaparBonificacion(
                        docente.cedula
                    )}
                </td>

                <td>
                    <strong>
                        ${escaparBonificacion(
                            nombre
                        )}
                    </strong>
                </td>

                <td>
                    ${escaparBonificacion(
                        docente.facultad ||
                        "Sin facultad"
                    )}
                </td>

                <td>
                    ${escaparBonificacion(
                        docente.carrera ||
                        "Sin carrera"
                    )}
                </td>

                <td>
                    ${crearBadgeCriterio(
                        criterio
                    )}
                </td>

                <td>
                    <strong
                        class="puntaje-bonificacion"
                    >
                        ${puntaje.toFixed(2)}
                    </strong>
                </td>

                <td>

                    <button
                        type="button"
                        class="btn btn-warning"
                        onclick="editarBonificacion(
                            ${item.idBonificacion}
                        )"
                    >

                        <i class="fa-solid fa-pen"></i>
                        Editar

                    </button>

                    <button
                        type="button"
                        class="btn btn-danger"
                        onclick="eliminarBonificacion(
                            ${item.idBonificacion}
                        )"
                    >

                        <i class="fa-solid fa-trash"></i>
                        Eliminar

                    </button>

                </td>

            `;

            tabla.appendChild(fila);

        }
    );

}


// ==================================================
// BADGE CRITERIO
// ==================================================

function crearBadgeCriterio(criterio) {

    let clase =
        "criterio-bonificacion";

    if (criterio === "Propiedad Industrial") {

        clase += " criterio-propiedad-industrial";

    } else if (criterio === "Derecho de Autor y Derechos Conexos") {

        clase += " criterio-derecho-autor";

    } else if (criterio === "Obtenciones Vegetales y Conocimientos Tradicionales") {

        clase += " criterio-obtenciones-vegetales";

    }

    return `

        <span class="${clase}">

            ${escaparBonificacion(
                criterio
            )}

        </span>

    `;

}


// ==================================================
// ESCAPAR HTML
// ==================================================

function escaparBonificacion(texto) {

    return String(texto ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");

}


// ==================================================
// MODAL DOCENTES
// ==================================================

async function abrirModalDocenteBonificacion() {

    const modal =
        document.getElementById(
            "modalDocenteBonificacion"
        );

    if (!modal) {

        console.error(
            "No existe #modalDocenteBonificacion"
        );

        return;
    }

    modal.classList.remove("oculto");
    document.body.classList.add("modal-abierto");
    document.addEventListener("keydown", manejarTecladoModalBonificacion);

    await cargarDocentesBonificacion();

    document.getElementById("buscarDocenteBonificacion")?.focus();

}


function cerrarModalDocenteBonificacion() {

    const modal =
        document.getElementById(
            "modalDocenteBonificacion"
        );

    if (modal) {

        modal.classList.add("oculto");
        document.body.classList.remove("modal-abierto");
        document.removeEventListener("keydown", manejarTecladoModalBonificacion);

    }

}

function manejarTecladoModalBonificacion(evento) {

    const modal = document.getElementById("modalDocenteBonificacion");

    if (!modal || modal.classList.contains("oculto")) return;

    if (evento.key === "Escape") {
        cerrarModalDocenteBonificacion();
        return;
    }

    if (evento.key !== "Tab") return;

    const elementos = modal.querySelectorAll(
        'button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'
    );

    if (!elementos.length) return;

    const primero = elementos[0];
    const ultimo = elementos[elementos.length - 1];

    if (evento.shiftKey && document.activeElement === primero) {
        evento.preventDefault();
        ultimo.focus();
    } else if (!evento.shiftKey && document.activeElement === ultimo) {
        evento.preventDefault();
        primero.focus();
    }

}


// ==================================================
// CARGAR DOCENTES
// ==================================================

async function cargarDocentesBonificacion() {

    try {

        const response =
            await fetch("/api/docentes");

        if (!response.ok) {

            throw new Error(
                "No se pudieron cargar los docentes."
            );

        }

        docentesBonificacion =
            await response.json();

        mostrarDocentesBonificacion(
            docentesBonificacion
        );

    } catch (error) {

        console.error(
            "Error cargando docentes:",
            error
        );

    }

}


// ==================================================
// MOSTRAR DOCENTES
// ==================================================

function mostrarDocentesBonificacion(lista) {

    const tabla =
        document.getElementById(
            "tablaDocentesBonificacion"
        );

    if (!tabla) return;

    tabla.innerHTML = "";

    if (
        !Array.isArray(lista) ||
        lista.length === 0
    ) {

        tabla.innerHTML = `

            <tr>

                <td
                    colspan="5"
                    style="text-align:center;"
                >

                    No existen docentes.

                </td>

            </tr>

        `;

        return;

    }


    lista.forEach(docente => {

        const nombre =
            `${docente.apellidos ?? ""}
             ${docente.nombres ?? ""}`
                .trim();


        const fila =
            document.createElement("tr");


        fila.innerHTML = `

            <td>
                ${escaparBonificacion(
                    docente.cedula
                )}
            </td>

            <td>
                <strong>
                    ${escaparBonificacion(
                        nombre
                    )}
                </strong>
            </td>

            <td>
                ${escaparBonificacion(
                    docente.facultad ||
                    "Sin facultad"
                )}
            </td>

            <td>
                ${escaparBonificacion(
                    docente.carrera ||
                    "Sin carrera"
                )}
            </td>

            <td>

                <button
                    type="button"
                    class="btn btn-success"
                    onclick="seleccionarDocenteBonificacion(
                        ${docente.idDocente}
                    )"
                >

                    <i class="fa-solid fa-check"></i>
                    Seleccionar

                </button>

            </td>

        `;

        tabla.appendChild(fila);

    });

}


// ==================================================
// BUSCAR DOCENTES
// ==================================================

function buscarDocentesBonificacion() {

    const input =
        document.getElementById(
            "buscarDocenteBonificacion"
        );

    const texto =
        input
            ? input.value
                .trim()
                .toLowerCase()
            : "";


    const filtrados =
        docentesBonificacion.filter(
            docente => {

                const cedula =
                    String(
                        docente.cedula ?? ""
                    ).toLowerCase();

                const nombres =
                    String(
                        docente.nombres ?? ""
                    ).toLowerCase();

                const apellidos =
                    String(
                        docente.apellidos ?? ""
                    ).toLowerCase();

                return (
                    cedula.includes(texto) ||
                    nombres.includes(texto) ||
                    apellidos.includes(texto) ||
                    `${apellidos} ${nombres}`
                        .includes(texto)
                );

            }
        );


    mostrarDocentesBonificacion(
        filtrados
    );

}


// ==================================================
// SELECCIONAR DOCENTE
// ==================================================

function seleccionarDocenteBonificacion(
    idDocente
) {

    const docente =
        docentesBonificacion.find(
            d =>
                Number(d.idDocente) ===
                Number(idDocente)
        );


    if (!docente) {

        Notificaciones.error(
            "Docente no encontrado."
        );

        return;

    }


    const nombre =
        `${docente.apellidos ?? ""}
         ${docente.nombres ?? ""}`
            .trim();


    document.getElementById(
        "idDocenteBonificacion"
    ).value =
        docente.idDocente;


    document.getElementById(
        "docenteBonificacion"
    ).value =
        nombre;


    document.getElementById(
        "cedulaBonificacion"
    ).value =
        docente.cedula || "";


    document.getElementById(
        "facultadBonificacion"
    ).value =
        docente.facultad ||
        "Sin facultad";


    document.getElementById(
        "carreraBonificacion"
    ).value =
        docente.carrera ||
        "Sin carrera";


    cerrarModalDocenteBonificacion();

}


// ==================================================
// GUARDAR / ACTUALIZAR
// ==================================================

async function guardarBonificacion() {

    const idDocente =
        document.getElementById(
            "idDocenteBonificacion"
        ).value;

    const criterio =
        document.getElementById(
            "criterioBonificacion"
        ).value;

    const puntaje =
        document.getElementById(
            "puntajeAsignado"
        ).value;


    if (!idDocente) {

        Notificaciones.error(
            "Seleccione un docente."
        );

        return;
    }


    if (!criterio) {

        Notificaciones.error(
            "Seleccione un criterio de asignación."
        );

        return;
    }


    const valor =
        Number(puntaje);


    if (
        puntaje === "" ||
        isNaN(valor) ||
        valor < 0 ||
        valor > 50
    ) {

        Notificaciones.error(
            "El puntaje debe estar entre 0 y 50."
        );

        return;
    }


    const datos = {

        idDocente:
            Number(idDocente),

        criterioAsignacion:
            criterio,

        puntajeAsignado:
            valor

    };


    try {

        let url =
            API_BONIFICACIONES;

        let metodo =
            "POST";


        if (
            idBonificacionEditando
        ) {

            url =
                `${API_BONIFICACIONES}/${idBonificacionEditando}`;

            metodo =
                "PUT";

        }


        const response =
            await fetch(
                url,
                {

                    method: metodo,

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(datos)

                }
            );


        const resultado =
            await response.json();


        if (!response.ok) {

            throw new Error(
                resultado.error ||
                "No se pudo guardar la bonificación."
            );

        }


        mostrarResultadoBonificacion(
            idBonificacionEditando
                ? "Bonificación actualizada correctamente."
                : "Bonificación registrada correctamente.",
            true
        );


        limpiarFormularioBonificacion();

        await cargarBonificaciones();


    } catch (error) {

        console.error(
            "Error guardando bonificación:",
            error
        );

        mostrarResultadoBonificacion(
            error.message,
            false
        );

    }

}


// ==================================================
// EDITAR
// ==================================================

function editarBonificacion(id) {

    const item =
        bonificaciones.find(
            b =>
                Number(
                    b.idBonificacion
                ) === Number(id)
        );


    if (!item) {

        Notificaciones.error(
            "Bonificación no encontrada."
        );

        return;
    }


    const docente =
        item.docente || {};


    idBonificacionEditando =
        item.idBonificacion;


    document.getElementById(
        "idDocenteBonificacion"
    ).value =
        docente.idDocente || "";


    document.getElementById(
        "docenteBonificacion"
    ).value =
        `${docente.apellidos ?? ""}
         ${docente.nombres ?? ""}`
            .trim();


    document.getElementById(
        "cedulaBonificacion"
    ).value =
        docente.cedula || "";


    document.getElementById(
        "facultadBonificacion"
    ).value =
        docente.facultad ||
        "Sin facultad";


    document.getElementById(
        "carreraBonificacion"
    ).value =
        docente.carrera ||
        "Sin carrera";


    document.getElementById(
        "criterioBonificacion"
    ).value =
        item.criterioAsignacion || "";


    document.getElementById(
        "puntajeAsignado"
    ).value =
        item.puntajeAsignado ?? "";


    const boton =
        document.querySelector(
            'button[onclick="guardarBonificacion()"]'
        );


    if (boton) {

        boton.innerHTML = `

            <i class="fa-solid fa-save"></i>
            Actualizar bonificación

        `;

    }

}


// ==================================================
// ELIMINAR
// ==================================================

function eliminarBonificacion(id) {

    Confirmacion.mostrar(
        "Eliminar bonificación",
        "¿Está seguro de eliminar esta bonificación?",
        async () => {

            try {

                Loader.mostrar(
                    "Eliminando bonificación..."
                );

                const response =
                    await fetch(
                        `${API_BONIFICACIONES}/${id}`,
                        {
                            method: "DELETE"
                        }
                    );


                let resultado = {};

                const texto =
                    await response.text();

                if (texto) {

                    try {

                        resultado =
                            JSON.parse(texto);

                    } catch (e) {

                        resultado = {
                            mensaje: texto
                        };

                    }

                }


                if (!response.ok) {

                    throw new Error(
                        resultado.error ||
                        resultado.mensaje ||
                        "No se pudo eliminar la bonificación."
                    );

                }


                Notificaciones.exito(
                    "Bonificación eliminada correctamente."
                );


                await cargarBonificaciones();


            } catch (error) {

                console.error(
                    "Error eliminando bonificación:",
                    error
                );

                Notificaciones.error(
                    error.message ||
                    "No se pudo eliminar la bonificación."
                );

            } finally {

                Loader.ocultar();

            }

        }
    );

}


// ==================================================
// FILTRAR BONIFICACIONES
// ==================================================

function filtrarBonificaciones() {

    const texto =
        document.getElementById(
            "buscarBonificacion"
        )?.value
            .trim()
            .toLowerCase() || "";


    const criterio =
        document.getElementById(
            "filtroCriterioBonificacion"
        )?.value || "";


    const filtradas =
        bonificaciones.filter(
            item => {

                const docente =
                    item.docente || {};


                const nombre =
                    `${docente.apellidos ?? ""}
                     ${docente.nombres ?? ""}`
                        .toLowerCase();


                const cedula =
                    String(
                        docente.cedula ?? ""
                    ).toLowerCase();


                const coincideTexto =
                    !texto ||
                    nombre.includes(texto) ||
                    cedula.includes(texto);


                const coincideCriterio =
                    !criterio ||
                    item.criterioAsignacion ===
                    criterio;


                return (
                    coincideTexto &&
                    coincideCriterio
                );

            }
        );


    mostrarBonificaciones(
        filtradas
    );

}


// ==================================================
// VALIDAR PUNTAJE
// ==================================================

function validarPuntajeBonificacion(
    input
) {

    let valor =
        Number(input.value);


    if (valor < 0) {

        input.value = 0;

    }


    if (valor > 50) {

        input.value = 50;

    }

}


// ==================================================
// LIMPIAR FORMULARIO
// ==================================================

function limpiarFormularioBonificacion() {

    idBonificacionEditando =
        null;


    document.getElementById(
        "idDocenteBonificacion"
    ).value = "";


    document.getElementById(
        "docenteBonificacion"
    ).value = "";


    document.getElementById(
        "cedulaBonificacion"
    ).value = "";


    document.getElementById(
        "facultadBonificacion"
    ).value = "";


    document.getElementById(
        "carreraBonificacion"
    ).value = "";


    document.getElementById(
        "criterioBonificacion"
    ).value = "";


    document.getElementById(
        "puntajeAsignado"
    ).value = "";


    const boton =
        document.querySelector(
            'button[onclick="guardarBonificacion()"]'
        );


    if (boton) {

        boton.innerHTML = `

            <i class="fa-solid fa-save"></i>
            Guardar bonificación

        `;

    }

}


// ==================================================
// MENSAJE
// ==================================================

function mostrarResultadoBonificacion(
    mensaje,
    exito
) {

    const elemento =
        document.getElementById(
            "resultadoBonificacion"
        );

    if (!elemento) return;


    elemento.textContent =
        mensaje;


    elemento.classList.remove(
        "oculto"
    );


    elemento.classList.remove(
        "exito",
        "error"
    );


    elemento.classList.add(
        exito
            ? "exito"
            : "error"
    );

}


// ==================================================
// INICIALIZACIÓN
// ==================================================

