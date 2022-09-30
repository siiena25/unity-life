package com.unitylife.demo.row_mappers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringRowMapper implements RowMapper<String> {

    private String columnLabel = "";

    public StringRowMapper(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    @Override
    public String mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getString(columnLabel);
    }
}
