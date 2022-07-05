package com.tfg.restapi.db.preparedStatement;

import com.tfg.restapi.model.Pareja;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CouplesBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    private final List<Pareja> parejas;

    public CouplesBatchPreparedStatementSetter(List<Pareja> parejas) {
        super();
        this.parejas = parejas;
    }

    /**
     * Dado un prepared statement, asigna los valores de la petición de este
     * @param ps
     * @param i
     */
    @Override
    public void setValues(PreparedStatement ps, int i) {

        try {
            Pareja pareja = parejas.get(i);
            ps.setString(1, pareja.getTutor().getEmail());
            ps.setString(2, pareja.getTutorizado().getEmail());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public int getBatchSize() {
        return parejas.size();
    }
}
