package com.tfg.restapi.model;

import java.util.Arrays;
import java.util.UUID;

public class PerfilProsocial {
    private final String id;
    private float sinceridad;
    private final int[] sinceridadPositiveResponses = {24,75};
    private final int[] sinceridadNegativeResponses = {7,15,32,41,49,58,67,74};
    private float liderazgo;
    private final int[] liderazgoPositiveResponses = {2,6,10,20,21,23,26,29,33,45,70,71};
    private final int[] liderazgoNegativeResponses = {};
    private float consideracion;
    private final int[] consideracionPositiveResponses = {3,5,11,12,14,16,17,27,51,59,60,61,66,68};
    private final int[] consideracionNegativeResponses = {};
    private float autocontrol;
    private final int[] autocontrolPositiveResponses = {22,36,46,50,53,73};
    private final int[] autocontrolNegativeResponses = {4,13,34,40,44,56,64,65};
    private float retraimiento;
    private final int[] retraimientoPositiveResponses = {8,9,28,35,42,52,63,69,72};
    private final int[] retraimientoNegativeResponses = {25,30,31,37,47};
    private float ansiedad;
    private final int[] ansiedadPositiveResponses = {1,18,19,38,39,43,48,54,55,57,62};
    private final int[] ansiedadNegativeResponses = {33};

    public PerfilProsocial(boolean[] responses) {
        //Generar id random
        this.id= UUID.randomUUID().toString();
        calculateAreas(responses);
    }

    public PerfilProsocial(String id, float sinceridad, float liderazgo, float consideracion, float autocontrol, float retraimiento, float ansiedad) {
        this.id = id;
        this.sinceridad = sinceridad;
        this.liderazgo = liderazgo;
        this.consideracion = consideracion;
        this.autocontrol = autocontrol;
        this.retraimiento = retraimiento;
        this.ansiedad = ansiedad;
    }

    private void calculateAreas(boolean[] responses) {
        this.consideracion = calculateArea(responses, consideracionPositiveResponses, consideracionNegativeResponses);
        this.autocontrol = calculateArea(responses, autocontrolPositiveResponses, autocontrolNegativeResponses);
        this.retraimiento = calculateArea(responses, retraimientoPositiveResponses, retraimientoNegativeResponses);
        this.ansiedad = calculateArea(responses, ansiedadPositiveResponses, ansiedadNegativeResponses);
        this.liderazgo = calculateArea(responses, liderazgoPositiveResponses, liderazgoNegativeResponses);
        this.sinceridad = calculateArea(responses, sinceridadPositiveResponses, sinceridadNegativeResponses);
    }

    private float calculateArea(boolean[] responses, int[] positiveResponses, int[] negativeResponses) {
        float count = Arrays.stream(positiveResponses).filter((index) -> responses[index-1]).toArray().length;
        count += Arrays.stream(negativeResponses).filter((index) -> !responses[index-1]).toArray().length;

        return (count / (positiveResponses.length + negativeResponses.length)) * 10;
    }

    public float getSinceridad() {
        return sinceridad;
    }

    public float getLiderazgo() {
        return liderazgo;
    }

    public float getConsideracion() {
        return consideracion;
    }

    public float getAutocontrol() {
        return autocontrol;
    }

    public float getRetraimiento() {
        return retraimiento;
    }

    public float getAnsiedad() {
        return ansiedad;
    }

    public String getId() {
        return id;
    }
}
