package com.unitylife.demo.entity;


import java.util.Objects;

public class Group {
    private int groupId;
    private int groupAdminId;
    private String groupName;

    public Group(int groupId, int groupAdminId, String name) {
        this.groupId = groupId;
        this.groupAdminId = groupAdminId;
        this.groupName = name;
    }

    public Group() {}

    public int getGroupID() {
        return groupId;
    }

    public void setGroupID(int groupid) {
        this.groupId = groupid;
    }

    public int getAdminId() {
        return groupAdminId;
    }

    public void setAdminId(int groupAdminId) {
        this.groupAdminId = groupAdminId;
    }

    public String getName() {
        return groupName;
    }

    public void setName(String name) {
        this.groupName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (groupId != group.groupId) return false;
        if (groupAdminId != group.groupAdminId) return false;
        return Objects.equals(groupName, group.groupName);
    }

    @Override
    public int hashCode() {
        int result = groupId;
        result = 31 * result + Integer.hashCode(groupAdminId);
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupid=" + groupId +
                ", groupadminid=" + groupAdminId +
                ", groupname='" + groupName + '\'' +
                '}';
    }
}