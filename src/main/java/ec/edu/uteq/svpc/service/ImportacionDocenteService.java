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

    public ImportacionDocenteService(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    public String importarDocentes(MultipartFile archivo) {

        int insertados = 0;
        int actualizados = 0;
        int omitidos = 0;

        try (InputStream inputStream = archivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet hoja = workbook.getSheetAt(0);

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);

                if (fila == null) {
                    omitidos++;
                    continue;
                }

                String cedula = obtenerTexto(fila.getCell(0));
                String nombres = obtenerTexto(fila.getCell(1));
                String apellidos = obtenerTexto(fila.getCell(2));
                String correo = obtenerTexto(fila.getCell(3));
                String facultad = obtenerTexto(fila.getCell(4));
                String carrera = obtenerTexto(fila.getCell(5));
                String idAreaTexto = obtenerTexto(fila.getCell(6));

                if (cedula.isBlank() || nombres.isBlank() || apellidos.isBlank()) {
                    omitidos++;
                    continue;
                }

                Docente docente = docenteRepository
                        .findByCedula(cedula)
                        .orElse(new Docente());

                boolean nuevo = docente.getIdDocente() == null;

                docente.setCedula(cedula);
                docente.setNombres(nombres);
                docente.setApellidos(apellidos);
                docente.setCorreo(correo);
                docente.setFacultad(facultad);
                docente.setCarrera(carrera);
                docente.setEstado(true);

                if (!idAreaTexto.isBlank()) {
                    docente.setIdArea(Integer.parseInt(idAreaTexto));
                }

                docenteRepository.save(docente);

                if (nuevo) {
                    insertados++;
                } else {
                    actualizados++;
                }
            }

            return "Importación finalizada. Insertados: " + insertados +
                    ", actualizados: " + actualizados +
                    ", omitidos: " + omitidos;

        } catch (Exception e) {
            throw new RuntimeException("Error al importar docentes: " + e.getMessage(), e);
        }
    }

    private String obtenerTexto(Cell celda) {
        if (celda == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(celda).trim();
    }
}