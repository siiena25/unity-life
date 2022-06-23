package com.unitylife.demo.controller;

import com.unitylife.demo.entity.LogInfo;
import com.unitylife.demo.entity.User;
import com.unitylife.demo.exceptions.DuplicateEmailException;
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

    @RequestMapping(value = "/login/{userId}", method = RequestMethod.GET)
    public @ResponseBody Integer getToken(
            @ApiParam(value = "User ID", required = true)
            @PathVariable("userId") int userId
    ) {
        User user = userService.getUserById(userId);
        Integer token = null;
        if (user != null) {
            LogInfo logInfo = new LogInfo(user.getId(), user.getEmail(), user.getPassword());
            logInfoService.addLogInfoData(logInfo);
            token = logInfoService.getTokenByUserId(userId);
            System.out.println("LoginController: getToken=" + token);
        }
        return token;
    }

    @RequestMapping(value = "/logout/{userId}", method = RequestMethod.GET)
    public void logout(
            @ApiParam(value = "User ID", required = true)
            @PathVariable("userId") int userId
    ) {
        Integer token = logInfoService.getTokenByUserId(userId);
        if(token != null) {
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
            System.out.println(user.getFirstName());
            System.out.println(user.getLastName());
            userService.register(user);
            return userService.getUserByEmail(user.getEmail());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(user);
        }
    }
}