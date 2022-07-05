package com.tfg.restapi.business.parsing.headers;

import com.tfg.restapi.model.Alumno;

public class DniHeader implements Header {

    @Override
    public boolean validate(String value) {
        return value.length() >= 8;
    }

    @Override
    public Alumno setField(Alumno alumno, String value) {
        alumno.setDni(anonymizeDocument(value));
        return alumno;
    }

    public static void main(String[] args) {
        System.out.println(anonymizeDocument("ABC123456"));
    }

    /**
     * Anonimiza los documentos identificativos de los alumnos
     * @param dni, nie o pasaporte
     * @return documento anonimizado
     */
    private static String anonymizeDocument(String dni) {

        if (documentDni(dni)) {
            return "***" + dni.substring(3,7) + "**";
        }
        else if (documentNie(dni)) {
            return "****" + dni.substring(4,8) + "*";
        }
        else if (documentPassport(dni)) {
            return "***" + dni.substring(5,9);
        }
        else if (dni.length() > 4) {
            return "****" + dni.substring(4);
        }
        else {
            return dni;
        }
    }

    /**
     * Comprueba si un string tiene formato de dni
     * @param dni string para comprobar si cumple el formato
     * @return true si cumple el formato, false si no
     */
    private static boolean documentDni(String dni) {
        return dni.matches("\\d{8}[a-zA-Z]");
    }

    /**
     * Comprueba si un string tiene formato de nie
     * @param dni string para comprobar si cumple el formato
     * @return true si cumple el formato, false si no
     */
    private static boolean documentNie(String dni) {
        return dni.matches("[a-zA-Z]\\d{7}[a-zA-Z]");
    }

    /**
     * Comprueba si un string tiene formato de pasaporte
     * @param dni string para comprobar si cumple el formato
     * @return true si cumple el formato, false si no
     */
    private static boolean documentPassport(String dni) {
        return dni.matches("[a-zA-Z]{3}\\d{6}");
    }
}
