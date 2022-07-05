package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public interface Header {
    /**
     * Comprueba que el formato en el que vengan los valores sea válido
     * @param value
     * @return
     */
    boolean validate(String value);

    /**
     * Dado un alumno establece la propiedad del alumno al valor dado
     * @param alumno
     * @param value
     * @return
     */
    Alumno setField(Alumno alumno, String value);
}
