package com.unitylife.demo.entity;

public class Membersip {
    private int groupId;
    private int userId;
    private int memberId;
    private int status; //default = 1

    public Membersip(int groupId, int userId, int status) {
        this.groupId = groupId;
        this.userId = userId;
        this.status = status;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserid(int userId) {
        this.userId = userId;
    }
}
