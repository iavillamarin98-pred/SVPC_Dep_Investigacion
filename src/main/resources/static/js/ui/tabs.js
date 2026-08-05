const Tabs = (() => {

    function activar(idPestana) {

        document
            .querySelectorAll(".tab-btn")
            .forEach(btn => btn.classList.remove("activo"));

        document
            .querySelectorAll(".tab-panel")
            .forEach(panel => panel.classList.remove("activo"));

        document
            .querySelector(`[data-tab="${idPestana}"]`)
            ?.classList.add("activo");

        document
            .getElementById(idPestana)
            ?.classList.add("activo");

    }

    return {
        activar
    };

})();