package com.unitylife.demo.helperMethods;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class UserInformation {
    public String[] splitUserNameWithDot(String name) {
        return name.split("\\.");
    }

    public String[] splitUserNameWithoutDot(String name) {
        return name.split(" ");
    }

    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public String getHeaders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getDetails().toString();
    }

}
