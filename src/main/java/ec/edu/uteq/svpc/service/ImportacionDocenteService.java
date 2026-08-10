package ec.edu.uteq.svpc.service;

import ec.edu.uteq.svpc.entity.Docente;
import ec.edu.uteq.svpc.repository.DocenteRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ImportacionDocenteService {

    private final DocenteRepository docenteRepository;

    public ImportacionDocenteService(
            DocenteRepository docenteRepository) {

        this.docenteRepository = docenteRepository;
    }

    public String importarDocentes(MultipartFile archivo) {

        int insertados = 0;
        int actualizados = 0;
        int omitidos = 0;

        try (
                InputStream inputStream = archivo.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet hoja = workbook.getSheetAt(0);

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    omitidos++;
                    continue;
                }

                /*
                 * ==========================================
                 * COLUMNAS DEL EXCEL MAESTRO
                 * ==========================================
                 *
                 * 0 N.
                 * 1 PERIODO
                 * 2 IDENTIFICACIÓN
                 * 3 NOMBRES
                 * 4 SEXO
                 * 5 FACULTAD
                 * 6 DEDICACION
                 * 7 TIPO
                 * 8 CATEGORIA
                 * 9 ESCALAFON
                 * ...
                 * 17 CARRERA
                 * ...
                 * 19 EMAIL INSTITUCIONAL
                 * 21 ACTIVO
                 */

                String cedula = obtenerTexto(fila.getCell(2));
                String nombreCompleto = obtenerTexto(fila.getCell(3));
                String facultad = obtenerTexto(fila.getCell(5));
                String carrera = obtenerTexto(fila.getCell(17));
                String correo = obtenerTexto(fila.getCell(19));
                String activo = obtenerTexto(fila.getCell(21));

                /*
                 * La cédula es obligatoria porque es
                 * nuestra clave para identificar al docente.
                 */
                if (cedula.isBlank() || nombreCompleto.isBlank()) {
                    omitidos++;
                    continue;
                }

                /*
                 * ==========================================
                 * BUSCAR POR CÉDULA
                 * ==========================================
                 *
                 * Si existe:
                 * actualizar.
                 *
                 * Si no existe:
                 * crear.
                 */
                Docente docente = docenteRepository
                        .findByCedula(cedula)
                        .orElse(new Docente());

                boolean nuevo = docente.getIdDocente() == null;

                /*
                 * ==========================================
                 * DATOS BÁSICOS
                 * ==========================================
                 */

                docente.setCedula(cedula);

                String[] nombreSeparado = separarNombre(nombreCompleto);

                docente.setApellidos(nombreSeparado[0]);
                docente.setNombres(nombreSeparado[1]);

                docente.setFacultad(facultad);
                docente.setCarrera(carrera);
                docente.setCorreo(correo);

                /*
                 * ==========================================
                 * ESTADO
                 * ==========================================
                 */

                if (!activo.isBlank()) {

                    docente.setEstado(
                            activo.equalsIgnoreCase("SI")
                                    || activo.equalsIgnoreCase("ACTIVO")
                                    || activo.equalsIgnoreCase("TRUE")
                                    || activo.equalsIgnoreCase("1"));

                } else {

                    docente.setEstado(true);
                }

                /*
                 * ==========================================
                 * GUARDAR
                 * ==========================================
                 */

                docenteRepository.save(docente);

                if (nuevo) {
                    insertados++;
                } else {
                    actualizados++;
                }
            }

            return "Importación finalizada. "
                    + "Insertados: " + insertados
                    + ", actualizados: " + actualizados
                    + ", omitidos: " + omitidos;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al importar docentes: "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * Obtiene el contenido de una celda como texto.
     */
    private String obtenerTexto(Cell celda) {

        if (celda == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();

        return formatter
                .formatCellValue(celda)
                .trim();
    }

    /**
     * Separa el nombre completo del Excel.
     *
     * Ejemplo:
     *
     * ABAD SUAREZ MANUEL ALBERTO
     *
     * Resultado:
     *
     * Apellidos: ABAD SUAREZ
     * Nombres: MANUEL ALBERTO
     */
    private String[] separarNombre(String nombreCompleto) {

        String nombre = nombreCompleto
                .trim()
                .replaceAll("\\s+", " ");

        String[] palabras = nombre.split("\\s+");

        if (palabras.length < 3) {

            return new String[] {
                    "",
                    nombre
            };
        }

        /*
         * Regla utilizada para el catálogo maestro:
         *
         * Las primeras dos palabras corresponden
         * a los apellidos y el resto a los nombres.
         */
        StringBuilder apellidos = new StringBuilder();
        StringBuilder nombres = new StringBuilder();

        apellidos.append(palabras[0])
                .append(" ")
                .append(palabras[1]);

        for (int i = 2; i < palabras.length; i++) {

            if (i > 2) {
                nombres.append(" ");
            }

            nombres.append(palabras[i]);
        }

        return new String[] {
                apellidos.toString(),
                nombres.toString()
        };
    }
}