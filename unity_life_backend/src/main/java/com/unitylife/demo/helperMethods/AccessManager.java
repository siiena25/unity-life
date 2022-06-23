package com.unitylife.demo.helperMethods;

import com.unitylife.demo.exceptions.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class AccessManager {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public AccessManager() {
    }

    public void checkUser(int id, int token) {
        try {
            String database_token_sql = "SELECT token FROM log_info WHERE userid = ?";
            Integer database_token = jdbcTemplate.queryForObject(database_token_sql, new Object[]{id}, Integer.class);
            String database_id_sql = "SELECT userid FROM log_info WHERE token = ?";
            Integer database_id = jdbcTemplate.queryForObject(database_id_sql, new Object[]{token}, Integer.class);
            if (database_id != id || database_token != token) {
                throw new AuthenticationException(id);
            }
        } catch (EmptyResultDataAccessException except) {
            throw new AuthenticationException((id));
        }
    }
}