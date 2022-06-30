package com.tfg.restapi.business.parsing;

import com.tfg.restapi.model.Alumno;
import com.tfg.restapi.model.Pareja;

import java.util.List;

public class ObjectToJsonParser {
    public String parseAlumnos(List<Alumno> tutores, List<Alumno> tutorizados) {
        StringBuilder jsonSb = new StringBuilder();
        jsonSb.append("{\"tutores\": [");
        if (tutores.size() > 0){
            for (Alumno alumno : tutores)
                jsonSb.append(parseAlumno(alumno));
            jsonSb.deleteCharAt(jsonSb.toString().length()-1);
        }
        jsonSb.append("],\"tutorizados\": [");
        if (tutorizados.size() > 0) {
            for (Alumno alumno : tutorizados)
                jsonSb.append(parseAlumno(alumno));
            jsonSb.deleteCharAt(jsonSb.toString().length() - 1);
        }
        jsonSb.append("]}");

        return jsonSb.toString();
    }

    private String parseAlumno(Alumno alumno) {
        return "{\"email\":\"" + alumno.getEmail() + "\"," +
                "\"dni\":\"" + alumno.getDni() + "\"," +
                "\"nombrePila\":\"" + alumno.getNombrePila() + "\"," +
                "\"curso\":\"" + alumno.getCurso() + "\"," +
                "\"unidad\":\"" + alumno.getUnidad() + "\"," +
                "\"perfil\":{" +
                "\"sinceridad\":" + alumno.getPerfil().getSinceridad() + "," +
                "\"liderazgo\":" + alumno.getPerfil().getLiderazgo() + "," +
                "\"autocontrol\":" + alumno.getPerfil().getAutocontrol() + "," +
                "\"consideracion\":" + alumno.getPerfil().getConsideracion() + "," +
                "\"retraimiento\":" + alumno.getPerfil().getRetraimiento() + "," +
                "\"ansiedad\":" + alumno.getPerfil().getAnsiedad() +
                "}},";
    }

    public String parseParejas(List<Pareja> parejas) {
        StringBuilder jsonSb = new StringBuilder();
        jsonSb.append("[");
        if (parejas.size() > 0) {
            for (Pareja pareja : parejas)
                jsonSb.append(parsePareja(pareja));
            jsonSb.deleteCharAt(jsonSb.toString().length() - 1);
        }
        jsonSb.append("]");

        return jsonSb.toString();
    }

    public String parsePareja(Pareja pareja){
        String parejaStr = "{\"tutor\": "+ parseAlumno(pareja.getTutor()) +
                "\"tutorizado\": " + parseAlumno(pareja.getTutorizado());

        parejaStr = parejaStr.substring(0,parejaStr.length()-1);

        return parejaStr + "},";
    }

    public String parseToReport(List<Alumno> candidatosTutores, List<Alumno> candidatosTutorizados, List<Alumno> seleccionadosTutores, List<Alumno> seleccionadosTutorizados, List<Pareja> parejas) {
        StringBuilder sb = new StringBuilder();
        sb.append( "VirtualTEI - Resultados del proceso de emparejamiento\n\n\n\n");
        sb.append( "1. CANDIDATOS A PARTICIPAR EN EL PROGRAMA\n\n\n");
        sb.append( "\t1.1. CANDIDATOS A TUTORES\n\n");
        parseAlumnoContent(candidatosTutores, sb);

        sb.append( "\n\n\t1.2. CANDIDATOS A TUTORIZADOS\n\n");
        parseAlumnoContent(candidatosTutorizados, sb);

        sb.append( "\n\n\n2. SELECCIONADOS PARA PARTICIPAR\n\n\n");
        sb.append( "\t2.1. SELECCIONADOS COMO TUTORES\n\n");
        parseAlumnoContentSimplified(seleccionadosTutores, sb);
        sb.append( "\n\n\t2.2. SELECCIONADOS COMO TUTORIZADOS\n\n");
        parseAlumnoContentSimplified(seleccionadosTutorizados, sb);

        sb.append( "\n\n\n3. PAREJAS SELECCIONADAS\n\n\n");
        for (Pareja pareja : parejas) {
            sb.append("\tPareja:\n");
            sb.append("\t\tTutor: ").append(pareja.getTutor().getEmail()).append("\n");
            sb.append("\t\tTutorizado: ").append(pareja.getTutorizado().getEmail()).append("\n\n");
        }
        return sb.toString();
    }

    private void parseAlumnoContentSimplified(List<Alumno> seleccionadosTutores, StringBuilder sb) {
        for (Alumno alumno : seleccionadosTutores) {
            sb.append("\tAlumno:\n");
            sb.append("\t\tEmail: ").append(alumno.getEmail()).append("\n");
            sb.append("\t\tNombre: ").append(alumno.getNombrePila()).append("\n");
            sb.append("\t\tDni: ").append(alumno.getDni()).append("\n");
            sb.append("\t\tCurso: ").append(alumno.getCurso()).append("\n");
            sb.append("\t\tUnidad: ").append(alumno.getUnidad()).append("\n\n");
        }
    }

    private void parseAlumnoContent(List<Alumno> candidatosTutores, StringBuilder sb) {
        for (Alumno alumno : candidatosTutores) {
            sb.append("\tAlumno:\n");
            sb.append("\t\tEmail: ").append(alumno.getEmail()).append("\n");
            sb.append("\t\tNombre: ").append(alumno.getNombrePila()).append("\n");
            sb.append("\t\tDni: ").append(alumno.getDni()).append("\n");
            sb.append("\t\tCurso: ").append(alumno.getCurso()).append("\n");
            sb.append("\t\tUnidad: ").append(alumno.getUnidad()).append("\n");
            if (alumno.getPerfil() != null){
                sb.append("\t\tPerfil prosocial:\n");
                sb.append("\t\t\tSinceridad: ").append(alumno.getPerfil().getSinceridad()).append("\n");
                sb.append("\t\t\tLiderazgo: ").append(alumno.getPerfil().getLiderazgo()).append("\n");
                sb.append("\t\t\tAutocontrol: ").append(alumno.getPerfil().getAutocontrol()).append("\n");
                sb.append("\t\t\tConsideración: ").append(alumno.getPerfil().getConsideracion()).append("\n");
                sb.append("\t\t\tRetraimiento social: ").append(alumno.getPerfil().getRetraimiento()).append("\n");
                sb.append("\t\t\tAnsiedad social/Timidez: ").append(alumno.getPerfil().getAnsiedad()).append("\n\n");
            }
        }
    }
}
