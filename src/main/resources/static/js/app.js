//==================================================
// CARGA DE MÓDULOS
//==================================================

async function cargarModulo(nombre) {

    let archivo = "";
    let scripts = [];

    switch (nombre) {

        case "dashboard":
    archivo = "modulos/dashboard.html";
    scripts = ["js/dashboard.js"];
    break;

        case "procesos":
            archivo = "modulos/procesos.html";
            scripts = ["js/procesos.js"];
            break;

        case "articulos":
            archivo = "modulos/importar-articulos.html";
            scripts = ["js/articulos.js"];
            break;

        case "libros":
            archivo = "modulos/importar-libros.html";
            scripts = ["js/libros.js"];
            break;

        case "capitulos":
            archivo = "modulos/importar-capitulos-libro.html";
            scripts = [
                "js/capitulos.js"
            ];
            break;

        case "proceedings":
            archivo = "modulos/importar-proceedings.html";
            scripts = ["js/proceedings.js"];
            break;

        case "proyectos":
            archivo = "modulos/proyectos.html";
            scripts = [
                "js/buscar-docente.js",
                "js/proyectos.js"
            ];
            break;
             case "bonificaciones":
            archivo = "modulos/bonificaciones.html";
            scripts = ["js/bonificaciones.js"];
            break;
                

        case "docentes":
            archivo = "modulos/docentes.html";
            scripts = ["js/docentes.js"];
            break;

        case "rankingGeneral":
            archivo = "modulos/ranking-general.html";
            scripts = ["js/ranking-general.js"];
            break;

        case "rankingCarrera":
            archivo = "modulos/ranking-carrera.html";
            scripts = ["js/ranking-carrera.js"];
            break;

        case "usuarios":
            archivo = "modulos/usuarios.html";
            scripts = ["js/usuarios.js"];
            break;

        case "configuracion":

    archivo = "modulos/configuracion-puntajes.html";

    scripts = [
        "js/configuracion-puntajes.js"
    ];

    break;

        default:
            archivo = "modulos/dashboard.html";
    }

    try {

        const respuesta = await fetch(archivo);

        document.getElementById("contenidoPrincipal").innerHTML =
            await respuesta.text();

        eliminarScriptsModulo();

        for (const src of scripts) {
            await cargarScript(src);
        }

        // =========================================
        // INICIALIZACIÓN DEL MÓDULO
        // =========================================

        if (
            nombre === "dashboard" &&
            typeof window.inicializarDashboard === "function"
        ) {

            window.inicializarDashboard();

        }

        if (
            nombre === "docentes" &&
            typeof window.inicializarDocentes === "function"
        ) {

            window.inicializarDocentes();

        }

        if (
        nombre === "configuracion" &&
        typeof window.inicializarConfiguracionPuntajes === "function"
        ) {

            window.inicializarConfiguracionPuntajes();

        }

        if (
            nombre === "bonificaciones" &&
            typeof window.inicializarBonificaciones === "function"
        ) {

            await window.inicializarBonificaciones();

        }

    } catch (e) {

        console.error(e);

    }
}


//==================================================
// CARGA DINÁMICA DE JAVASCRIPT
//==================================================

function eliminarScriptsModulo() {

    document.querySelectorAll(".scriptModulo")
        .forEach(s => s.remove());

}

function cargarScript(src) {

    return new Promise((resolve, reject) => {

        const script = document.createElement("script");

        script.src = src + "?v=" + Date.now();

        script.className = "scriptModulo";

        script.onload = resolve;

        script.onerror = reject;

        document.body.appendChild(script);

    });

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

setTimeout(() => {

    const primera = document.querySelector(".tab-btn");

    if(primera){

        Tabs.abrir(primera.dataset.tab);

    }

},100);
