const Confirmacion = (() => {

    let callbackAceptar = null;

    function crear() {

        if (document.getElementById("modalConfirmacion")) {

            return;

        }

        const modal = document.createElement("div");

        modal.id = "modalConfirmacion";

        modal.className = "modal oculto";

        modal.innerHTML = `

            <div class="modal-content modal-confirmacion">

                <div class="modal-header">

                    <h3 id="confirmTitulo">

                        Confirmación

                    </h3>

                </div>

                <div class="modal-body">

                    <p id="confirmMensaje"></p>

                </div>

                <div class="acciones">

                    <button
                        id="btnConfirmarAceptar"
                        class="btn btn-primary">

                        Aceptar

                    </button>

                    <button
                        id="btnConfirmarCancelar"
                        class="btn btn-warning">

                        Cancelar

                    </button>

                </div>

            </div>

        `;

        document.body.appendChild(modal);

        document
            .getElementById("btnConfirmarAceptar")
            .onclick = () => {

                ocultar();

                if (callbackAceptar) {

                    callbackAceptar();

                }

            };

        document
            .getElementById("btnConfirmarCancelar")
            .onclick = ocultar;

    }

    function mostrar(titulo, mensaje, aceptar) {

        crear();

        callbackAceptar = aceptar;

        document.getElementById("confirmTitulo").textContent = titulo;

        document.getElementById("confirmMensaje").textContent = mensaje;

        document
            .getElementById("modalConfirmacion")
            .classList
            .remove("oculto");

    }

    function ocultar() {

        document
            .getElementById("modalConfirmacion")
            .classList
            .add("oculto");

    }

    return {

        mostrar

    };

})();