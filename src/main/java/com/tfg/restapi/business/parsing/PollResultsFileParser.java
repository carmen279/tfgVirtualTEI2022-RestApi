package com.tfg.restapi.business.parsing;

import com.tfg.restapi.model.Alumno;
import com.tfg.restapi.model.PerfilProsocial;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Iterator;
import java.util.List;

public class PollResultsFileParser extends FileParser {

    /**
     * Por cada linea coge los valores del correo y del alumno, creando un array de boolean según las respuestas de la encuesta
     * @param actualResult
     * @param cellsInRow
     * @return
     */
    @Override
    public List<Object> processRow(List<Object> actualResult, Iterator<Cell> cellsInRow) {

        int cellCounter = 0;
        boolean isHeader = false;
        Alumno alumno = new Alumno();
        boolean[] responses = new boolean[75];

        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            // each cell case
            String cellContent;
            if (currentCell.getCellType() == CellType.NUMERIC){
                cellContent = Double.toString(currentCell.getNumericCellValue());
            } else {
                cellContent = currentCell.getStringCellValue();
            }

            if (cellContent.equals("ID")) {
                isHeader = true;
                break;
            } else {
               if(cellCounter == 5) {
                   alumno.setEmail(cellContent);
               } else if (cellCounter >= 6) {
                   responses[cellCounter-6] = cellContent.equals("SI");
                }
            }
            cellCounter++;
        }

        if(!isHeader){
            alumno.setPerfil(new PerfilProsocial(responses));
            actualResult.add(alumno);
        }

        return actualResult;
    }
}
