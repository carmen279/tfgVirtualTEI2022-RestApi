package com.tfg.restapi.model;

public class Alumno{
    private String email;
    private String dni;
    private String nombrePila;
    private String curso;
    private String unidad;

    private PerfilProsocial perfil;

    public Alumno(String email, String dni, String nombrePila, String curso, String unidad) {
        this.email = email;
        this.dni = dni;
        this.nombrePila = nombrePila;
        this.curso = curso;
        this.unidad = unidad;
    }

    public Alumno() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombrePila() {
        return nombrePila;
    }

    public void setNombrePila(String nombrePila) {
        this.nombrePila = nombrePila;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public PerfilProsocial getPerfil() { return perfil; }

    public void setPerfil(PerfilProsocial perfil) { this.perfil = perfil; }
}
