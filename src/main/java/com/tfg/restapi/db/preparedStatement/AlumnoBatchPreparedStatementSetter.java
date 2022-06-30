package com.tfg.restapi.db.preparedStatement;

import com.tfg.restapi.model.Alumno;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AlumnoBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    private final List<Alumno> alumnos;

    public AlumnoBatchPreparedStatementSetter(List<Alumno> alumnos) {
        super();
        this.alumnos = alumnos;
    }
    @Override
    public void setValues(PreparedStatement ps, int i) {

        try {
            Alumno tutor = alumnos.get(i);
            ps.setString(1, tutor.getEmail());
            ps.setString(2, tutor.getDni());
            ps.setString(3, tutor.getNombrePila());
            ps.setString(4, tutor.getCurso());
            ps.setString(5, tutor.getUnidad());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public int getBatchSize() {
        return alumnos.size();
    }
}
