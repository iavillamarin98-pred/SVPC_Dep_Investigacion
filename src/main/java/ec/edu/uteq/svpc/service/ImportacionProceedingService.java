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
import java.text.Normalizer;
import java.util.Locale;

@Service
public class ImportacionProceedingService {

    private final ArticuloRepository articuloRepository;
    private final DocenteRepository docenteRepository;
    private final ArticuloDocenteRepository articuloDocenteRepository;

    public ImportacionProceedingService(
            ArticuloRepository articuloRepository,
            DocenteRepository docenteRepository,
            ArticuloDocenteRepository articuloDocenteRepository) {
        this.articuloRepository = articuloRepository;
        this.docenteRepository = docenteRepository;
        this.articuloDocenteRepository = articuloDocenteRepository;
    }

    public String importar(MultipartFile archivo, Integer idProceso) {

        int articulosInsertados = 0;
        int articulosActualizados = 0;
        int docentesInsertados = 0;
        int docentesActualizados = 0;
        int relacionesGuardadas = 0;
        int relacionesActualizadas = 0;
        int filasOmitidas = 0;

        try (InputStream inputStream = archivo.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet hoja = workbook.getSheet("Proceedings");

            if (hoja == null) {
                throw new RuntimeException(
                        "No existe la hoja 'Proceedings' dentro del archivo Excel.");
            }

            System.out.println("Hoja leída: " + hoja.getSheetName());
            /*
             * Estructura del Excel
             *
             * fila 0 título
             * fila 1 vacía
             * fila 2 encabezados
             * fila 3 datos
             */

            for (int i = 3; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    filasOmitidas++;
                    continue;
                }

                String tipoProceeding = normalizarTextoGeneral(texto(fila.getCell(0)));

                String tituloProceeding = texto(fila.getCell(2));

                String codigoExcel = texto(fila.getCell(1));

                String codigo = codigoExcel.isBlank()
                        ? generarCodigo(tituloProceeding)
                        : codigoExcel;

                String cuartil = normalizarCuartil(texto(fila.getCell(4)));

                String issn = texto(fila.getCell(6));

                String baseIndexada = normalizarBaseIndexada(texto(fila.getCell(8)));

                String areaConocimiento = normalizarTextoGeneral(texto(fila.getCell(12)));

                String estadoPublicacion = normalizarTextoGeneral(texto(fila.getCell(15)));

                String cedula = texto(fila.getCell(16));

                String estadoRevision = normalizarTextoGeneral(texto(fila.getCell(17)));

                String tipoParticipante = normalizarRolParticipante(texto(fila.getCell(18)));

                String participante = texto(fila.getCell(19));

                String carrera = normalizarTextoGeneral(texto(fila.getCell(23)));

                /*
                 * DEPURACIÓN (TEMPORAL)
                 */
                System.out.println("--------------------------------");
                System.out.println("Titulo : " + tituloProceeding);
                System.out.println("Codigo : " + codigo);
                System.out.println("--------------------------------");

                /*
                 * Solo se importan registros APROBADOS
                 */

                if (!estadoRevision.equals("APROBADO")) {
                    filasOmitidas++;
                    continue;
                }

                /*
                 * Validaciones mínimas
                 */

                if (tituloProceeding.isBlank()
                        || cedula.isBlank()
                        || participante.isBlank()) {

                    filasOmitidas++;
                    continue;
                }

                /*
                 * Guardar o actualizar proceeding.
                 * Se almacena en la tabla ARTICULOS.
                 */
                Articulo articulo = articuloRepository
                        .findByIdProcesoAndCodigo(idProceso.longValue(), codigo)
                        .orElse(new Articulo());

                boolean articuloNuevo = articulo.getIdArticulo() == null;

                articulo.setIdProceso(idProceso.longValue());
                articulo.setCodigo(codigo);

                /*
                 * El proceeding utiliza el mismo campo titulo.
                 */
                articulo.setTitulo(tituloProceeding);

                articulo.setIssn(issn);

                /*
                 * Todos los proceedings se identificarán por esta base
                 * independientemente del valor que venga en el Excel.
                 */
                articulo.setBaseIndexada("PROCEEDING");

                /*
                 * Se conserva el cuartil original únicamente como dato.
                 * El cálculo de puntajes siempre lo tratará como Q4.
                 */
                articulo.setCuartil(cuartil);

                articulo.setAreaArticulo(areaConocimiento);

                articulo.setTipoParticipante(tipoParticipante);

                /*
                 * El archivo de proceedings no posee período.
                 */
                articulo.setPeriodo("NO APLICA");

                articulo.setEstado(estadoPublicacion);
                articulo.setEstadoRevision(estadoRevision);

                /*
                 * Nuevo campo agregado a la entidad.
                 */
                articulo.setTipoProceeding(tipoProceeding);

                /*
                 * No aplica revista para proceedings.
                 */
                articulo.setRevista(null);

                articulo = articuloRepository.save(articulo);

                if (articuloNuevo) {
                    articulosInsertados++;
                } else {
                    articulosActualizados++;
                }

                /*
                 * Guardar o actualizar docente.
                 *
                 * IMPORTANTE:
                 * La importación de Proceedings NO debe modificar
                 * facultad, carrera, correo ni demás información
                 * maestra de un docente existente.
                 */
                Docente docente = docenteRepository
                        .findByCedula(cedula)
                        .orElse(null);

                boolean docenteNuevo = false;

                if (docente == null) {

                    /*
                     * El docente no existe en la base.
                     * Se crea únicamente con la información disponible
                     * en el Excel.
                     */
                    docente = new Docente();

                    docente.setCedula(cedula);

                    String[] nombresSeparados = separarNombre(participante);

                    docente.setApellidos(
                            nombresSeparados[0]);

                    docente.setNombres(
                            nombresSeparados[1]);

                    /*
                     * Estos campos no deben inventarse desde
                     * la importación de producción científica.
                     */
                    docente.setCorreo(null);
                    docente.setFacultad(null);
                    docente.setCarrera(null);
                    docente.setEstado(true);

                    docente = docenteRepository.save(docente);

                    docenteNuevo = true;
                }

                /*
                 * Si el docente ya existía:
                 *
                 * NO se modifica:
                 * - facultad
                 * - carrera
                 * - correo
                 * - nombres
                 * - apellidos
                 * - estado
                 *
                 * La información maestra pertenece a Gestión de Docentes.
                 */
                if (docenteNuevo) {
                    docentesInsertados++;
                } else {
                    docentesActualizados++;
                }

                /*
                 * Guardar relación proceeding-docente.
                 */
                ArticuloDocenteId relacionId = new ArticuloDocenteId(
                        articulo.getIdArticulo(),
                        docente.getIdDocente());

                ArticuloDocente relacion = articuloDocenteRepository
                        .findById(relacionId)
                        .orElse(new ArticuloDocente());

                boolean relacionNueva = relacion.getId() == null;

                relacion.setId(relacionId);
                relacion.setRolParticipante(tipoParticipante);

                /*
                 * El puntaje se calculará posteriormente.
                 */
                relacion.setPuntajeObtenido(BigDecimal.ZERO);

                articuloDocenteRepository.save(relacion);

                if (relacionNueva) {
                    relacionesGuardadas++;
                } else {
                    relacionesActualizadas++;
                }

            } // FIN FOR

            return "Importación de proceedings finalizada. "
                    + "Proceedings insertados: " + articulosInsertados
                    + ", proceedings actualizados: " + articulosActualizados
                    + ", docentes insertados: " + docentesInsertados
                    + ", docentes actualizados: " + docentesActualizados
                    + ", relaciones guardadas: " + relacionesGuardadas
                    + ", relaciones actualizadas: " + relacionesActualizadas
                    + ", filas omitidas: " + filasOmitidas;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al importar proceedings: " + e.getMessage(), e);
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
            return "PROCEEDING";
        }

        String limpio = valor
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll(",+$", "")
                .toUpperCase();

        if (limpio.isBlank()) {
            return "PROCEEDING";
        }

        return limpio;
    }

    /**
     * El Excel de Proceedings puede venir con:
     *
     * 1
     * 2
     * 3
     * 4
     * 0
     *
     * o también:
     *
     * Q1
     * Q2
     * Q3
     * Q4
     * NAQ1
     * ...
     */
    private String normalizarCuartil(String valor) {

        if (valor == null || valor.isBlank()) {
            return "NO APLICA";
        }

        String limpio = valor
                .trim()
                .replaceAll("\\s+", "")
                .toUpperCase();

        return switch (limpio) {

            case "1", "Q1", "NAQ1" -> "Q1";

            case "2", "Q2", "NAQ2" -> "Q2";

            case "3", "Q3", "NAQ3" -> "Q3";

            case "4", "Q4", "NAQ4" -> "Q4";

            case "0", "Q0" -> "Q0";

            case "NOAPLICA",
                    "NO APLICA",
                    "N/A",
                    "NA" ->
                "NO APLICA";

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

            case "AUTOR",
                    "AUTOR PRINCIPAL" ->
                "AUTOR";

            case "COAUTOR",
                    "CO-AUTOR",
                    "CO AUTOR" ->
                "COAUTOR";

            default -> limpio;
        };
    }

    private String[] separarNombre(String participante) {

        String limpio = participante
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();

        String[] partes = limpio.split(" ");

        if (partes.length >= 4) {

            String apellidos = partes[0] + " " + partes[1];

            StringBuilder nombres = new StringBuilder();

            for (int i = 2; i < partes.length; i++) {
                nombres.append(partes[i]).append(" ");
            }

            return new String[] {
                    apellidos.trim(),
                    nombres.toString().trim()
            };
        }

        if (partes.length == 3) {

            return new String[] {
                    partes[0] + " " + partes[1],
                    partes[2]
            };
        }

        if (partes.length == 2) {

            return new String[] {
                    partes[0],
                    partes[1]
            };
        }

        return new String[] {
                "SIN APELLIDO",
                limpio
        };
    }

    private String generarCodigo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            return "PROC-SIN-TITULO";
        }

        String limpio = Normalizer.normalize(titulo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]", "");

        return "PROC-" + Integer.toHexString(limpio.hashCode()).toUpperCase();
    }

}