package com.tfg.restapi.business.parsing;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class FileParser {

    /**
     * Procesa un archivo excel, devolviendo una lista de los contenidos de cada línea
     * @param excelFile
     * @return
     * @throws IOException
     */
    public List<Object> processExcelSheet(MultipartFile excelFile) throws IOException {

        List<Object> processedInformation = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(excelFile.getInputStream());

        for (Row currentRow : workbook
                .getSheetAt(0)) {

            Iterator<Cell> cellsInRow = currentRow.iterator();

            processedInformation = processRow(processedInformation, cellsInRow);
        }
        workbook.close();

        return processedInformation;
    }

    /**
     * Procesa los contenidos de cada línea en función de la clase en la que se use
     * @param actualResult
     * @param cellsInRow
     * @return
     */
    public abstract List<Object>  processRow(List<Object> actualResult,Iterator<Cell> cellsInRow);
}
