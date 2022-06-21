package com.unitylife.demo.service;

import com.unitylife.demo.dao.FriendsDao;
import com.unitylife.demo.dao_impl.PostgreSQLFriendsDaoImpl;
import com.unitylife.demo.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import java.sql.SQLException;
import java.util.Collection;


@org.springframework.stereotype.Service
public class FriendsService {
    PostgreSQLFriendsDaoImpl postgreSQLFriendsDao;

    FriendsService(PostgreSQLFriendsDaoImpl postgreSQLFriendsDao) {
        this.postgreSQLFriendsDao = postgreSQLFriendsDao;
    }

    @Autowired
    @Qualifier("PostgreFriends")
    private FriendsDao dao;

    public Collection<User> getAllFriends(int id) {
        return this.dao.getAllFriends(id);
    }

    public void removeAllFriends(int id) {
        System.out.println("IN SERVICE IN SERVICE IN SERVICE IN SERVICEIN SERVICEIN SERVICEIN SERVICEIN SERVICEIN SERVICEIN SERVICEIN SERVICE");
        this.dao.removeAllFriends(id);
    }

    public void unFriend(int id, String username) {
        try {
            this.dao.unFriend(id, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countFriends(int id) {
        return this.dao.countFriends(id);
    }

    public void sendRequest(int id, String username) {
        try {
            this.dao.sendRequest(id, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void becomeFriend(int id, String username) {
        this.dao.becomeFriend(id, username);
    }

    public void blockFriend(int id, String username) {
        this.dao.blockFriend(id, username);
    }

    public Collection<User> commonFriends(int id, String username) {
        return this.dao.commonFriends(id, username);
    }

    public Collection<User> getFriendsByName(String username, int id) {
        return this.dao.getFriendsByName(username, id);
    }

    public Collection<User> getInvitationList(int id) {
        return this.dao.getInvitationList(id);
    }

    public Collection<User> getBlockList(int id) {
        return this.dao.getBlockList(id);
    }
}