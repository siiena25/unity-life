package com.unitylife.demo.service;

import com.unitylife.demo.helperMethods.UserInformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationService {
    @Autowired
    private UserService userService;

    public String getEmail() {
        return new UserInformation().getEmail();
    }

    public String getToken() {
        int token = new UserInformation().getEmail().hashCode();
        return Integer.toString(token);
    }
}