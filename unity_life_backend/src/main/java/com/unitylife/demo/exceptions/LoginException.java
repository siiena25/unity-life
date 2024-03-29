package com.unitylife.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Incorrect credentials!")
public class LoginException extends RuntimeException {
    public LoginException() {
        super("Incorrect login or password");
    }
}
