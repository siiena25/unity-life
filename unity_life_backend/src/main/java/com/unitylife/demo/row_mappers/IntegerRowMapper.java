package com.unitylife.demo.row_mappers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerRowMapper implements RowMapper<Integer> {

    private String columnLabel = "";

    public IntegerRowMapper(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    @Override
    public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getInt(columnLabel);
    }
}
