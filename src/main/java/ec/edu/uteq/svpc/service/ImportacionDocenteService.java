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

            for (int i = 3; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    omitidos++;
                    continue;
                }

                /*
                 * ==========================================
                 * COLUMNAS DEL DISTRIBUTIVO
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
                 * 10 TOTAL HORAS
                 * 11 HORAS ACTIVIDADES PLANIFICADAS
                 * 12 HORAS ACTIVIDADES SOLICITADAS
                 * 13 ACTIVIDADES COMPLETAS
                 * 14 TELEFONO
                 * 15 CIUDAD
                 * 16 DIRECCION
                 * 17 CARRERA
                 * 18 EMAIL PERSONAL
                 * 19 EMAIL INSTITUCIONAL
                 * 20 TABLA PONDERATIVA
                 * 21 ACTIVO
                 * 22 USUARIO
                 * 23 PAIS_ORIGEN
                 * 24 DISCAPACIDAD
                 * 25 PORCENTAJE_DISCAPACIDAD
                 * 26 NUMERO_CONADIS
                 * 27 ETNIA
                 * 28 NACIONALIDAD
                 */

                String cedula = normalizarCedula(
                        obtenerTexto(fila.getCell(2)));

                String nombreCompleto = normalizarTexto(
                        obtenerTexto(fila.getCell(3)));

                String facultad = normalizarTexto(
                        obtenerTexto(fila.getCell(5)));

                String carrera = normalizarTexto(
                        obtenerTexto(fila.getCell(17)));

                String correo = obtenerTexto(
                        fila.getCell(19));

                String activo = normalizarTexto(
                        obtenerTexto(fila.getCell(21)));

                /*
                 * ==========================================
                 * VALIDACIONES
                 * ==========================================
                 */

                if (cedula.equalsIgnoreCase("IDENTIFICACIÓN")
                        || nombreCompleto.equalsIgnoreCase("NOMBRES")) {

                    omitidos++;

                    System.out.println(
                            "Fila de encabezados omitida: " + (i + 1));

                    continue;
                }

                /*
                 * La cédula y el nombre son obligatorios.
                 */
                if (cedula.isBlank() || nombreCompleto.isBlank()) {
                    omitidos++;
                    continue;
                }

                /*
                 * ==========================================
                 * BUSCAR DOCENTE POR CÉDULA
                 * ==========================================
                 */

                Docente docente = docenteRepository
                        .findByCedula(cedula)
                        .orElse(new Docente());

                boolean nuevo = docente.getIdDocente() == null;

                /*
                 * ==========================================
                 * DATOS DEL DOCENTE
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

                if (activo.isBlank()) {

                    docente.setEstado(true);

                } else {

                    docente.setEstado(
                            activo.equals("SI")
                                    || activo.equals("ACTIVO")
                                    || activo.equals("TRUE")
                                    || activo.equals("1"));
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

            return "Importación de docentes finalizada. "
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

    private String obtenerTexto(Cell celda) {

        if (celda == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();

        return formatter
                .formatCellValue(celda)
                .trim();
    }

    private String normalizarCedula(String valor) {

        if (valor == null || valor.isBlank()) {
            return "";
        }

        return valor
                .trim()
                .replaceAll("\\s+", "");
    }

    private String normalizarTexto(String valor) {

        if (valor == null || valor.isBlank()) {
            return "";
        }

        return valor
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private String[] separarNombre(String nombreCompleto) {

        String nombre = nombreCompleto
                .trim()
                .replaceAll("\\s+", " ");

        String[] palabras = nombre.split("\\s+");

        /*
         * Ejemplo:
         *
         * ABAD SUAREZ MANUEL ALBERTO
         *
         * Apellidos:
         * ABAD SUAREZ
         *
         * Nombres:
         * MANUEL ALBERTO
         */

        if (palabras.length < 3) {

            return new String[] {
                    "",
                    nombre
            };
        }

        String apellidos = palabras[0] + " " + palabras[1];

        StringBuilder nombres = new StringBuilder();

        for (int i = 2; i < palabras.length; i++) {

            if (nombres.length() > 0) {
                nombres.append(" ");
            }

            nombres.append(palabras[i]);
        }

        return new String[] {
                apellidos,
                nombres.toString()
        };
    }
}