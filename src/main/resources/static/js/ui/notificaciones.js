const Notificaciones = (() => {

    let contenedor = null;

    function obtenerContenedor() {

        if (!contenedor) {

            contenedor = document.createElement("div");
            contenedor.id = "contenedorNotificaciones";

            document.body.appendChild(contenedor);
        }

        return contenedor;
    }

    function mostrar(tipo, mensaje, duracion = 3500) {

        const div = document.createElement("div");

        div.className = `toast toast-${tipo}`;

        let icono = "";

        switch (tipo) {

            case "success":
                icono = "✔";
                break;

            case "error":
                icono = "✖";
                break;

            case "warning":
                icono = "⚠";
                break;

            default:
                icono = "ℹ";
        }

        div.innerHTML = `
            <span class="toast-icono">${icono}</span>
            <span class="toast-mensaje">${mensaje}</span>
        `;

        obtenerContenedor().appendChild(div);

        setTimeout(() => {

            div.classList.add("ocultar");

            setTimeout(() => div.remove(), 300);

        }, duracion);

    }

    return {

        exito: (mensaje) => mostrar("success", mensaje),

        error: (mensaje) => mostrar("error", mensaje),

        advertencia: (mensaje) => mostrar("warning", mensaje),

        info: (mensaje) => mostrar("info", mensaje)

    };

})();