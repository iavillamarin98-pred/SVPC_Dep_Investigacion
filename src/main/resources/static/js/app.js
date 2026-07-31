//==================================================
// CARGA DE MÓDULOS
//==================================================

async function cargarModulo(nombre) {

    let archivo = "";
    let script = "";

    switch (nombre) {

        case "dashboard":
            archivo = "modulos/dashboard.html";
            break;

        case "procesos":
            archivo = "modulos/procesos.html";
            script = "js/procesos.js";
            break;

        case "articulos":
            archivo = "modulos/importar-articulos.html";
            script = "js/articulos.js";
            break;

        case "libros":
            archivo = "modulos/importar-libros.html";
            script = "js/libros.js";
            break;

        case "capitulos":
            archivo = "modulos/importar-capitulos-libro.html";
            script = "js/capitulos.js";
            break;

        case "proceedings":
            archivo = "modulos/importar-proceedings.html";
            script = "js/proceedings.js";
            break;

        case "proyectos":
            archivo = "modulos/proyectos.html";
            script = "js/proyectos.js";
            break;

        case "rankingGeneral":
            archivo = "modulos/ranking-general.html";
            script = "js/ranking-general.js";
            break;

        case "rankingCarrera":
            archivo = "modulos/ranking-carrera.html";
            script = "js/ranking-carrera.js";
            break;

        case "usuarios":
            archivo = "modulos/usuarios.html";
            script = "js/usuarios.js";
            break;

        case "configuracion":
            archivo = "modulos/configuracion.html";
            script = "js/configuracion.js";
            break;

        default:
            archivo = "modulos/dashboard.html";
    }

    try {

        const respuesta = await fetch(archivo);

        document.getElementById("contenidoPrincipal").innerHTML =
            await respuesta.text();

        if (script) {

            cargarScript(script);

        }

    } catch (e) {

        document.getElementById("contenidoPrincipal").innerHTML =

            `<div class="card">

                <h2>Módulo en construcción</h2>

                <p>Este módulo estará disponible próximamente.</p>

            </div>`;

    }

}



//==================================================
// CARGA DINÁMICA DE JAVASCRIPT
//==================================================

function cargarScript(src) {

    const viejo = document.getElementById("scriptModulo");

    if (viejo) {

        viejo.remove();

    }

    const script = document.createElement("script");

    script.src = src + "?v=" + Date.now();

    script.id = "scriptModulo";

    document.body.appendChild(script);

}



//==================================================
// MENÚ ACORDEÓN
//==================================================

function inicializarMenu() {

    const menus = document.querySelectorAll(".menu");

    menus.forEach(menu => {

        const titulo = menu.querySelector(".menu-titulo");

        titulo.addEventListener("click", () => {

            menus.forEach(m => {

                if (m !== menu) {

                    m.classList.remove("activo");

                }

            });

            menu.classList.toggle("activo");

        });

    });

}



//==================================================
// OPCIÓN ACTIVA
//==================================================

function activarOpcion(opcion) {

    document.querySelectorAll(".submenu li")
        .forEach(li => li.classList.remove("activo"));

    document.querySelectorAll(".sidebar > nav > ul > li")
        .forEach(li => li.classList.remove("activo"));

    opcion.classList.add("activo");

}



//==================================================
// DASHBOARD ACTIVO
//==================================================

function activarDashboard(opcion) {

    document.querySelectorAll(".submenu li")
        .forEach(li => li.classList.remove("activo"));

    document.querySelectorAll(".sidebar > nav > ul > li")
        .forEach(li => li.classList.remove("activo"));

    opcion.classList.add("activo");

}



//==================================================
// INICIO
//==================================================

document.addEventListener("DOMContentLoaded", () => {

    inicializarMenu();

    const dashboard = document.querySelector(".sidebar > nav > ul > li");

    dashboard.classList.add("activo");

    cargarModulo("dashboard");

});