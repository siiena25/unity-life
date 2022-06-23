package com.unitylife.demo.dao_impl;

import com.unitylife.demo.dao.GroupDao;
import com.unitylife.demo.entity.Group;
import com.unitylife.demo.entity.Membersip;
import com.unitylife.demo.exceptions.AuthenticationException;
import com.unitylife.demo.row_mappers.GroupRowMapper;
import com.unitylife.demo.row_mappers.IntegerRowMapper;
import com.unitylife.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository("PostgresGroupRepo")
public class PostgreSQLGroupDaoImpl implements GroupDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Override
    public Collection<Group> getAllGroup() {
        final String sql = "SELECT * FROM groups";
        List<Group> groups = jdbcTemplate.query(sql, new GroupRowMapper());
        return groups;
    }

    @Override
    public Group getGroupById(int id) {
//    new UserInformation().checkUser(id);
        final String sql = "SELECT * FROM groups WHERE groupid = ?";
        Group group = jdbcTemplate.queryForObject(sql, new GroupRowMapper(), id);
        return group;
    }

    @Override
    public void removeGroupById(int id) {
        final String sql = "DELETE FROM groups WHERE groupid = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void createGroup(Group group) {
        final String sql = "INSERT INTO groups (groupid, groupname, groupadminid) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, group.getGroupID(), group.getName(), group.getAdminId());
    }

    @Override
    public Collection<Group> getGroupByName(String name) {
        final String sql = "SELECT * FROM groups WHERE groupname = ? ";
        List<Group> groups = jdbcTemplate.query(sql, new GroupRowMapper(), name);
        return groups;
    }

    @Override
    public Collection<Group> getGroupByAdminId(int adminId) {
        final String sql = "SELECT * FROM groups WHERE groupadminid = ? ";
        List<Group> groups = jdbcTemplate.query(sql, new GroupRowMapper(), adminId);
        return groups;
    }

    @Override
    public Collection<Group> getAllGroupsForUser(int memberid) {
        // new UserInformation().checkUser(memberid);
        final String sql = "SELECT g.* FROM groups g, membership m " +
                "WHERE g.groupid = m.groupid AND memberid = ? ";
        List<Group> groups = jdbcTemplate.query(sql, new GroupRowMapper(), memberid);
        return groups;
    }

    @Override
    public void addUserToGroup(int groupId, int userId) {
        Membersip membersip = new Membersip(groupId, userId, 1);
        final String sql = "INSERT INTO membership (groupid, userid, status) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql,
                membersip.getGroupId(),
                membersip.getUserId(),
                membersip.getStatus());
    }

    @Override
    public void addUserToGroupByAdmin(int userid, int groupid, int memberid) {
        // change status from 3 to 1
        checkAdminId(userid, groupid);
        String sql = "UPDATE membership SET status = 1 WHERE groupid = ? AND userid = ?";
        jdbcTemplate.update(sql, groupid, memberid);
    }

    @Override
    public Collection<Integer> getAllUserIds(int groupId) {
        final String sql = "SELECT userid FROM membership WHERE groupid = ?";
        List<Integer> userIdsList = jdbcTemplate.query(sql, new IntegerRowMapper("memberid"), groupId);
        return userIdsList;
    }

    public void checkAdminId(int id, int groupid) {
        String sql = "SELECT groupadminid FROM groups WHERE groupid = ?";
        String adminid = jdbcTemplate.queryForObject(sql, new Object[]{groupid}, String.class);
        int adminId = userService.getUserByUserName(adminid).getId();
        if (adminId != id) {
            throw new AuthenticationException(id);
        }
    }

    @Override
    public void sendJoinRequest(int groupid, int userid) {
        String sql = "INSERT INTO membership(groupid, userid, status) SELECT ?,?,? " +
                "WHERE NOT EXISTS (SELECT * FROM membership WHERE (groupid = ? AND userid = ?))";
        jdbcTemplate.update(sql, groupid, userid, 2, groupid, userid);
    }

    @Override
    public void removeUserFromGroup(int userid, int groupid, int memberid) {
        // remove
        checkAdminId(userid, groupid);
        final String sql = "DELETE FROM membership" +
                " WHERE groupid = ? AND userid = ?";
        jdbcTemplate.update(sql, groupid, memberid);
    }
}