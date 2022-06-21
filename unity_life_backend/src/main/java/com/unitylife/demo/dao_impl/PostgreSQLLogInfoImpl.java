package com.unitylife.demo.dao_impl;

import com.unitylife.demo.dao.LogInfoDao;
import com.unitylife.demo.entity.LogInfo;
import com.unitylife.demo.row_mappers.IntegerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("PostgresLogInfoRepo")
public class PostgreSQLLogInfoImpl implements LogInfoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addLogInfoData(LogInfo logInfo) {
        final String sql = "UPDATE log_info SET userid = ?, email = ?, password = ?, token = ?";
        jdbcTemplate.update(sql,
                logInfo.getUserId(),
                logInfo.getEmail(),
                logInfo.getPassword(),
                logInfo.getToken());
    }

    @Override
    public void removeLogInfoDataByUserId(Integer userId) {
        final String sql = "DELETE FROM log_info WHERE userid = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public Integer getTokenByUserId(int userId) {
        final String sql = "SELECT token FROM log_info WHERE userId = ?";
        Integer token = jdbcTemplate.queryForObject(sql, new IntegerRowMapper(), userId);
        return token;
    }
}
