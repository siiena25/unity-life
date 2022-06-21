package com.unitylife.demo.row_mappers;

import com.unitylife.demo.entity.Group;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupRowMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(ResultSet resultSet, int i) throws SQLException {
        Group group = new Group();
        group.setGroupID(resultSet.getInt("groupid"));
        group.setName(resultSet.getString("groupname"));
        group.setAdminId(resultSet.getInt("groupadminid"));
        return group;
    }
}