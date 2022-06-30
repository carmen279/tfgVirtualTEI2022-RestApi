package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public class CursoHeader implements Header {

    @Override
    public boolean validate(String value) {
        return true;
    }

    @Override
    public Alumno setField(Alumno alumno, String value) {
        alumno.setCurso(value);
        return alumno;
    }
}
