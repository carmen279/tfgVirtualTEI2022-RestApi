package com.tfg.restapi.business.parsing;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Iterator;
import java.util.List;

public class EmailFileParser extends FileParser {

    /**
     * Procesa cada línea y devuelve una lista con un email por cada línea procesada (con el formato correcto)
     * @param actualResult
     * @param cellsInRow
     * @return
     */
    @Override
    public List<Object> processRow(List<Object> actualResult, Iterator<Cell> cellsInRow) {
        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            // each cell case
            String title = currentCell.getStringCellValue();
            //if(title.endsWith("@educastur.es"))
            if(title.endsWith("@gmail.com"))
                actualResult.add(title);
        }
        return actualResult;
    }
}
