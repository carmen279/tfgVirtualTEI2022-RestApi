package com.tfg.restapi.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tfg.restapi.business.EmailManager;
import com.tfg.restapi.business.parsing.ObjectToJsonParser;
import com.tfg.restapi.business.parsing.PollResultsFileParser;
import com.tfg.restapi.business.parsing.StudentFileParser;
import com.tfg.restapi.configuration.DatabaseConfig;
import com.tfg.restapi.db.DbInterface;
import com.tfg.restapi.model.Alumno;
import com.tfg.restapi.business.parsing.EmailFileParser;
import com.tfg.restapi.model.Pareja;
import com.tfg.restapi.model.PerfilProsocial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class RestAPIController {

    //Parece haber un falso error: ¡Funciona correctamente!
    //Más gente habla de este problema: https://stackoverflow.com/questions/26889970/intellij-incorrectly-saying-no-beans-of-type-found-for-autowired-repository
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;
    DatabaseConfig dbconfig = new DatabaseConfig();
    JdbcTemplate template = new JdbcTemplate(dbconfig.dataSource(dbconfig.dataSourceProperties()));

    DbInterface dbinterface = new DbInterface(template);


    /**
     * Carga los alumnos tutores en la base de datos dado un archivo excel
     * @param excelFile
     * @return
     */
    @PostMapping("/storeTutors")
    public String storeTutors(@Valid @RequestBody MultipartFile excelFile) {
        try {
            List<Object> tutoresObj = (new StudentFileParser()).processExcelSheet(excelFile);
            List<Alumno> tutores = tutoresObj.stream().map((object)->(Alumno)object).toList();

            if (dbinterface.addTutoresToDB(tutores))
                return "Los alumnos se han insertado con éxito a la base de datos.";

            return "Ha habido un problema al cargar los alumnos en la base de datos";

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The excel file provided could not be correctly processed");
        }
    }

    /**
     * Guarda los alumnos tutorizados en la base de datos dado un archivo excel
     * @param excelFile
     * @return
     */
    @PostMapping("/storeTutorees")
    public String storeTutorees(@Valid @RequestBody MultipartFile excelFile) {
        try {
            List<Object> tutorizadosObj = (new StudentFileParser()).processExcelSheet(excelFile);
            List<Alumno> tutorizados = tutorizadosObj.stream().map((object)->(Alumno)object).toList();

            if (dbinterface.addTutorizadosToDB(tutorizados))
                return "Los alumnos se han insertado con éxito a la base de datos.";

            return "Ha habido un problema al cargar los alumnos en la base de datos";

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The excel file provided could not be correctly processed");
        }
    }

    /**
     * Devuelve los datos de los alumnos candidatos tutores y tutorizados en formato json
     * @return
     */
    @GetMapping("/getCandidatesDetails")
    public String getCandidatesDetails() {
        List<Alumno> tutores = dbinterface.getTutorCandidatesDetailsFromDb();
        List<Alumno> tutorizados = dbinterface.getTutorizadoCandidatesDetailsFromDb();
        return (new ObjectToJsonParser()).parseAlumnos(tutores, tutorizados);
    }

    /**
     * Devuelve los datos de los alumnos candidatos seleccionados tutores y tutorizados en formato json
     * @return
     */
    @GetMapping("/getSelectedDetails")
    public String getSelectedDetails() {
        List<Alumno> tutores = dbinterface.getTutorSelectedDetailsFromDb();
        List<Alumno> tutorizados = dbinterface.getTutorizadoSelectedDetailsFromDb();
        return (new ObjectToJsonParser()).parseAlumnos(tutores, tutorizados);
    }

    /**
     * Recibe un json de tutores y tutorizados seleccionados y los guarda en la base de datos
     * @param selectedJson
     * @return
     */
    @PostMapping("/storeSelected")
    public String storeSelected(@Valid @RequestBody String selectedJson) {
        //Recibe json formato {tutores:["ejemplo1@educastur.es, ejemplo2@educastur.es"],tutorizados:["ejemplo3@educastur.es, ejemplo4@educastur.es"]}
        selectedJson = URLDecoder.decode(selectedJson);
        selectedJson = selectedJson.substring(0, selectedJson.length()-1);
        System.out.println(selectedJson);
        JsonObject jsonObject = JsonParser.parseString(selectedJson).getAsJsonObject();
        List<String> tutores = getListFromJsonParam(jsonObject, "tutores");
        List<String> tutorizados = getListFromJsonParam(jsonObject, "tutorizados");

        if (tutores.size() != tutorizados.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The excel file provided could not be correctly processed");

        if (dbinterface.addSelectedToDB(tutores, tutorizados))
            return "la selección de alumnos se ha insertado con éxito a la base de datos.";

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "The excel file provided could not be correctly processed");
    }

    /**
     * Dado un objeto json y una clave string parámetro en el que el parámetro es la key de una lista,
     * devuelve una lista de strings con los contenidos de esa lista
     * @param jsonObject
     * @param jsonParam
     * @return
     */
    private List<String> getListFromJsonParam(JsonObject jsonObject, String jsonParam) {
        JsonArray listJson = jsonObject.getAsJsonArray(jsonParam);
        List<String> list = new ArrayList<>();
        for (JsonElement elem : listJson){
            list.add(elem.getAsString());
        }
        return list;
    }

    /**
     * Guarda las parejas pasadas en formato json
     * @param couplesJson
     * @return
     */
    @PostMapping("/storeCouples")
    public String storeCouples(@Valid @RequestBody String couplesJson) {
        List<Pareja> parejas = getParejasFromJson(couplesJson);

        if (dbinterface.addCouplesToDB(parejas))
            return "la selección de parejas se ha insertado con éxito a la base de datos.";

        return "Ha habido un problema al cargar la selección de parejas en la base de datos";
    }

    /**
     * Dado un string con contenidos en formato json, parsea el contenido y devuelve una lista de objetos parejas
     * @param couplesJson
     * @return
     */
    private List<Pareja> getParejasFromJson(String couplesJson) {
        JsonArray jsonArray = JsonParser.parseString(couplesJson).getAsJsonArray();
        List<Pareja> parejas = new ArrayList<>();
        for (JsonElement parejaJson : jsonArray){
            Alumno tutor = new Alumno();
            tutor.setEmail(parejaJson.getAsJsonObject().get("tutor").getAsString());
            Alumno tutorizado = new Alumno();
            tutorizado.setEmail(parejaJson.getAsJsonObject().get("tutorizado").getAsString());
            parejas.add(new Pareja(tutor,tutorizado));
        }
        return parejas;
    }

    /**
     * Devuelve un String de las parejas de la base de datos en formato json
     * @return
     */
    @GetMapping("/getCouples")
    public String getCouples() {
        List<Pareja> parejas = dbinterface.getParejasFromDb();
        return (new ObjectToJsonParser()).parseParejas(parejas);
    }

    /**
     * Devuelve en json los datos de candidatos a tutores, a tutorizados, los alumnos seleccionados y las parejas finalmente formadas
     * @return
     */
    @GetMapping("/getReport")
    public String getReport() {
        List<Alumno> candidatosTutores = dbinterface.getTutorCandidatesDetailsFromDb();
        List<Alumno> candidatosTutorizados = dbinterface.getTutorizadoCandidatesDetailsFromDb();
        List<Alumno> seleccionadosTutores = dbinterface.getTutorSelectedDetailsFromDb();
        List<Alumno> seleccionadosTutorizados = dbinterface.getTutorizadoSelectedDetailsFromDb();
        List<Pareja> parejas = dbinterface.getParejasFromDb();
        return (new ObjectToJsonParser()).parseToReport(candidatosTutores, candidatosTutorizados, seleccionadosTutores, seleccionadosTutorizados, parejas);
    }

    /**
     * Llama a la base de datos para recoger los candidatos a tutores y tutorizados. Dadas ambas listas,
     * hace un cálculo de puntuación para cada alumno y aquellos con mayor puntuación quedan seleccionados.
     * @return
     */
    @GetMapping("/getSelectionSuggestion")
    public String getSelectionSuggestion() {
        List<Alumno> tutores = dbinterface.getTutorCandidatesDetailsFromDb();
        List<Alumno> tutorizados = dbinterface.getTutorizadoCandidatesDetailsFromDb();

        //Sólo se aceptan tutores que cumplan con unos umbrales mínimos
        List<Alumno> filteredTutores = tutores.stream().filter((tutor) -> (tutor.getPerfil().getAnsiedad()< 7
                && tutor.getPerfil().getRetraimiento()< 5
                && tutor.getPerfil().getConsideracion() >= 5
                && tutor.getPerfil().getAutocontrol() >= 5
                && tutor.getPerfil().getLiderazgo() >= 5
                && tutor.getPerfil().getSinceridad() >= 5)).toList();

        List<Alumno> selectedTutores = orderTutoresByPerfil(filteredTutores);
        List<Alumno> selectedTutorizados = orderTutorizadosByPerfil(tutorizados);

        int length = Math.min(selectedTutores.size(),selectedTutorizados.size());

        selectedTutores = selectedTutores.subList(0,length);
        selectedTutorizados = selectedTutorizados.subList(0,length);

        return (new ObjectToJsonParser()).parseAlumnos(selectedTutores, selectedTutorizados);
    }

    /**
     * Dada una lista de alumnos tutorizados, ordena la lista en función de la puntuación del perfil de cada alumno
     * @param tutorizados
     * @return
     */
    private List<Alumno> orderTutorizadosByPerfil(List<Alumno> tutorizados) {
        ArrayList<Alumno> tutorizadosMutable = new ArrayList<>();

        if (tutorizados.size() > 0) {
            tutorizadosMutable.addAll(tutorizados);
            tutorizadosMutable.sort((alumno1, alumno2) -> {
                PerfilProsocial perfil1 = alumno1.getPerfil();
                PerfilProsocial perfil2 = alumno2.getPerfil();
                int punctuationLiderago = -Float.compare(perfil1.getLiderazgo(), perfil2.getLiderazgo());
                int punctuationAutocontrol = -Float.compare(perfil1.getAutocontrol(), perfil2.getAutocontrol());
                int punctuationConsideracion = -Float.compare(perfil1.getConsideracion(), perfil2.getConsideracion());
                int punctuationAnsiedad = -(Float.compare(perfil1.getAnsiedad(), perfil2.getAnsiedad()));
                int punctuationRetraimiento = (Float.compare(perfil1.getRetraimiento(), perfil2.getRetraimiento()));
                int punctuationSinceridad = Float.compare(perfil1.getSinceridad(), perfil2.getSinceridad());
                return punctuationLiderago + punctuationAnsiedad + punctuationAutocontrol + punctuationConsideracion + punctuationRetraimiento + punctuationSinceridad;
            });
        }

        return tutorizadosMutable;
    }

    /**
     * Dada una lista de alumnos tutores, ordena la lista en función de la puntuación del perfil de cada alumno
     * @param tutores
     * @return
     */
    private List<Alumno> orderTutoresByPerfil(List<Alumno> tutores) {
        ArrayList<Alumno> tutoresMutable = new ArrayList<>();

        if (tutores.size() > 0){
            tutoresMutable.addAll(tutores);
            tutoresMutable.sort((alumno1, alumno2) -> {
                PerfilProsocial perfil1 = alumno1.getPerfil();
                PerfilProsocial perfil2 = alumno2.getPerfil();
                int punctuationLiderago = Float.compare(perfil1.getLiderazgo(), perfil2.getLiderazgo());
                int punctuationAutocontrol = Float.compare(perfil1.getAutocontrol(), perfil2.getAutocontrol());
                int punctuationConsideracion = Float.compare(perfil1.getConsideracion(), perfil2.getConsideracion());
                int punctuationAnsiedad = -(Float.compare(perfil1.getAnsiedad(), perfil2.getAnsiedad()));
                int punctuationRetraimiento = -(Float.compare(perfil1.getRetraimiento(), perfil2.getRetraimiento()));
                int punctuationSinceridad = Float.compare(perfil1.getSinceridad(), perfil2.getSinceridad());
                return punctuationLiderago + punctuationAnsiedad + punctuationAutocontrol + punctuationConsideracion + punctuationRetraimiento + punctuationSinceridad;
            });
        }

        return tutoresMutable;
    }

    /**
     * Sugiere una asignación de parejas que ordena a los tutores de más prosociales a menos y los tutorizados de menos a más y los empareja uno a uno
     * @return
     */
    @GetMapping("/getCouplesSuggestion")
    public String getCouplesSuggestion() {
        List<Alumno> tutores = dbinterface.getTutorSelectedDetailsFromDb();
        List<Alumno> tutorizados = dbinterface.getTutorizadoSelectedDetailsFromDb();

        List<Alumno> orderedTutores = orderTutoresByPerfil(tutores);
        List<Alumno> orderedTutorizados = orderTutorizadosByPerfil(tutorizados);

        List<Pareja> parejas = new ArrayList<>();

        for (int i = 0; i < orderedTutores.size(); i++) {
            Pareja pareja = new Pareja(orderedTutores.get(i), orderedTutorizados.get(i));
            parejas.add(pareja);
        }

        return (new ObjectToJsonParser()).parseParejas(parejas);
    }

    /**
     * Dado un archivo, lo parsea y devuelve un grupo de perfiles de alumno que será guardado en la base de datos
     * @param excelFile
     * @return
     */
    @PostMapping("/storePollResults")
    public String storePollResults(@Valid @RequestBody MultipartFile excelFile) {
        try {
            List<Object> perfilesObj = (new PollResultsFileParser()).processExcelSheet(excelFile);
            List<Alumno> perfiles = perfilesObj.stream().map((object)->(Alumno)object).toList();

            if (dbinterface.addPerfilesToDB(perfiles))
                return "Los perfiles se han generado y enviado con éxito a la base de datos.";

            return "Ha habido un problema al cargar los perfiles de los alumnos en la base de datos";

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The excel file provided could not be correctly processed");
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Hubo algún problema al insertar los perfiles en la base de datos");
        }
    }

    /**
     * Recibe como parámetro un link a la encuesta y a un archivo excel que contiene los emails de los
     * alumnos a los que se les enviará la encuesta
     * @param pollLink
     * @param excelFile
     * @return
     */
    @PostMapping("/sendEmails")
    public String sendEmails(@RequestParam String pollLink, @Valid @RequestBody MultipartFile excelFile) {
        try {
            List<Object> emailsObj = (new EmailFileParser()).processExcelSheet(excelFile);
            List<String> emails = emailsObj.stream().map((object)->(String)object).toList();

            String decodedPollLink = URLDecoder.decode(pollLink, StandardCharsets.UTF_8);

            return (new EmailManager(javaMailSender, from)).sendEmail(emails, decodedPollLink);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The excel file provided could not be correctly processed");
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There has been an error while distributing the emails");
        }
    }

    /**
     * Borra todos los datos de la base de datos
     * @return
     */
    @GetMapping("/clearData")
    public String clearData() {
        if (dbinterface.clearDatabase())
            return "Todos los datos han sido eliminados con éxito";

        return "Se ha producido un problema al eliminar los datos";
    }
}
