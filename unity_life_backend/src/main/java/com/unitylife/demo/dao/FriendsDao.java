
package com.unitylife.demo.dao;

import com.unitylife.demo.entity.User;

import java.sql.SQLException;
import java.util.Collection;

public interface FriendsDao {
    Collection<User> getAllFriends(int id);

    Collection<User> commonFriends(int id, String username);

    Collection<User> getFriendsByName(String username, int id);

    Collection<User> getInvitationList(int id);

    Collection<User> getBlockList(int id);

    void removeAllFriends(int id);

    void unFriend(int id, String username) throws SQLException;

    int countFriends(int id);

    void sendRequest(int id, String username) throws SQLException;

    void becomeFriend(int id, String username);

    void blockFriend(int id, String username);
}