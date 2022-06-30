package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public class EmailHeader implements Header {

    @Override
    public boolean validate(String value) {
        return value.endsWith("@educastur.es");
    }

    @Override
    public Alumno setField(Alumno alumno, String value) {
        alumno.setEmail(value);
        return alumno;
    }
}
