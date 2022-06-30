package com.tfg.restapi.model;

public class Pareja {
    private Alumno tutor;
    private Alumno tutorizado;

    public Pareja(Alumno tutor, Alumno tutorizado) {
        this.tutor = tutor;
        this.tutorizado = tutorizado;
    }

    public Alumno getTutor() {
        return tutor;
    }

    public Alumno getTutorizado() {
        return tutorizado;
    }
}
