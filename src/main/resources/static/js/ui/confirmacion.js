const Confirmacion = (() => {

    let callbackAceptar = null;

    function crear() {

        if (document.getElementById("modalConfirmacion")) {

            return;

        }

        const modal = document.createElement("div");

        modal.id = "modalConfirmacion";

        modal.className = "modal-overlay oculto";

         modal.innerHTML = `

            <div class="modal-content modal-confirmacion">

                <div class="modal-header">

                    <h3 id="confirmTitulo">
                        Confirmación
                    </h3>

                    <button
                        type="button"
                        class="modal-cerrar"
                        id="btnConfirmarCerrar"
                        aria-label="Cerrar">

                        &times;

                    </button>

                </div>


                <div class="modal-body">

                    <p id="confirmMensaje"></p>

                </div>


                <div class="modal-footer">

                    <button
                        type="button"
                        id="btnConfirmarAceptar"
                        class="btn btn-primary">

                        <i class="fa-solid fa-check"></i>

                        Aceptar

                    </button>


                    <button
                        type="button"
                        id="btnConfirmarCancelar"
                        class="btn btn-warning">

                        <i class="fa-solid fa-xmark"></i>

                        Cancelar

                    </button>

                </div>

            </div>

        `;

        document.body.appendChild(modal);

        
        // ==============================================
        // ACEPTAR
        // ==============================================

        document
            .getElementById(
                "btnConfirmarAceptar"
            )
            .onclick = () => {

                const callback =
                    callbackAceptar;

                ocultar();

                callbackAceptar =
                    null;


                if (
                    typeof callback ===
                    "function"
                ) {

                    callback();

                }

            };


        // ==============================================
        // CANCELAR
        // ==============================================

        document
            .getElementById(
                "btnConfirmarCancelar"
            )
            .onclick = () => {

                ocultar();

                callbackAceptar =
                    null;

            };


        // ==============================================
        // X
        // ==============================================

        document
            .getElementById(
                "btnConfirmarCerrar"
            )
            .onclick = () => {

                ocultar();

                callbackAceptar =
                    null;

            };


        // ==============================================
        // CLIC EN EL FONDO
        // ==============================================

        modal.onclick = (event) => {

            if (
                event.target === modal
            ) {

                ocultar();

                callbackAceptar =
                    null;

            }

        };

    }


    // ==================================================
    // MOSTRAR
    // ==================================================

    function mostrar(
        titulo,
        mensaje,
        aceptar
    ) {

        crear();


        callbackAceptar =
            aceptar;


        document.getElementById(
            "confirmTitulo"
        ).textContent =
            titulo;


        document.getElementById(
            "confirmMensaje"
        ).textContent =
            mensaje;


        document
            .getElementById(
                "modalConfirmacion"
            )
            .classList
            .remove("oculto");

    }


    // ==================================================
    // OCULTAR
    // ==================================================

    function ocultar() {

        const modal =
            document.getElementById(
                "modalConfirmacion"
            );


        if (modal) {

            modal.classList.add(
                "oculto"
            );

        }

    }


    return {

        mostrar,

        ocultar

    };

})();