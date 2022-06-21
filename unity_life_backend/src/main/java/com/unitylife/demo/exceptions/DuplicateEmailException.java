package com.unitylife.demo.exceptions;

import com.unitylife.demo.entity.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.CONFLICT, reason = "The given Email is already in the database.")
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String param1) {
        super(param1 + "already exists!");
    }

    public DuplicateEmailException(User param1) {
        super(param1 + "already exists!");
    }

}
