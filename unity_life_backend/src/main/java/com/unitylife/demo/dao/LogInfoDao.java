package com.unitylife.demo.dao;

import com.unitylife.demo.entity.LogInfo;

public interface LogInfoDao {
    void addLogInfoData(LogInfo logInfo);

    void removeLogInfoDataByUserId(Integer userId);

    String getTokenByUserId(int userId);
}
