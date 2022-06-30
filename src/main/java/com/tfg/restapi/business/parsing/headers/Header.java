package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public interface Header {
    boolean validate(String value);

    Alumno setField(Alumno alumno, String value);
}
