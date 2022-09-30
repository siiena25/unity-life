package com.unitylife.demo.dao_impl;

import com.unitylife.demo.dao.LogInfoDao;
import com.unitylife.demo.entity.LogInfo;
import com.unitylife.demo.entity.User;
import com.unitylife.demo.row_mappers.IntegerRowMapper;
import com.unitylife.demo.row_mappers.StringRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("PostgresLogInfoRepo")
public class PostgreSQLLogInfoImpl implements LogInfoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addLogInfoData(LogInfo logInfo) {
        removeLogInfoDataByUserId(logInfo.getUserId());
        final String sql = "INSERT INTO log_info (userid, email, password, token) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                logInfo.getUserId(),
                logInfo.getEmail(),
                logInfo.getPassword(),
                Integer.toString(logInfo.getToken()));
    }

    @Override
    public void removeLogInfoDataByUserId(Integer userId) {
        final String sql = "DELETE FROM log_info WHERE userid = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public String getTokenByUserId(int userId) {
        final String sql = "SELECT token FROM log_info WHERE userid = ?";
        System.out.println("LOG:: LogInfoImpl " + sql);
        String token = jdbcTemplate.queryForObject(sql, new StringRowMapper("token"), userId);
        System.out.println("LOG:: LogInfoImpl " + token);
        return token;
    }
}
