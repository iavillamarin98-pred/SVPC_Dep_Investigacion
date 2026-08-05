const Loader = (() => {

    let overlay = null;

    function crear() {

        if (overlay) {
            return;
        }

        overlay = document.createElement("div");

        overlay.id = "loaderGlobal";

        overlay.innerHTML = `
            <div class="loader-box">

                <div class="spinner"></div>

                <div id="loaderTexto">
                    Procesando...
                </div>

            </div>
        `;

        document.body.appendChild(overlay);

    }

    function mostrar(texto = "Procesando...") {

        crear();

        document.getElementById("loaderTexto").textContent = texto;

        overlay.classList.remove("oculto");

    }

    function ocultar() {

        if (overlay) {

            overlay.classList.add("oculto");

        }

    }

    return {

        mostrar,

        ocultar

    };

})();