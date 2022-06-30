package com.tfg.restapi.business.parsing;

import com.tfg.restapi.business.parsing.headers.*;
import com.tfg.restapi.model.Alumno;
import org.apache.poi.ss.usermodel.Cell;

import java.util.*;

public class StudentFileParser extends FileParser {
    private final Map<String, Header> stringHeaderMap = initializeStringHeaderMap();
    private final Map<Integer,Header> headerMap = new HashMap<>();

    @Override
    public List<Object> processRow(List<Object> actualResult, Iterator<Cell> cellsInRow) {

        int cellCounter = 0;
        boolean foundFirstAlumno = false;
        Alumno alumno = new Alumno();

        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            // each cell case
            String cellContent = currentCell.getStringCellValue();

            if (headerMap.size() != stringHeaderMap.size()) {
                if (stringHeaderMap.containsKey(cellContent))
                    headerMap.put(cellCounter, stringHeaderMap.get(cellContent));
            } else {
                foundFirstAlumno = true;
                if (headerMap.containsKey(cellCounter)){
                    Header header = headerMap.get(cellCounter);
                    if(header.validate(cellContent))
                        alumno = header.setField(alumno, cellContent);
                }
            }
            cellCounter++;
        }

        if(foundFirstAlumno)
            actualResult.add(alumno);

        return actualResult;
    }

    private Map<String, Header> initializeStringHeaderMap() {
        Map<String, Header> map = new HashMap<>();

        map.put("EMAIL", new EmailHeader());
        map.put("NOMBRE", new NombreHeader());
        map.put("DNIPASAPORTE", new DniHeader());
        map.put("CURSO", new CursoHeader());
        map.put("UNIDAD", new UnidadHeader());

        return map;
    }
}
