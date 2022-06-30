package com.tfg.restapi.db.preparedStatement;

import com.tfg.restapi.model.Alumno;
import com.tfg.restapi.model.PerfilProsocial;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PerfilBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    private final List<Alumno> alumnos;

    public PerfilBatchPreparedStatementSetter(List<Alumno> alumnos) {
        super();
        this.alumnos = alumnos;
    }
    @Override
    public void setValues(PreparedStatement ps, int i) {

        try {
            Alumno tutor = alumnos.get(i);
            PerfilProsocial perfil = tutor.getPerfil();

            ps.setString(1, perfil.getId());
            ps.setFloat(2, perfil.getSinceridad());
            ps.setFloat(3, perfil.getLiderazgo());
            ps.setFloat(4, perfil.getAutocontrol());
            ps.setFloat(5, perfil.getConsideracion());
            ps.setFloat(6, perfil.getRetraimiento());
            ps.setFloat(7, perfil.getAnsiedad());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public int getBatchSize() {
        return alumnos.size();
    }
}
