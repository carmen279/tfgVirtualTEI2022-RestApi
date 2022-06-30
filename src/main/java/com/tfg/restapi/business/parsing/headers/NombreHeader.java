package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public class NombreHeader implements Header {

    @Override
    public boolean validate(String value) {
        return value.split(",").length == 2;
    }

    @Override
    public Alumno setField(Alumno alumno, String value) {
        alumno.setNombrePila(value.split(",")[1]);
        return alumno;
    }
}
