const Modal = (() => {

    function abrir(id) {

        const modal = document.getElementById(id);

        if (!modal) return;

        modal.classList.remove("oculto");

    }

    function cerrar(id) {

        const modal = document.getElementById(id);

        if (!modal) return;

        modal.classList.add("oculto");

    }

    function toggle(id) {

        const modal = document.getElementById(id);

        if (!modal) return;

        modal.classList.toggle("oculto");

    }

    return {

        abrir,
        cerrar,
        toggle

    };

})();