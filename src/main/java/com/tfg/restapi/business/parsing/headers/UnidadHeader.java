package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public class UnidadHeader implements Header {

    @Override
    public boolean validate(String value) {
        return true;
    }

    @Override
    public Alumno setField(Alumno alumno, String value) {
        alumno.setUnidad(value);
        return alumno;
    }
}
