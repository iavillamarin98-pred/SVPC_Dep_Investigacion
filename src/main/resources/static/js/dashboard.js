/* ==================================================
   DASHBOARD SVPC
================================================== */

(function () {

    let graficoFacultades = null;


    /* ==================================================
       INICIALIZACIÓN
    ================================================== */

    function inicializarDashboard() {

        console.log("Inicializando Dashboard SVPC...");

        actualizarFecha();

        cargarDashboard();

    }


    /* ==================================================
       CARGAR DASHBOARD
    ================================================== */

    async function cargarDashboard() {

        try {

            // ==========================================
            // PROCESO ACTIVO
            // ==========================================

            const procesoActivo =
                await fetch("/api/procesos/activo");

            if (!procesoActivo.ok) {
                throw new Error(
                    "No se pudo obtener el proceso activo."
                );
            }

            const proceso =
                await procesoActivo.json();

            if (!proceso || !proceso.idProceso) {
                throw new Error(
                    "No existe un proceso de valoración activo."
                );
            }

            const idProceso =
                proceso.idProceso;

            console.log(
                "Dashboard - proceso activo:",
                proceso
            );


            // ==========================================
            // RESUMEN
            // ==========================================

            const response =
                await fetch(
                    `/api/dashboard/resumen?idProceso=${idProceso}`
                );

            if (!response.ok) {
                throw new Error(
                    "No se pudo cargar el resumen del dashboard."
                );
            }

            const datos =
                await response.json();

            console.log(
                "Dashboard:",
                datos
            );


            // ==========================================
            // TOTALES
            // ==========================================

            document.getElementById("totalArticulos").textContent =
                datos.articulos ?? 0;

            document.getElementById("totalProceedings").textContent =
                datos.proceedings ?? 0;

            document.getElementById("totalLibros").textContent =
                datos.libros ?? 0;

            document.getElementById("totalCapitulos").textContent =
                datos.capitulos ?? 0;

            document.getElementById("totalProyectos").textContent =
                datos.proyectos ?? 0;

            document.getElementById("totalBonificacion").textContent =
                Number(
                    datos.bonificaciones ?? 0
                ).toFixed(2);


            // ==========================================
            // RANKING GENERAL
            // ==========================================

            const rankingResponse =
                await fetch(
                    `/api/rankings/general?idProceso=${idProceso}`
                );

            if (!rankingResponse.ok) {
                throw new Error(
                    "No se pudo cargar el ranking."
                );
            }

            const ranking =
                await rankingResponse.json();

            console.log(
                "Ranking para estadísticas:",
                ranking
            );


            // ==========================================
            // MOSTRAR RANKINGS
            // ==========================================

            mostrarRankingGeneralDashboard(
                ranking
            );

            mostrarRankingFacultadesDashboard(
                ranking
            );


            // ==========================================
            // GRÁFICA POR FACULTAD
            // ==========================================

            construirGraficaFacultades(
                ranking
            );


        } catch (error) {

            console.error(
                "Error cargando Dashboard:",
                error
            );

            mostrarErrorRankingDashboard(
                error.message
            );

        }

    }


    /* ==================================================
       RANKING GENERAL
    ================================================== */

    function mostrarRankingGeneralDashboard(ranking) {

        const contenedor =
            document.getElementById(
                "rankingGeneral"
            );

        if (!contenedor) return;

        if (!Array.isArray(ranking) || ranking.length === 0) {

            contenedor.innerHTML = `
                <div class="dashboard-loading">
                    No existen datos de ranking.
                </div>
            `;

            return;

        }

        const top =
            ranking
                .slice()
                .sort(
                    (a, b) =>
                        Number(b.total ?? 0) -
                        Number(a.total ?? 0)
                )
                .slice(0, 5);


        contenedor.innerHTML = top.map(
            (item, index) => {

                const nombre =
                    `${item.apellidos ?? ""} ${item.nombres ?? ""}`.trim();

                const puesto =
                    Number(item.puesto ?? index + 1);

                const total =
                    Number(item.total ?? 0).toFixed(2);

                let medalla = "";

                if (puesto === 1) medalla = "🥇";
                else if (puesto === 2) medalla = "🥈";
                else if (puesto === 3) medalla = "🥉";

                return `
                    <div class="dashboard-ranking-item">

                        <div class="dashboard-ranking-position">
                            ${medalla || puesto}
                        </div>

                        <div class="dashboard-ranking-info">

                            <strong>
                                ${escapeHtml(nombre)}
                            </strong>

                            <small>
                                ${escapeHtml(
                                    item.facultad || "Sin facultad"
                                )}
                            </small>

                        </div>

                        <div class="dashboard-ranking-score">
                            ${total}
                        </div>

                    </div>
                `;

            }
        ).join("");

    }


    /* ==================================================
       RANKING POR FACULTAD
    ================================================== */

   function mostrarRankingFacultadesDashboard(ranking) {

    const contenedor =
        document.getElementById(
            "rankingFacultad"
        );

    if (!contenedor) return;

    if (!Array.isArray(ranking) || ranking.length === 0) {

        contenedor.innerHTML = `
            <div class="dashboard-loading">
                No existen datos de facultades.
            </div>
        `;

        return;

    }


    // ==========================================
    // AGRUPAR POR FACULTAD
    // ==========================================

    const facultades = {};

    ranking.forEach(item => {

        const facultad =
            item.facultad?.trim() ||
            "Sin facultad";

        const total =
            Number(item.total ?? 0);

        if (!facultades[facultad]) {

            facultades[facultad] = 0;

        }

        facultades[facultad] += total;

    });


    // ==========================================
    // ORDENAR
    // ==========================================

    const lista =
        Object.entries(facultades)
            .map(([facultad, total]) => ({
                facultad,
                total
            }))
            .sort((a, b) => b.total - a.total)
            .slice(0, 5);


    // ==========================================
    // MOSTRAR
    // ==========================================

    contenedor.innerHTML =
        lista.map((item, index) => {

            let puesto;

            if (index === 0) {
                puesto = "🥇";
            } else if (index === 1) {
                puesto = "🥈";
            } else if (index === 2) {
                puesto = "🥉";
            } else {
                puesto = index + 1;
            }

            return `
                <div class="dashboard-ranking-item">

                    <div class="dashboard-ranking-position">
                        ${puesto}
                    </div>

                    <div class="dashboard-ranking-info">

                        <strong>
                            ${escapeHtml(item.facultad)}
                        </strong>

                        <small>
                            Puntaje acumulado
                        </small>

                    </div>

                    <div class="dashboard-ranking-score">
                        ${item.total.toFixed(2)}
                    </div>

                </div>
            `;

        }).join("");

}


    /* ==================================================
       GRÁFICA POR FACULTAD
    ================================================== */

    function construirGraficaFacultades(ranking) {

        const canvas =
            document.getElementById(
                "graficoFacultades"
            );

        if (!canvas) return;

        if (typeof Chart === "undefined") {

            console.error(
                "Chart.js no está cargado."
            );

            return;

        }


        // ==========================================
        // AGRUPAR FACULTADES
        // ==========================================

        const facultades = {};

        ranking.forEach(item => {

            const facultad =
                item.facultad?.trim() ||
                "Sin facultad";

            const total =
                Number(item.total ?? 0);

            if (!facultades[facultad]) {

                facultades[facultad] = 0;

            }

            facultades[facultad] += total;

        });


        // ==========================================
        // ORDENAR TOP 10
        // ==========================================

        const datos =
            Object.entries(facultades)
                .map(
                    ([facultad, total]) => ({
                        facultad,
                        total
                    })
                )
                .sort(
                    (a, b) =>
                        b.total - a.total
                )
                .slice(0, 10);


        const etiquetas =
            datos.map(
                item => item.facultad
            );


        const valores =
            datos.map(
                item => item.total
            );


        // ==========================================
        // DESTRUIR GRÁFICA ANTERIOR
        // ==========================================

        if (graficoFacultades) {

            graficoFacultades.destroy();

        }


        // ==========================================
        // CREAR GRÁFICA
        // ==========================================

        graficoFacultades =
            new Chart(
                canvas,
                {

                    type: "bar",

                    data: {

                        labels:
                            etiquetas,

                        datasets: [

                            {

                                label:
                                    "Puntaje acumulado",

                                data:
                                    valores

                            }

                        ]

                    },

                    options: {

                        responsive: true,

                        maintainAspectRatio: false,

                        plugins: {

                            legend: {

                                display: false

                            }

                        },

                        scales: {

                            y: {

                                beginAtZero: true,

                                title: {

                                    display: true,

                                    text:
                                        "Puntaje"

                                }

                            },

                            x: {

                                ticks: {

                                    autoSkip: false,

                                    maxRotation: 45,

                                    minRotation: 25

                                }

                            }

                        }

                    }

                }
            );

    }


    /* ==================================================
       ERROR RANKING
    ================================================== */

    function mostrarErrorRankingDashboard(mensaje) {

        const general =
            document.getElementById(
                "rankingGeneral"
            );

        const facultad =
            document.getElementById(
                "rankingFacultad"
            );

        const contenido = `
            <div class="dashboard-loading">
                ${escapeHtml(mensaje)}
            </div>
        `;

        if (general) {
            general.innerHTML = contenido;
        }

        if (facultad) {
            facultad.innerHTML = contenido;
        }

    }


    /* ==================================================
       FECHA
    ================================================== */

    function actualizarFecha() {

        const elemento =
            document.getElementById(
                "ultimaActualizacion"
            );

        if (!elemento) return;

        elemento.textContent =
            new Date().toLocaleString(
                "es-EC",
                {
                    dateStyle: "short",
                    timeStyle: "short"
                }
            );

    }


    /* ==================================================
       NAVEGACIÓN
    ================================================== */

    function irRankingGeneral() {

        if (
            typeof cargarModulo === "function"
        ) {

            cargarModulo(
                "rankingGeneral"
            );

        }

    }


    function irRankingFacultad() {

        if (
            typeof cargarModulo === "function"
        ) {

            cargarModulo(
                "rankingCarrera"
            );

        }

    }


    /* ==================================================
       SEGURIDAD
    ================================================== */

    function escapeHtml(valor) {

        if (
            valor === null ||
            valor === undefined
        ) {

            return "";

        }

        return String(valor)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");

    }


    /* ==================================================
       EXPONER FUNCIONES
    ================================================== */

    window.inicializarDashboard =
        inicializarDashboard;

    window.irRankingGeneral =
        irRankingGeneral;

    window.irRankingFacultad =
        irRankingFacultad;

})();