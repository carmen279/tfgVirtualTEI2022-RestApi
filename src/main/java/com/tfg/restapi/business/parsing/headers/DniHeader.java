package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public class DniHeader implements Header {

    @Override
    public boolean validate(String value) {
        return value.length() >= 8;
    }

    @Override
    public Alumno setField(Alumno alumno, String value) {
        StringBuilder anonymize = new StringBuilder();
        for (int i=0; i<value.length(); i++){
            if (i<3 || i>value.length()-2){
                anonymize.append(value.charAt(i));
            } else {
                anonymize.append("*");
            }
        }
        alumno.setDni(anonymize.toString());
        return alumno;
    }
}
