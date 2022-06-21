package com.unitylife.demo.dao;

import com.unitylife.demo.entity.Group;


import java.util.Collection;

public interface GroupDao {
    Collection<Group> getAllGroup();

    Group getGroupById(int id);

    void removeGroupById(int id);

    void createGroup(Group group);

    Collection<Group> getGroupByName(String name);

    Collection<Group> getGroupByAdminId(int adminId);

    Collection<Group> getAllGroupsForUser(int memberid);

    Collection<Integer> getAllUserIds(int groupId);

    void sendJoinRequest(int groupid, int memberid);

    void addUserToGroup(int groupId, int userId);

    void addUserToGroupByAdmin(int userid, int groupid, int memberid);

    void removeUserFromGroup(int userid, int groupid, int memberid);
}