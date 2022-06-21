package com.unitylife.demo.dao;

import com.unitylife.demo.entity.User;

import java.util.Collection;

public interface UserDao {
    Collection<User> getAllUser();

    User getUserById(int id);

    void removeUserById(int id);

    void updateUser(User user);

    void insertUserToDb(User user);

    Collection<User> getUsersByFirstName(String name);

    Collection<User> getUsersByLastName(String name);

    // As in "First Last" nomenclature
    User getUsersByFullName(String name);

    // As in "first.last" nomenclature
    User getUserByUserName(String name);

    User getUserByEmail(String email);

    Collection<User> getUsersByAge(int age);

    Collection<User> getUsersByGender(String gender);

    Collection<User> getUsersByCountry(String country);

    Collection<User> getUserByCity(String city);

    void setFirstName(int userId, String first);

    void setLastName(int userID, String last);

    void setEmail(int userID, String email);

    void setAge(int userID, int age);

    void setGender(int userID, String gender);

    void setCountry(int userID, String country);

    void setCity(int userID, String city);

    void setPassword(int userID, String password);
}