// ==================================================
// RANKING POR FACULTAD
// ==================================================

(function () {

    const API_RANKING_FACULTAD = "/api/rankings/facultad";
    const API_RANKING_GENERAL_2 = "/api/rankings/general";
    const API_FACULTADES = "/api/rankings/facultades";

    let rankingFacultadActual = [];
    let ultimaListaMostrada = [];
    let facultadSeleccionada = "";


    // ==================================================
    // OBTENER ID DE PROCESO ACTIVO
    // ==================================================

    async function obtenerIdProcesoActivoRankingFacultad() {

        if (!PROCESO_ACTIVO) {
            await cargarProcesoActivo();
        }

        return obtenerIdProceso();

    }


    // ==================================================
    // INICIALIZAR
    // ==================================================

    async function inicializarRankingFacultad() {

        await cargarFacultadesDisponibles();
        await cargarRankingFacultad();

    }


    // ==================================================
    // CARGAR LISTA DE FACULTADES (para el <select>)
    // ==================================================

    async function cargarFacultadesDisponibles() {

        try {

            const response = await fetch(API_FACULTADES);

            if (!response.ok) {
                throw new Error("No se pudieron cargar las facultades.");
            }

            const facultades = await response.json();

            const select = document.getElementById("filtroFacultadRanking");

            if (!select) return;

            select.innerHTML = '<option value="">Todas las facultades</option>';

            facultades.forEach(facultad => {

                const option = document.createElement("option");

                option.value = facultad;
                option.textContent = facultad;

                select.appendChild(option);

            });

            select.value = facultadSeleccionada;

        } catch (error) {

            console.error("Error cargando facultades:", error);

        }

    }


    // ==================================================
    // CARGAR RANKING
    // ==================================================

    async function cargarRankingFacultad() {

        const tabla = document.getElementById("tablaRankingFacultad");

        if (tabla) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="12" style="text-align:center;">Cargando...</td>
                </tr>
            `;
        }

        try {

            const idProceso = await obtenerIdProcesoActivoRankingFacultad();

            let url = "";

            if (facultadSeleccionada) {
                url = `${API_RANKING_FACULTAD}?idProceso=${idProceso}&facultad=${encodeURIComponent(facultadSeleccionada)}`;
            } else {
                url = `${API_RANKING_GENERAL_2}?idProceso=${idProceso}`;
            }

            const response = await fetch(url);

            if (!response.ok) {
                throw new Error("Error al cargar el ranking por facultad.");
            }

            const datos = await response.json();

            rankingFacultadActual = Array.isArray(datos) ? datos : [];

            mostrarRankingFacultad(rankingFacultadActual);

        } catch (error) {

            console.error("Error cargando ranking por facultad:", error);

            if (tabla) {
                tabla.innerHTML = `
                    <tr>
                        <td colspan="12" style="text-align:center;">
                            ${escaparRankingFacultad(error.message)}
                        </td>
                    </tr>
                `;
            }

            Notificaciones.error(error.message);

        }

    }


    // ==================================================
    // REFRESCAR (botón manual)
    // ==================================================

    async function refrescarRankingFacultad() {

        Loader.mostrar("Actualizando ranking...");

        try {

            await cargarRankingFacultad();

            Notificaciones.exito("Ranking actualizado correctamente.");

        } finally {

            Loader.ocultar();

        }

    }


    // ==================================================
    // CAMBIO DE FACULTAD
    // ==================================================

    async function cambiarFacultadRanking() {

        const select = document.getElementById("filtroFacultadRanking");

        facultadSeleccionada = select ? select.value : "";

        const buscar = document.getElementById("buscarRankingFacultad");

        if (buscar) buscar.value = "";

        await cargarRankingFacultad();

    }


    // ==================================================
    // MOSTRAR RANKING
    // ==================================================

    function mostrarRankingFacultad(lista) {

        ultimaListaMostrada = Array.isArray(lista) ? lista : [];

        const tabla = document.getElementById("tablaRankingFacultad");

        if (!tabla) return;

        tabla.innerHTML = "";

        if (ultimaListaMostrada.length === 0) {

            tabla.innerHTML = `
                <tr>
                    <td colspan="12" style="text-align:center;">
                        No existen docentes registrados en esta facultad.
                    </td>
                </tr>
            `;

            return;
        }

        ultimaListaMostrada.forEach(item => {

            const nombre = `${item.apellidos ?? ""} ${item.nombres ?? ""}`.trim();

            const puesto = Number(item.puesto ?? 0);

            const fila = document.createElement("tr");

            fila.className = claseFilaRankingFacultad(puesto);

            fila.innerHTML = `
                <td>${crearBadgePuestoFacultad(puesto)}</td>
                <td>${escaparRankingFacultad(item.cedula)}</td>
                <td><strong>${escaparRankingFacultad(nombre)}</strong></td>
                <td>${escaparRankingFacultad(item.facultad || "Sin facultad")}</td>
                <td>${escaparRankingFacultad(item.carrera || "Sin carrera")}</td>
                <td>${Number(item.puntajeArticulos ?? 0).toFixed(2)}</td>
                <td>${Number(item.puntajeProceedings ?? 0).toFixed(2)}</td>
                <td>${Number(item.puntajeLibros ?? 0).toFixed(2)}</td>
                <td>${Number(item.puntajeCapitulos ?? 0).toFixed(2)}</td>
                <td>${Number(item.puntajeProyectos ?? 0).toFixed(2)}</td>
                <td>${Number(item.puntajeBonificaciones ?? 0).toFixed(2)}</td>
                <td><strong>${Number(item.total ?? 0).toFixed(2)}</strong></td>
            `;

            tabla.appendChild(fila);

        });

    }


    // ==================================================
    // CLASE DE FILA / BADGE SEGÚN PUESTO
    // ==================================================

    function claseFilaRankingFacultad(puesto) {

        if (puesto === 1) return "ranking-puesto-1";
        if (puesto === 2) return "ranking-puesto-2";
        if (puesto === 3) return "ranking-puesto-3";
        if (puesto <= 10) return "ranking-top-10";

        return "";

    }


    function crearBadgePuestoFacultad(puesto) {

        if (puesto === 1) return `🥇 ${puesto}`;
        if (puesto === 2) return `🥈 ${puesto}`;
        if (puesto === 3) return `🥉 ${puesto}`;

        return puesto;

    }


    // ==================================================
    // FILTRAR
    // ==================================================

    function filtrarRankingFacultad() {

        const texto = document.getElementById("buscarRankingFacultad")
            ?.value.trim().toLowerCase() || "";

        const filtrados = rankingFacultadActual.filter(item => {

            const nombre = `${item.apellidos ?? ""} ${item.nombres ?? ""}`.toLowerCase();
            const nombreInv = `${item.nombres ?? ""} ${item.apellidos ?? ""}`.toLowerCase();
            const cedula = String(item.cedula ?? "").toLowerCase();

            return (
                !texto ||
                cedula.includes(texto) ||
                nombre.includes(texto) ||
                nombreInv.includes(texto)
            );

        });

        mostrarRankingFacultad(filtrados);

    }


    // ==================================================
    // ESCAPAR HTML
    // ==================================================

    function escaparRankingFacultad(texto) {

        return String(texto ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");

    }


    // ==================================================
    // CARGA DE LIBRERÍA PDF (jsPDF + AutoTable) BAJO DEMANDA
    // ==================================================

    function cargarLibreriaPDF() {

        return new Promise((resolve, reject) => {

            if (window.jspdf && window.jspdf.jsPDF) {
                resolve();
                return;
            }

            const s1 = document.createElement("script");
            s1.src = "https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js";

            s1.onload = () => {

                const s2 = document.createElement("script");
                s2.src = "https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.8.2/jspdf.plugin.autotable.js";

                s2.onload = resolve;
                s2.onerror = () => reject(new Error("No se pudo cargar jspdf-autotable."));

                document.body.appendChild(s2);

            };

            s1.onerror = () => reject(new Error("No se pudo cargar jsPDF."));

            document.body.appendChild(s1);

        });

    }


    // ==================================================
    // ENCABEZADO INSTITUCIONAL DEL PDF
    // ==================================================

    function dibujarEncabezadoPDF(doc, subtitulo) {

        const ancho = doc.internal.pageSize.getWidth();

        const nombreProceso = (typeof PROCESO_ACTIVO !== "undefined" && PROCESO_ACTIVO)
            ? PROCESO_ACTIVO.nombre
            : "N/D";

        const periodo = (typeof PROCESO_ACTIVO !== "undefined" && PROCESO_ACTIVO)
            ? PROCESO_ACTIVO.periodo
            : "N/D";

        doc.setFont("helvetica", "bold");
        doc.setFontSize(13);
        doc.text("UNIVERSIDAD TÉCNICA ESTATAL DE QUEVEDO", ancho / 2, 12, { align: "center" });

        doc.setFontSize(10);
        doc.text("DIRECCIÓN DE INVESTIGACIÓN, CIENCIA Y TECNOLOGÍA", ancho / 2, 18, { align: "center" });

        doc.setFontSize(11);
        doc.text(subtitulo, ancho / 2, 25, { align: "center" });

        doc.setFont("helvetica", "normal");
        doc.setFontSize(9);
        doc.text(`Proceso: ${nombreProceso}`, 14, 32);
        doc.text(`Periodo: ${periodo}`, 14, 37);
        doc.text(`Facultad: ${facultadSeleccionada || "Todas las facultades"}`, 14, 42);

        const fechaTexto = new Date().toLocaleString("es-EC", {
            dateStyle: "long",
            timeStyle: "short"
        });

        doc.text(`Fecha de generación: ${fechaTexto}`, ancho - 14, 32, { align: "right" });
        doc.text(`Total de docentes: ${ultimaListaMostrada.length}`, ancho - 14, 37, { align: "right" });

        doc.setDrawColor(14, 122, 54);
        doc.setLineWidth(0.6);
        doc.line(14, 45, ancho - 14, 45);

    }


    // ==================================================
    // ESTILO DE FILAS SEGÚN PUESTO (AutoTable)
    // ==================================================

    function estiloFilaPDF(data) {

        if (data.section !== "body") return;

        const puesto = Number(data.row.raw[0]);

        if (puesto === 1) {
            data.cell.styles.fillColor = [255, 243, 205];
        } else if (puesto === 2) {
            data.cell.styles.fillColor = [241, 243, 245];
        } else if (puesto === 3) {
            data.cell.styles.fillColor = [248, 232, 220];
        } else if (puesto <= 10) {
            data.cell.styles.fillColor = [255, 248, 220];
        }

    }


    // ==================================================
    // FECHA PARA NOMBRE DE ARCHIVO
    // ==================================================

    function fechaParaArchivo() {

        const ahora = new Date();

        const pad = (n) => String(n).padStart(2, "0");

        return `${ahora.getFullYear()}${pad(ahora.getMonth() + 1)}${pad(ahora.getDate())}_${pad(ahora.getHours())}${pad(ahora.getMinutes())}`;

    }


    // ==================================================
    // EXPORTAR PDF
    // ==================================================

    async function exportarRankingFacultadPDF() {

        if (!ultimaListaMostrada.length) {
            Notificaciones.advertencia("No hay datos para exportar.");
            return;
        }

        Loader.mostrar("Generando PDF...");

        try {

            await cargarLibreriaPDF();

            const { jsPDF } = window.jspdf;

            const doc = new jsPDF({
                orientation: "landscape",
                unit: "mm",
                format: "a4"
            });

            const subtitulo = facultadSeleccionada
                ? `RANKING DE PRODUCCIÓN CIENTÍFICA — ${facultadSeleccionada}`
                : "RANKING GENERAL DE PRODUCCIÓN CIENTÍFICA DOCENTE";

            dibujarEncabezadoPDF(doc, subtitulo);

            const filas = ultimaListaMostrada.map(item => [
                item.puesto,
                item.cedula || "",
                `${item.apellidos ?? ""} ${item.nombres ?? ""}`.trim(),
                item.facultad || "Sin facultad",
                item.carrera || "Sin carrera",
                Number(item.puntajeArticulos ?? 0).toFixed(2),
                Number(item.puntajeProceedings ?? 0).toFixed(2),
                Number(item.puntajeLibros ?? 0).toFixed(2),
                Number(item.puntajeCapitulos ?? 0).toFixed(2),
                Number(item.puntajeProyectos ?? 0).toFixed(2),
                Number(item.puntajeBonificaciones ?? 0).toFixed(2),
                Number(item.total ?? 0).toFixed(2)
            ]);

            doc.autoTable({
                startY: 49,
                head: [[
                    "Puesto", "Cédula", "Docente", "Facultad", "Carrera",
                    "Artículos", "Proceedings", "Libros", "Capítulos",
                    "Proyectos", "Bonificaciones", "Total"
                ]],
                body: filas,
                styles: { fontSize: 7, cellPadding: 2, overflow: "linebreak" },
                headStyles: { fillColor: [14, 122, 54], textColor: 255, fontStyle: "bold" },
                columnStyles: {
                    2: { cellWidth: 42 },
                    3: { cellWidth: 45 },
                    4: { cellWidth: 38 }
                },
                didParseCell: estiloFilaPDF
            });

            const sufijoFacultad = facultadSeleccionada
                ? "_" + facultadSeleccionada.replace(/[^a-zA-Z0-9]+/g, "_").toLowerCase()
                : "_general";

            doc.save(`ranking${sufijoFacultad}_${fechaParaArchivo()}.pdf`);

            Notificaciones.exito("PDF generado correctamente.");

        } catch (error) {

            console.error("Error generando PDF:", error);

            Notificaciones.error("No se pudo generar el PDF.");

        } finally {

            Loader.ocultar();

        }

    }


    // ==================================================
    // EXPONER AL SCOPE GLOBAL
    // ==================================================

    window.filtrarRankingFacultad = filtrarRankingFacultad;
    window.cambiarFacultadRanking = cambiarFacultadRanking;
    window.refrescarRankingFacultad = refrescarRankingFacultad;
    window.exportarRankingFacultadPDF = exportarRankingFacultadPDF;


    // ==================================================
    // INICIALIZACIÓN
    // ==================================================

    inicializarRankingFacultad();

})();