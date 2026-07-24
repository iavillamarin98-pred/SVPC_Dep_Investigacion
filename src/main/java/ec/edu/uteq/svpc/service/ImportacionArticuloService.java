package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Articulo;
import ec.edu.uteq.svpc.entity.ArticuloDocente;
import ec.edu.uteq.svpc.entity.ArticuloDocenteId;
import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.repository.ArticuloDocenteRepository;
import ec.edu.uteq.svpc.repository.ArticuloRepository;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;

@Service
public class ImportacionArticuloService {

    private final ArticuloRepository articuloRepository;
    private final DocenteRepository docenteRepository;
    private final ArticuloDocenteRepository articuloDocenteRepository;

    public ImportacionArticuloService(
            ArticuloRepository articuloRepository,
            DocenteRepository docenteRepository,
            ArticuloDocenteRepository articuloDocenteRepository
    ) {
        this.articuloRepository = articuloRepository;
        this.docenteRepository = docenteRepository;
        this.articuloDocenteRepository = articuloDocenteRepository;
    }

    public String importarArticulos(MultipartFile archivo, Long idProceso) {

        int articulosInsertados = 0;
        int articulosActualizados = 0;
        int docentesInsertados = 0;
        int docentesActualizados = 0;
        int relacionesGuardadas = 0;
        int relacionesActualizadas = 0;
        int filasOmitidas = 0;

        try (InputStream inputStream = archivo.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet hoja = workbook.getSheetAt(0);

            /*
             * Estructura del Excel:
             * fila 0 = título institucional
             * fila 1 = vacía
             * fila 2 = encabezados
             * fila 3 en adelante = datos reales
             */
            for (int i = 3; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    filasOmitidas++;
                    continue;
                }

                String codigo = texto(fila.getCell(1));
                String tituloArticulo = texto(fila.getCell(2));
                String issn = texto(fila.getCell(6));

                String baseIndexada = normalizarBaseIndexada(texto(fila.getCell(8)));
                String revista = texto(fila.getCell(9));
                String areaConocimiento = texto(fila.getCell(11));

                /*
                 * Columna 14 = ESTADO DE PUBLICACIÓN
                 * Ejemplo: PUBLICADO
                 */
                String estadoPublicacion = normalizarTextoGeneral(texto(fila.getCell(14)));

                String cedula = texto(fila.getCell(15));

                /*
                 * Columna 16 = ESTADO
                 * Ejemplo: APROBADO
                 * Este es el campo correcto para filtrar.
                 */
                String estadoRevision = normalizarTextoGeneral(texto(fila.getCell(16)));

                String tipoParticipante = normalizarRolParticipante(texto(fila.getCell(17)));
                String participante = texto(fila.getCell(18));

                String periodo = normalizarPeriodo(texto(fila.getCell(22)));
                String carrera = normalizarTextoGeneral(texto(fila.getCell(23)));
                String cuartil = normalizarCuartil(texto(fila.getCell(25)));

                /*
                 * Solo se importan registros aprobados.
                 */
                if (!estadoRevision.equals("APROBADO")) {
                    filasOmitidas++;
                    continue;
                }

                /*
                 * Validaciones mínimas para poder guardar.
                 */
                if (codigo.isBlank()
                        || tituloArticulo.isBlank()
                        || cedula.isBlank()
                        || participante.isBlank()) {
                    filasOmitidas++;
                    continue;
                }

                /*
                 * Guardar o actualizar artículo.
                 * El artículo se identifica por idProceso + codigo.
                 */
                Articulo articulo = articuloRepository
                        .findByIdProcesoAndCodigo(idProceso, codigo)
                        .orElse(new Articulo());

                boolean articuloNuevo = articulo.getIdArticulo() == null;

                articulo.setIdProceso(idProceso);
                articulo.setCodigo(codigo);
                articulo.setTitulo(tituloArticulo);
                articulo.setIssn(issn);
                articulo.setRevista(revista);
                articulo.setBaseIndexada(baseIndexada);
                articulo.setCuartil(cuartil);
                articulo.setAreaArticulo(areaConocimiento);
                articulo.setTipoParticipante(tipoParticipante);
                articulo.setPeriodo(periodo);

                /*
                 * estado = ESTADO DE PUBLICACIÓN
                 * estadoRevision = ESTADO del Excel
                 */
                articulo.setEstado(estadoPublicacion);
                articulo.setEstadoRevision(estadoRevision);

                articulo = articuloRepository.save(articulo);

                if (articuloNuevo) {
                    articulosInsertados++;
                } else {
                    articulosActualizados++;
                }

                /*
                 * Guardar o actualizar docente.
                 */
                Docente docente = docenteRepository
                        .findByCedula(cedula)
                        .orElse(new Docente());

                boolean docenteNuevo = docente.getIdDocente() == null;

                docente.setCedula(cedula);

                String[] nombresSeparados = separarNombre(participante);
                docente.setApellidos(nombresSeparados[0]);
                docente.setNombres(nombresSeparados[1]);

                docente.setCorreo(null);
                docente.setFacultad(null);
                docente.setCarrera(carrera);
                docente.setEstado(true);

                docente = docenteRepository.save(docente);

                if (docenteNuevo) {
                    docentesInsertados++;
                } else {
                    docentesActualizados++;
                }

                /*
                 * Guardar o actualizar relación artículo-docente.
                 */
                ArticuloDocenteId relacionId = new ArticuloDocenteId(
                        articulo.getIdArticulo(),
                        docente.getIdDocente()
                );

                ArticuloDocente relacion = articuloDocenteRepository
                        .findById(relacionId)
                        .orElse(new ArticuloDocente());

                boolean relacionNueva = relacion.getId() == null;

                relacion.setId(relacionId);
                relacion.setRolParticipante(tipoParticipante);
                relacion.setPuntajeObtenido(BigDecimal.ZERO);

                articuloDocenteRepository.save(relacion);

                if (relacionNueva) {
                    relacionesGuardadas++;
                } else {
                    relacionesActualizadas++;
                }
            }

            return "Importación finalizada. " +
                    "Artículos insertados: " + articulosInsertados +
                    ", artículos actualizados: " + articulosActualizados +
                    ", docentes insertados: " + docentesInsertados +
                    ", docentes actualizados: " + docentesActualizados +
                    ", relaciones guardadas: " + relacionesGuardadas +
                    ", relaciones actualizadas: " + relacionesActualizadas +
                    ", filas omitidas: " + filasOmitidas;

        } catch (Exception e) {
            throw new RuntimeException("Error al importar artículos: " + e.getMessage(), e);
        }
    }

    private String texto(Cell celda) {
        if (celda == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(celda).trim();
    }

    private String normalizarTextoGeneral(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }

        return valor
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private String normalizarBaseIndexada(String valor) {
        if (valor == null || valor.isBlank()) {
            return "NO APLICA";
        }

        String limpio = valor
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll(",+$", "")
                .toUpperCase();

        if (limpio.isBlank()) {
            return "NO APLICA";
        }

        return limpio;
    }

    private String normalizarCuartil(String valor) {
        if (valor == null || valor.isBlank()) {
            return "NO APLICA";
        }

        String limpio = valor
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();

        return switch (limpio) {
            case "NAQ1", "Q1" -> "Q1";
            case "NAQ2", "Q2" -> "Q2";
            case "NAQ3", "Q3" -> "Q3";
            case "NAQ4", "Q4" -> "Q4";
            case "NO APLICA", "N/A", "NA", "NO APLICA." -> "NO APLICA";
            default -> limpio;
        };
    }

    private String normalizarRolParticipante(String valor) {
        if (valor == null || valor.isBlank()) {
            return "SIN ROL";
        }

        String limpio = valor
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();

        return switch (limpio) {
            case "AUTOR", "AUTOR PRINCIPAL" -> "AUTOR";
            case "COAUTOR", "CO-AUTOR", "CO AUTOR" -> "COAUTOR";
            default -> limpio;
        };
    }

    private String normalizarPeriodo(String valor) {
        if (valor == null || valor.isBlank()) {
            return "SIN PERIODO";
        }

        return valor
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private String[] separarNombre(String participante) {

        String limpio = participante
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();

        String[] partes = limpio.split(" ");

        /*
         * Caso común:
         * APELLIDO1 APELLIDO2 NOMBRE1 NOMBRE2
         */
        if (partes.length >= 4) {
            String apellidos = partes[0] + " " + partes[1];
            StringBuilder nombres = new StringBuilder();

            for (int i = 2; i < partes.length; i++) {
                nombres.append(partes[i]).append(" ");
            }

            return new String[]{
                    apellidos.trim(),
                    nombres.toString().trim()
            };
        }

        /*
         * Caso:
         * APELLIDO1 APELLIDO2 NOMBRE1
         */
        if (partes.length == 3) {
            return new String[]{
                    partes[0] + " " + partes[1],
                    partes[2]
            };
        }

        /*
         * Caso:
         * APELLIDO NOMBRE
         */
        if (partes.length == 2) {
            return new String[]{
                    partes[0],
                    partes[1]
            };
        }

        return new String[]{
                "SIN APELLIDO",
                limpio
        };
    }
}