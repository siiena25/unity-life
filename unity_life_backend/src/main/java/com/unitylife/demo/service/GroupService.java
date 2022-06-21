package com.unitylife.demo.service;

import com.unitylife.demo.dao.GroupDao;
import com.unitylife.demo.dao_impl.PostgreSQLGroupDaoImpl;
import com.unitylife.demo.entity.Group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
public class GroupService {
    PostgreSQLGroupDaoImpl postgreSQLGroupDao;

    GroupService(PostgreSQLGroupDaoImpl postgreSQLGroupDao) {
        this.postgreSQLGroupDao = postgreSQLGroupDao;
    }

    @Autowired
    @Qualifier("PostgresGroupRepo")
    private GroupDao groupDao;

    public Collection<Group> getAllGroup() {
        return this.groupDao.getAllGroup();
    }

    public Group getGroupById(int id) {
        return this.groupDao.getGroupById(id);
    }

    public void removeGroupById(int id) {
        this.groupDao.removeGroupById(id);
    }

    public void createGroup(Group group) {
        this.groupDao.createGroup(group);
    }

    public Collection<Group> getGroupByName(String name) {
        return this.groupDao.getGroupByName(name);
    }

    public Collection<Group> getGroupByAdmin(int adminId) {
        return this.groupDao.getGroupByAdminId(adminId);
    }

    public Collection<Group> getAllGroupsForUser(int memberid) {
        return this.groupDao.getAllGroupsForUser(memberid);
    }

    public void sendJoinRequest(int groupid, int memberid) {
        this.groupDao.sendJoinRequest(groupid, memberid);
    }

    public void addUserToGroup(int groupId, int userId) {
        this.groupDao.addUserToGroup(groupId, userId);
    }

    public void addMemberToGroupByAdmin(int userid, int groupid, int memberid) {
        this.groupDao.addUserToGroupByAdmin(userid, groupid, memberid);
    }

    public Collection<Integer> getAllMemberIds(int groupId) {
        return this.groupDao.getAllUserIds(groupId);
    }

    public void removeMemberFromGroup(int userid, int groupid, int memberid) {
        this.groupDao.removeUserFromGroup(userid, groupid, memberid);
    }
}
