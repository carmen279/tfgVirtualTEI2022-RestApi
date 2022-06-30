package com.tfg.restapi.db.preparedStatement;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SelectedAlumnoBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    private final List<String> alumnos;

    public SelectedAlumnoBatchPreparedStatementSetter(List<String> alumnos) {
        super();
        this.alumnos = alumnos;
    }
    @Override
    public void setValues(PreparedStatement ps, int i) {

        try {
            String tutor = alumnos.get(i);
            ps.setString(1, tutor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public int getBatchSize() {
        return alumnos.size();
    }
}
