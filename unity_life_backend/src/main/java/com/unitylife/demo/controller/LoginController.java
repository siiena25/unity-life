package com.unitylife.demo.controller;

import com.unitylife.demo.entity.LogInfo;
import com.unitylife.demo.entity.User;
import com.unitylife.demo.entity.UserWithToken;
import com.unitylife.demo.exceptions.AuthenticationException;
import com.unitylife.demo.exceptions.DuplicateEmailException;
import com.unitylife.demo.exceptions.LoginException;
import com.unitylife.demo.service.AuthenticationService;
import com.unitylife.demo.service.LogInfoService;
import com.unitylife.demo.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(description = "Login Controller")
public class LoginController {

    @Autowired
    private LogInfoService logInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/successLogin", method = RequestMethod.GET)
    public @ResponseBody String createToken() {
        User user = userService.getUserByEmail(authenticationService.getEmail());
        String token = "authorization_token = " + authenticationService.getToken();
        int id = user.getId();
        return token + "\n" + "userid = " + id;
    }

    @RequestMapping(value = "/login/{email}/{password}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody UserWithToken getToken(
            @ApiParam(value = "Email", required = true)
            @PathVariable("email") String email,

            @ApiParam(value = "Password", required = true)
            @PathVariable("password") String password
    ) {
        try {
            User user = userService.getUserByEmail(email);
            System.out.println("LOG:: login user= " + user);
            System.out.println("LOG:: login password = " + password);
            if (password.equals(user.getPassword())) {
                String token;
                LogInfo logInfo = new LogInfo(user.getId(), user.getEmail(), user.getPassword());
                System.out.println("LOG:: login LogInfo = " + logInfo);
                logInfoService.addLogInfoData(logInfo);
                token = logInfoService.getTokenByUserId(user.getId());
                System.out.println("LOG:: login token = " + token + " user " + user);
                System.out.println("LOG:: " + new UserWithToken(user, token));
                return new UserWithToken(user, token);
            }
        } catch (Exception e) {
            throw new LoginException();
        }
        return null;
    }

    @RequestMapping(value = "/logout/{userId}", method = RequestMethod.GET)
    public void logout(
            @ApiParam(value = "User ID", required = true)
            @PathVariable("userId") int userId
    ) {
        String token = logInfoService.getTokenByUserId(userId);
        if (token != null) {
            logInfoService.removeLogInfoDataByUserId(userId);
        }
    }

    @ApiOperation(value = "Registers User WHERE: id is not required AND gender must be Male or Female")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })

    @RequestMapping(value = "/register", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody User register(
            @ApiParam(value = "User information", required = true)
            @RequestBody User user) {
        try {
            System.out.println("LOG:: register start " + user);
            userService.register(user);
            System.out.println("LOG:: register " + userService.getUserByEmail(user.getEmail()));
            return userService.getUserByEmail(user.getEmail());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(user);
        }
    }
}