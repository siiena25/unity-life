package com.unitylife.demo.entity;

import java.util.Objects;

public class LogInfo {
    private int userId;
    private String email;
    private int token;
    private String password;

    public LogInfo(int userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        createToken(email);
    }

    private void createToken(String email) {
        this.token = email.hashCode();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "userid=" + userId + '\'' +
                "email=" + email + '\'' +
                "token=" + token + '\'' +
                "password=" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null | getClass() != obj.getClass()) return false;

        LogInfo logInfo = (LogInfo) obj;

        if (userId != logInfo.userId) return false;
        if (!Objects.equals(email, logInfo.email)) return false;
        return Objects.equals(password, logInfo.password);
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (email.hashCode());
        result = 31 * result + (password.hashCode());
        return result;
    }
}
