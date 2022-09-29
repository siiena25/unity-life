package com.unitylife.demo.entity;

import java.util.Objects;

public class UserWithToken {
    private int userid;
    private String firstname;
    private String lastname;
    private String email;
    private int age;
    private String gender;
    private String country;
    private String city;
    private String password;
    private int roleid;
    private String token;

    public UserWithToken(String firstname, String lastname, String email,
                int age, String gender, String country, String city,
                String password, int roleid, String token) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.country = country;
        this.city = city;
        this.password = password;
        this.roleid = 2;
        this.token = token;
    }

    public UserWithToken(User user, String token) {
        this.firstname = user.getFirstName();
        this.lastname = user.getLastName();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.country = user.getCountry();
        this.city = user.getCity();
        this.password = user.getPassword();
        this.roleid = user.getRoleId();
        this.token = token;
    }

    public UserWithToken() {
    }

    public int getId() {
        return userid;
    }

    public void setId(int userid) {
        this.userid = userid;
    }

    public String getFirstName() {
        return firstname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return this.password;
    }

    public int getRoleId() {
        return roleid;
    }

    public void setRoleId(int roleId) {
        this.roleid = roleId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid=" + userid +
                ", firstName='" + firstname + '\'' +
                ", lastName='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWithToken user = (UserWithToken) o;

        if (userid != user.userid) return false;
        if (age != user.age) return false;
        if (!Objects.equals(firstname, user.firstname)) return false;
        if (!Objects.equals(lastname, user.lastname)) return false;
        if (!Objects.equals(email, user.email)) return false;
        if (!Objects.equals(gender, user.gender)) return false;
        if (!Objects.equals(country, user.country)) return false;
        if (!Objects.equals(city, user.city)) return false;
        if (!Objects.equals(token, user.token)) return false;
        return Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        int result = userid;
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

}
