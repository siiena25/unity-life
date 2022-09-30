package com.unitylife.demo.service;

import com.unitylife.demo.dao.LogInfoDao;
import com.unitylife.demo.dao_impl.PostgreSQLLogInfoImpl;
import com.unitylife.demo.entity.LogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LogInfoService {
    PostgreSQLLogInfoImpl postgreSQLLogInfo;

    LogInfoService(PostgreSQLLogInfoImpl postgreSQLLogInfo) {
        this.postgreSQLLogInfo = postgreSQLLogInfo;
    }

    @Autowired
    @Qualifier("PostgresLogInfoRepo")
    private LogInfoDao logInfoDao;

    public void addLogInfoData(LogInfo logInfo) {
        this.logInfoDao.addLogInfoData(logInfo);
    }

    public void removeLogInfoDataByUserId(Integer userId) {
        this.logInfoDao.removeLogInfoDataByUserId(userId);
    }

    public String getTokenByUserId(int userId) {
        return this.logInfoDao.getTokenByUserId(userId);
    }
}
