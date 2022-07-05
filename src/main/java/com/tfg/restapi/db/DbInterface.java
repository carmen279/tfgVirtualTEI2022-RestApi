package com.tfg.restapi.db;

import com.tfg.restapi.db.preparedStatement.AlumnoBatchPreparedStatementSetter;
import com.tfg.restapi.db.preparedStatement.CouplesBatchPreparedStatementSetter;
import com.tfg.restapi.db.preparedStatement.PerfilBatchPreparedStatementSetter;
import com.tfg.restapi.db.preparedStatement.SelectedAlumnoBatchPreparedStatementSetter;
import com.tfg.restapi.model.Alumno;
import com.tfg.restapi.model.Pareja;
import com.tfg.restapi.model.PerfilProsocial;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DbInterface {

    JdbcTemplate template;

    public DbInterface(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Dada una lista de tutores, los inserta en la BD en la tabla de "CandidatoTutor"
     * @param tutores
     * @return
     */
    public boolean addTutoresToDB(List<Alumno> tutores) {
        String removeParejas = "DELETE FROM PUBLIC.\"Pareja\"";
        template.update(removeParejas);
        String removeSelectedTutor = "DELETE FROM PUBLIC.\"SeleccionadoTutor\"";
        template.update(removeSelectedTutor);
        String removeSelectedTutorizado = "DELETE FROM PUBLIC.\"SeleccionadoTutorizado\"";
        template.update(removeSelectedTutorizado);
        String removeCandidateTutorPerfil = "DELETE FROM PUBLIC.\"CandidatoTutorPerfilProsocial\"";
        template.update(removeCandidateTutorPerfil);
        String removeCandidateTutor = "DELETE FROM PUBLIC.\"CandidatoTutor\"";
        template.update(removeCandidateTutor);
        String query="INSERT INTO  PUBLIC.\"CandidatoTutor\" (\"email\", \"dni\", \"nombrePila\", \"curso\", \"unidad\") " +
                "VALUES (?,?,?,?,?)";
        int[] rows = template.batchUpdate(query, new AlumnoBatchPreparedStatementSetter(tutores) );

        return Arrays.stream(rows).reduce(0, Integer::sum) > 0;
    }

    /**
     * Dada una lista de tutorizados, los inserta en la BD en la tabla de "CandidatoTutorizado"
     * @param tutorizados
     * @return
     */
    public boolean addTutorizadosToDB(List<Alumno> tutorizados) {
        String removeParejas = "DELETE FROM PUBLIC.\"Pareja\"";
        template.update(removeParejas);
        String removeSelectedTutor = "DELETE FROM PUBLIC.\"SeleccionadoTutor\"";
        template.update(removeSelectedTutor);
        String removeSelectedTutorizado = "DELETE FROM PUBLIC.\"SeleccionadoTutorizado\"";
        template.update(removeSelectedTutorizado);
        String removeCandidateTutorizadoPerfil = "DELETE FROM PUBLIC.\"CandidatoTutorizadoPerfilProsocial\"";
        template.update(removeCandidateTutorizadoPerfil);
        String removeCandidateTutorizado = "DELETE FROM PUBLIC.\"CandidatoTutorizado\"";
        template.update(removeCandidateTutorizado);
        String query="INSERT INTO  PUBLIC.\"CandidatoTutorizado\" (\"email\", \"dni\", \"nombrePila\", \"curso\", \"unidad\") " +
                "VALUES (?,?,?,?,?)";
        int[] rows = template.batchUpdate(query, new AlumnoBatchPreparedStatementSetter(tutorizados) );

        return Arrays.stream(rows).reduce(0, Integer::sum) > 0;
    }

    /**
     * Dada una lista de perfiles de alumno, los inserta en la BD en la tabla "PerfilProsocial"
     * @param perfiles
     * @return
     * @throws SQLException
     */
    public boolean addPerfilesToDB(List<Alumno> perfiles) throws SQLException {
        String removeParejas = "DELETE FROM PUBLIC.\"Pareja\"";
        template.update(removeParejas);
        String removeSelectedTutor = "DELETE FROM PUBLIC.\"SeleccionadoTutor\"";
        template.update(removeSelectedTutor);
        String removeSelectedTutorizado = "DELETE FROM PUBLIC.\"SeleccionadoTutorizado\"";
        template.update(removeSelectedTutorizado);
        String removeCandidateTutorPerfil = "DELETE FROM PUBLIC.\"CandidatoTutorPerfilProsocial\"";
        template.update(removeCandidateTutorPerfil);
        String removeCandidateTutorizadoPerfil = "DELETE FROM PUBLIC.\"CandidatoTutorizadoPerfilProsocial\"";
        template.update(removeCandidateTutorizadoPerfil);
        String removePerfiles = "DELETE FROM PUBLIC.\"PerfilProsocial\"";
        template.update(removePerfiles);
        //Añadir perfil bd
        String query="INSERT INTO  PUBLIC.\"PerfilProsocial\" (\"id\", \"sinceridad\", \"liderazgo\", \"autocontrol\", \"consideracion\", \"retraimiento\", \"ansiedad\") " +
                "VALUES (?,?,?,?,?,?,?)";
        int[] rows = template.batchUpdate(query, new PerfilBatchPreparedStatementSetter(perfiles) );

        if (Arrays.stream(rows).reduce(0, Integer::sum) > 0) {

            for (Alumno alumno : perfiles) {
                addPerfilAlumnoRelation(alumno);
            }

            return true;
        }
        return false;
    }

    /**
     * Dado un alumno, comprueba si es tutor o tutorizado y lo añade en la tabla correspondiente
     * @param alumno
     */
    private void addPerfilAlumnoRelation(Alumno alumno) {
        //Buscar email en tutores
        if (checkIfTutor(alumno)) {
            //Si se encuentra añadir a tabla candidatotutorperfil
            addPerfilTutorRelation(alumno);
        } else {
            if (checkIfTutorizado(alumno)) {
                //Si se encuentra añadir a tabla candidatotutorizadoperfil
                addPerfilTutorizadoRelation(alumno);
            }
        }
    }

    /**
     * Añade el alumno en la tabla "CandidatoTutorPerfilProsocial"
     * @param alumno
     */
    private void addPerfilTutorRelation(Alumno alumno) {
        String queryInsertPerfilTutor= "INSERT INTO  PUBLIC.\"CandidatoTutorPerfilProsocial\" (\"candidatoTutor\", \"perfilProsocial\") " +
                "VALUES (?,?)";
        int row = template.update(queryInsertPerfilTutor, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, alumno.getEmail());
                ps.setString(2, alumno.getPerfil().getId());
            }
        });
    }

    /**
     * Añade el alumno a la tabla "CandidatoTutorizadoPerfilProsocial"
     * @param alumno
     */
    private void addPerfilTutorizadoRelation(Alumno alumno) {
        String queryInsertPerfilTutorizado= "INSERT INTO  PUBLIC.\"CandidatoTutorizadoPerfilProsocial\" (\"candidatoTutorizado\", \"perfilProsocial\") " +
                "VALUES (?,?)";
        int row = template.update(queryInsertPerfilTutorizado, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, alumno.getEmail());
                ps.setString(2, alumno.getPerfil().getId());
            }
        });
    }

    /**
     * Comprueba que el alumno se encuentra en la tabla de "CandidatoTutor"
     * @param alumno
     * @return
     */
    private boolean checkIfTutor(Alumno alumno) {
        String checkIfTutor = "SELECT * FROM PUBLIC.\"CandidatoTutor\" WHERE \"email\" = ?";

        return template.execute(checkIfTutor, (PreparedStatementCallback<Boolean>) ps -> {
            ps.setString(1, alumno.getEmail());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        });
    }

    /**
     * Comprueba que el alumno se encuentra en la tabla de "CandidatoTutorizado"
     * @param alumno
     * @return
     */
    private boolean checkIfTutorizado(Alumno alumno) {
        String checkIfTutorizado = "SELECT * FROM PUBLIC.\"CandidatoTutorizado\" WHERE \"email\" = ?";

        return template.execute(checkIfTutorizado, (PreparedStatementCallback<Boolean>) ps -> {
            ps.setString(1, alumno.getEmail());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        });
    }

    /**
     * Devuelve los datos de todos los candidatos a tutores de la BD
     * @return
     */
    public List<Alumno> getTutorCandidatesDetailsFromDb() {
        String getTutoresDetails = "SELECT c.*,p.* FROM PUBLIC.\"CandidatoTutor\" AS c, PUBLIC.\"CandidatoTutorPerfilProsocial\" AS cp, PUBLIC.\"PerfilProsocial\" AS p WHERE c.\"email\" = cp.\"candidatoTutor\" AND cp.\"perfilProsocial\" = p.\"id\" ";
        return getAlumnosListFromQuery(getTutoresDetails);
    }

    /**
     * Devuelve los datos de todos los candidatos a tutorizados de la BD
     * @return
     */
    public List<Alumno> getTutorizadoCandidatesDetailsFromDb() {
        String getTutorizadosDetails = "SELECT c.*,p.* FROM PUBLIC.\"CandidatoTutorizado\" AS c, PUBLIC.\"CandidatoTutorizadoPerfilProsocial\" AS cp, PUBLIC.\"PerfilProsocial\" AS p WHERE c.\"email\" = cp.\"candidatoTutorizado\" AND cp.\"perfilProsocial\" = p.\"id\" ";
        return getAlumnosListFromQuery(getTutorizadosDetails);
    }


    /**
     * Devuelve los datos de todos los candidatos a tutores seleccionados de la BD
     * @return
     */
    public List<Alumno> getTutorSelectedDetailsFromDb() {
        String getTutoresDetails = "SELECT c.*, p.* FROM PUBLIC.\"CandidatoTutor\" AS c, PUBLIC.\"SeleccionadoTutor\" AS s, " +
                "PUBLIC.\"CandidatoTutorPerfilProsocial\" AS cp, PUBLIC.\"PerfilProsocial\" AS p " +
                "WHERE c.\"email\" = s.\"emailAlumno\" AND c.\"email\" = cp.\"candidatoTutor\" " +
                "AND cp.\"perfilProsocial\" = p.\"id\"";
        return getAlumnosListFromQuery(getTutoresDetails);
    }


    /**
     * Devuelve los datos de todos los candidatos a tutorizados seleccionados de la BD
     * @return
     */
    public List<Alumno> getTutorizadoSelectedDetailsFromDb() {
        String getTutorizadosDetails = "SELECT c.*, p.* FROM PUBLIC.\"CandidatoTutorizado\" AS c, PUBLIC.\"SeleccionadoTutorizado\" AS s, " +
                "PUBLIC.\"CandidatoTutorizadoPerfilProsocial\" AS cp, PUBLIC.\"PerfilProsocial\" AS p " +
                "WHERE c.\"email\" = s.\"emailAlumno\" AND c.\"email\" = cp.\"candidatoTutorizado\" " +
                "AND cp.\"perfilProsocial\" = p.\"id\"";
        return getAlumnosListFromQuery(getTutorizadosDetails);
    }

    /**
     * Devuelve una lista de alumnos con los detalles y datos de la BD
     * @param getTutoresDetails
     * @return
     */
    private List<Alumno> getAlumnosListFromQuery(String getTutoresDetails) {
        List<Map<String, Object>> rows = template.queryForList(getTutoresDetails);
        List<Alumno> alumnos = new ArrayList<>();
        for(Map<String, Object> row : rows){
            Alumno alumno = new Alumno(row.get("email").toString(), row.get("dni").toString(),
                    row.get("nombrePila").toString(), row.get("curso").toString(), row.get("unidad").toString());
            alumno.setPerfil(new PerfilProsocial(row.get("id").toString(), Float.parseFloat(row.get("sinceridad").toString()),
                    Float.parseFloat(row.get("liderazgo").toString()), Float.parseFloat(row.get("consideracion").toString()),
                    Float.parseFloat(row.get("autocontrol").toString()), Float.parseFloat(row.get("retraimiento").toString()),
                    Float.parseFloat(row.get("ansiedad").toString())));
            alumnos.add(alumno);
        }
        return alumnos;
    }

    /**
     * Dadas dos listas de emails de alumnos tutores y tutorizados, las añade en la base de datos en la tabla "SeleccionadoTutor" y "SeleccionadoTutorizado"
     * @param tutores
     * @param tutorizados
     * @return
     */
    public boolean addSelectedToDB(List<String> tutores, List<String> tutorizados) {
        String removeParejas = "DELETE FROM PUBLIC.\"Pareja\"";
        template.update(removeParejas);
        String removeSelectedTutor = "DELETE FROM PUBLIC.\"SeleccionadoTutor\"";
        template.update(removeSelectedTutor);
        String removeSelectedTutorizado = "DELETE FROM PUBLIC.\"SeleccionadoTutorizado\"";
        template.update(removeSelectedTutorizado);
        String queryInsertSelectedTutor= "INSERT INTO  PUBLIC.\"SeleccionadoTutor\" (\"emailAlumno\") " +
                "VALUES (?)";
        String queryInsertSelectedTutorizado= "INSERT INTO  PUBLIC.\"SeleccionadoTutorizado\" (\"emailAlumno\") " +
                "VALUES (?)";

        int[] rowsTutores = template.batchUpdate(queryInsertSelectedTutor, new SelectedAlumnoBatchPreparedStatementSetter(tutores) );
        int[] rowsTutorizados = template.batchUpdate(queryInsertSelectedTutorizado, new SelectedAlumnoBatchPreparedStatementSetter(tutorizados) );

        return Arrays.stream(rowsTutores).reduce(0, Integer::sum) > 0
                && Arrays.stream(rowsTutorizados).reduce(0, Integer::sum) > 0;
    }

    /**
     * Dada una lista de parejas de alumnos, añade las parejas a la base de datos
     * @param parejas
     * @return
     */
    public boolean addCouplesToDB(List<Pareja> parejas) {
        String removeParejas = "DELETE FROM PUBLIC.\"Pareja\"";
        template.update(removeParejas);
        String queryInsertCouples= "INSERT INTO  PUBLIC.\"Pareja\" (\"tutor\",\"tutorizado\") " +
                "VALUES (?,?)";

        int[] rowsCouples = template.batchUpdate(queryInsertCouples, new CouplesBatchPreparedStatementSetter(parejas) );

        return Arrays.stream(rowsCouples).reduce(0, Integer::sum) > 0;
    }

    /**
     * Devuelve una lista de parejas con todos sus atributos de la BD
     * @return
     */
    public List<Pareja> getParejasFromDb() {
        String getParejas = """
                SELECT ct."email" AS emailTutor,ct."dni" AS dniTutor,
                ct."nombrePila" AS nombrePilaTutor, ct."curso" AS cursoTutor,
                ct."unidad" AS unidadTutor, pp."id" AS idPerfilTutor,\s
                pp."sinceridad" AS sinceridadTutor, pp."liderazgo" AS liderazgoTutor,
                pp."consideracion" AS consideracionTutor, pp."autocontrol" AS autocontrolTutor,
                pp."ansiedad" AS ansiedadTutor, pp."retraimiento" AS retraimientoTutor,
                ct2."email" AS emailTutorizado, ct2."dni" AS dniTutorizado,
                ct2."nombrePila" AS nombrePilaTutorizado, ct2."curso" AS cursoTutorizado,
                ct2."unidad" AS unidadTutorizado, pp2."id" AS idPerfilTutorizado,
                pp2."sinceridad" AS "sinceridadTutorizado", pp2."liderazgo" AS "liderazgoTutorizado",
                pp2."consideracion" AS "consideracionTutorizado", pp2."autocontrol" AS "autocontrolTutorizado",
                pp2."ansiedad" AS "ansiedadTutorizado", pp2."retraimiento" AS "retraimientoTutorizado"
                FROM PUBLIC."Pareja" AS p,PUBLIC."CandidatoTutor" AS ct, PUBLIC."CandidatoTutorizado" AS ct2,PUBLIC."PerfilProsocial" AS pp,PUBLIC."PerfilProsocial" AS pp2, PUBLIC."CandidatoTutorPerfilProsocial" AS ctp,PUBLIC."CandidatoTutorizadoPerfilProsocial" AS ctp2
                WHERE p."tutor" = ct."email" AND p."tutorizado" = ct2."email" AND ct."email" = ctp."candidatoTutor" AND ct2."email" = ctp2."candidatoTutorizado" AND ctp."perfilProsocial" = pp."id" AND ctp2."perfilProsocial" = pp2."id";
                """;

        return getParejasListFromQuery(getParejas);
    }

    /**
     * Dada una query, llama a la base de datos y parsea los resultados, devolviendo una lista de parejas
     * @param getParejas
     * @return
     */
    private List<Pareja> getParejasListFromQuery(String getParejas) {
        List<Map<String, Object>> rows = template.queryForList(getParejas);
        List<Pareja> parejas = new ArrayList<>();
        for(Map<String, Object> row : rows){
            Alumno tutor = new Alumno(row.get("emailTutor").toString(), row.get("dniTutor").toString(),
                    row.get("nombrePilaTutor").toString(), row.get("cursoTutor").toString(), row.get("unidadTutor").toString());
            tutor.setPerfil(new PerfilProsocial(row.get("idPerfilTutor").toString(), Float.parseFloat(row.get("sinceridadTutor").toString()),
                    Float.parseFloat(row.get("liderazgoTutor").toString()), Float.parseFloat(row.get("consideracionTutor").toString()),
                    Float.parseFloat(row.get("autocontrolTutor").toString()), Float.parseFloat(row.get("retraimientoTutor").toString()),
                    Float.parseFloat(row.get("ansiedadTutor").toString())));
            Alumno tutorizado = new Alumno(row.get("emailTutorizado").toString(), row.get("dniTutorizado").toString(),
                    row.get("nombrePilaTutorizado").toString(), row.get("cursoTutorizado").toString(), row.get("unidadTutorizado").toString());
            tutorizado.setPerfil(new PerfilProsocial(row.get("idPerfilTutorizado").toString(), Float.parseFloat(row.get("sinceridadTutorizado").toString()),
                    Float.parseFloat(row.get("liderazgoTutorizado").toString()), Float.parseFloat(row.get("consideracionTutorizado").toString()),
                    Float.parseFloat(row.get("autocontrolTutorizado").toString()), Float.parseFloat(row.get("retraimientoTutorizado").toString()),
                    Float.parseFloat(row.get("ansiedadTutorizado").toString())));

            parejas.add(new Pareja(tutor,tutorizado));
        }
        return parejas;
    }

    /**
     * Limpia todas las tablas de la BD
     * @return
     */
    public boolean clearDatabase() {
        String removeParejas = "DELETE FROM PUBLIC.\"Pareja\"";
        int rowsParejas = template.update(removeParejas);
        String removeSelectedTutor = "DELETE FROM PUBLIC.\"SeleccionadoTutor\"";
        int rowsSelectedTutor = template.update(removeSelectedTutor);
        String removeSelectedTutorizado = "DELETE FROM PUBLIC.\"SeleccionadoTutorizado\"";
        int rowsSelectedTutorizado = template.update(removeSelectedTutorizado);
        String removePerfilTutor = "DELETE FROM PUBLIC.\"CandidatoTutorPerfilProsocial\"";
        int rowsPerfilTutor = template.update(removePerfilTutor);
        String removePerfilTutorizado = "DELETE FROM PUBLIC.\"CandidatoTutorizadoPerfilProsocial\"";
        int rowsPerfilTutorizado = template.update(removePerfilTutorizado);
        String removePerfiles = "DELETE FROM PUBLIC.\"PerfilProsocial\"";
        int rowsPerfiles = template.update(removePerfiles);
        String removeCandidatosTutor = "DELETE FROM PUBLIC.\"CandidatoTutor\"";
        int rowsCandidatosTutor = template.update(removeCandidatosTutor);
        String removeCandidatosTutorizado = "DELETE FROM PUBLIC.\"CandidatoTutorizado\"";
        int rowsCandidatosTutorizado = template.update(removeCandidatosTutorizado);


        return rowsParejas + rowsSelectedTutor + rowsSelectedTutorizado + rowsPerfilTutor
                + rowsPerfilTutorizado + rowsPerfiles + rowsCandidatosTutor
                + rowsCandidatosTutorizado > 0;
    }
}
