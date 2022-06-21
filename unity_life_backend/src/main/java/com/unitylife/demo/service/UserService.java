package com.unitylife.demo.service;

import com.unitylife.demo.dao_impl.PostgreSQLUserDaoImpl;
import com.unitylife.demo.dao.UserDao;
import com.unitylife.demo.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
public class UserService {
    PostgreSQLUserDaoImpl postgreSQLUserDao;

    UserService(PostgreSQLUserDaoImpl postgreSQLUserDao) {
        this.postgreSQLUserDao = postgreSQLUserDao;
    }

    @Autowired
    @Qualifier("PostgresUserRepo")
    private UserDao userDao;

    public Collection<User> getAllUsers() {
        return this.userDao.getAllUser();
    }

    public User getUserById(int userid) {
        return this.userDao.getUserById(userid);
    }

    public void removeUserById(int id) {
        this.userDao.removeUserById(id);
    }

    public void updateUser(User user) {
        this.userDao.updateUser(user);
    }

    public Collection<User> getUsersByFirstName(String name) {
        return this.userDao.getUsersByFirstName(name);
    }

    public Collection<User> getUsersByLastName(String name) {
        return this.userDao.getUsersByLastName(name);
    }

    // As in "First Last" nomenclature
    public User getUsersByFullName(String name) {
        return this.userDao.getUsersByFullName(name);
    }

    // As in "first.last" nomenclature
    public User getUserByUserName(String name) {
        return this.userDao.getUserByUserName(name);
    }

    public User getUserByEmail(String email) {
        return this.userDao.getUserByEmail(email);
    }

    public Collection<User> getUsersByAge(int age) {
        return this.userDao.getUsersByAge(age);
    }

    public Collection<User> getUsersByGender(String gender) {
        return this.userDao.getUsersByGender(gender);
    }

    public Collection<User> getUsersByCountry(String country) {
        return this.userDao.getUsersByCountry(country);
    }

    public Collection<User> getUsersByCity(String city) {
        return this.userDao.getUserByCity(city);
    }

    public void register(User user) {
        this.userDao.insertUserToDb(user);
    }

    public void setFirst(int userid, String first) {
        this.userDao.setFirstName(userid, first);
    }

    public void setLast(int userid, String last) {
        this.userDao.setLastName(userid, last);
    }

    public void setEmail(int userid, String email) {this.userDao.setEmail(userid, email);}

    public void setAge(int userid, int age) {
        this.userDao.setAge(userid, age);
    }

    public void setGender(int userid, String gender) {
        this.userDao.setGender(userid, gender);
    }

    public void setCountry(int userid, String country) {
        this.userDao.setCountry(userid, country);
    }

    public void setCity(int userid, String city) {
        this.userDao.setCity(userid, city);
    }

    public void setPassword(int userid, String password) {
        this.userDao.setPassword(userid, password);
    }
}