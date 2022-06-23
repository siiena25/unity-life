package com.unitylife.demo.controller;

import com.unitylife.demo.entity.User;
import com.unitylife.demo.exceptions.DuplicateEmailException;
import com.unitylife.demo.exceptions.ResourceNotFoundException;
import com.unitylife.demo.helperMethods.AccessManager;
import com.unitylife.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Represents a controller for the User service.
 */

@RestController
@RequestMapping("/{userid}/user")
@Api(description = "User Controller")
public class UserController {

    @Autowired
    private AccessManager accessManager = new AccessManager();

    @Autowired
    private UserService userService;


    @ApiOperation(value = "Gets User given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/user/{userid2}/token/{token}", method = RequestMethod.GET)
    public User getUserById(@ApiParam(value = "User ID that you are searching for", required = true)
                            @PathVariable("userid2") int userid2,
                            @ApiParam(value = "User ID calling method", required = true)
                            @PathVariable("userid") int userid,
                            @PathVariable("token") int token) {
        try {
            accessManager.checkUser(userid, token);
            return userService.getUserById(userid2);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(Integer.toString(userid2));
        }
    }


    @ApiOperation(value = "Delete User given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/removeUser/{userid2}/token/{token}", method = RequestMethod.DELETE)
    public void deleteUserById(@ApiParam(value = "User ID that is to be deleted", required = true)
                               @RequestBody int userid2,
                               @ApiParam(value = "User ID calling method", required = true)
                               @PathVariable("userid") int userid,
                               @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.removeUserById(userid2);
    }

    @ApiOperation(value = "Updates User")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/updateUser/{userId}/token/{token}", method = RequestMethod.PUT, params = "id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUserById(@ApiParam(value = "User ID", required = true) @RequestBody User user,
                               @ApiParam(value = "User ID calling method", required = true)
                               @PathVariable("userid") int userid,
                               @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.updateUser(user);
    }


    @ApiOperation(value = "Gets List of User given first name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/firstName/{name}/token/{token}", method = RequestMethod.GET)
    public Collection<User> getUsersByFirstName(@ApiParam(value = "First name", required = true)
                                                @PathVariable("name") String name,
                                                @ApiParam(value = "User ID calling method", required = true)
                                                @PathVariable("userid") int userid,
                                                @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        Collection<User> result = userService.getUsersByFirstName(name);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(name);
        }
        return result;
    }

    @ApiOperation(value = "Gets List of User given last name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/lastName/{name}/token/{token}", method = RequestMethod.GET)
    public Collection<User> getUsersByLastName(@ApiParam(value = "Last name", required = true)
                                               @PathVariable("name") String name,
                                               @ApiParam(value = "User ID calling method", required = true)
                                               @PathVariable("userid") int userid,
                                               @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        Collection<User> result = userService.getUsersByLastName(name);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(name);
        }
        return result;
    }

    @ApiOperation(value = "Gets List of User given full name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/fullname/{name}/token/{token}", method = RequestMethod.GET)
    public User getUsersByFullName(@ApiParam(value = "name", required = true)
                                   @PathVariable("name") String name,
                                   @ApiParam(value = "User ID calling method", required = true)
                                   @PathVariable("userid") int userid,
                                   @PathVariable("token") int token) {
        try {
            accessManager.checkUser(userid, token);
            return userService.getUsersByFullName(name);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(name);
        }
    }

    @ApiOperation(value = "Gets User given username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/username/token/{token}/{firstname}.{lastname}", method = RequestMethod.GET)
    public User personalPage(
            @ApiParam(value = "Firstname", required = true)
            @PathVariable("firstname") String firstName,
            @ApiParam(value = "Lastname", required = true)
            @PathVariable("lastname") String lastName,
            @ApiParam(value = "User ID calling method", required = true)
            @PathVariable("userid") int userid,
            @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        try {
            return userService.getUserByUserName(firstName + "." + lastName);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(firstName, lastName);
        }
    }

    @ApiOperation(value = "Gets List of User given email")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/email/{email}/token/{token}", method = RequestMethod.GET)
    public User getUserByEmail(@ApiParam(value = "Email address", required = true)
                               @PathVariable("email") String email,
                               @ApiParam(value = "User ID calling method", required = true)
                               @PathVariable("userid") int userid,
                               @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        try {
            return userService.getUserByEmail(email);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(email);
        }
    }

    @ApiOperation(value = "Gets List of User given age")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/age/{age}/token/{token}", method = RequestMethod.GET)
    public Collection<User> getUserByAge(@ApiParam(value = "Age", required = true)
                                         @PathVariable("age") int age,
                                         @ApiParam(value = "User ID calling method", required = true)
                                         @PathVariable("userid") int userid,
                                         @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        Collection<User> result = userService.getUsersByAge(age);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(age);
        }
        return result;
    }

    @ApiOperation(value = "Gets List of User given gender")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/gender/{gender}/token/{token}", method = RequestMethod.GET)
    public Collection<User> getUsersByGender(@ApiParam(value = "Gender", required = true)
                                             @PathVariable("gender") String gender,
                                             @ApiParam(value = "User ID calling method", required = true)
                                             @PathVariable("userid") int userid,
                                             @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        Collection<User> result = userService.getUsersByGender(gender);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(gender);
        }
        return result;
    }

    @ApiOperation(value = "Gets List of User given country")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/country/{country}/token/{token}", method = RequestMethod.GET)
    public Collection<User> getUsersByCountry(@ApiParam(value = "Country", required = true)
                                              @PathVariable("country") String country,
                                              @ApiParam(value = "User ID calling method", required = true)
                                              @PathVariable("userid") int userid,
                                              @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        Collection<User> result = userService.getUsersByCountry(country);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(country);
        }
        return result;
    }

    @ApiOperation(value = "Gets List of User given city")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/city/{city}/token/{token}", method = RequestMethod.GET)
    public Collection<User> getUsersByCity(@ApiParam(value = "City", required = true)
                                           @PathVariable("city") String city,
                                           @ApiParam(value = "User ID calling method", required = true)
                                           @PathVariable("userid") int userid,
                                           @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        Collection<User> result = userService.getUsersByCity(city);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(city);
        }
        return result;
    }


    /*@ApiOperation(value = "Login")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully logged in"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@ApiParam(value = "Email address", required = true) @RequestBody String email,
                      @ApiParam(value = "Password", required = true) @RequestBody String password) {
        userService.login(email, password);
    }*/

    @ApiOperation(value = "Set the first name of the user")
    @RequestMapping(value = "/setfirst/{first}/token/{token}", method = RequestMethod.PUT)
    public void setFirst(@ApiParam(value = "First name", required = true)
                         @PathVariable("first") String first,
                         @ApiParam(value = "User ID calling method", required = true)
                         @PathVariable("userid") int userid,
                         @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.setFirst(userid, first);
    }

    @ApiOperation(value = "Set the last name of the user")
    @RequestMapping(value = "/setlast/{last}/token/{token}", method = RequestMethod.PUT)
    public void setLast(@ApiParam(value = "Last name", required = true)
                        @PathVariable("last") String last,
                        @ApiParam(value = "User ID calling method", required = true)
                        @PathVariable("userid") int userid,
                        @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.setLast(userid, last);
    }

    @ApiOperation(value = "Set email of the user")
    @RequestMapping(value = "/setemail/{email}/token/{token}", method = RequestMethod.PUT)
    public void setEmail(@ApiParam(value = "Email", required = true)
                         @PathVariable("email") String email,
                         @ApiParam(value = "User ID calling method", required = true)
                         @PathVariable("userid") int userid,
                         @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        try {
            userService.setEmail(userid, email);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(email);
        }
    }

    @ApiOperation(value = "Set age of the user")
    @RequestMapping(value = "/setage/{age}/token/{token}", method = RequestMethod.PUT)
    public void setAge(@ApiParam(value = "Age", required = true)
                       @PathVariable("age") int age,
                       @ApiParam(value = "User ID calling method", required = true)
                       @PathVariable("userid") int userid,
                       @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.setAge(userid, age);
    }

    @ApiOperation(value = "Set gender of the user WHERE gender has to be Male or Female")
    @RequestMapping(value = "/setgender/{gender}/token/{token}", method = RequestMethod.PUT)
    public void setGender(@ApiParam(value = "Gender", required = true)
                          @PathVariable("gender") String gender,
                          @ApiParam(value = "User ID calling method", required = true)
                          @PathVariable("userid") int userid,
                          @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.setGender(userid, gender);
    }

    @ApiOperation(value = "Set country of the user")
    @RequestMapping(value = "/setcountry/{country}/token/{token}", method = RequestMethod.PUT)
    public void setCountry(@ApiParam(value = "Country", required = true)
                           @PathVariable("country") String country,
                           @ApiParam(value = "User ID calling method", required = true)
                           @PathVariable("userid") int userid,
                           @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.setCountry(userid, country);
    }

    @ApiOperation(value = "Set city of the user")
    @RequestMapping(value = "/setcity/{city}/token/{token}", method = RequestMethod.PUT)
    public void setCity(@ApiParam(value = "City", required = true)
                        @PathVariable("city") String city,
                        @ApiParam(value = "User ID calling method", required = true)
                        @PathVariable("userid") int userid,
                        @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.setCity(userid, city);
    }

    @ApiOperation(value = "Set password for the user")
    @RequestMapping(value = "/setpassword/{password}/token/{token}", method = RequestMethod.PUT)
    public void setPassword(@ApiParam(value = "Password", required = true)
                            @PathVariable("password") String password,
                            @ApiParam(value = "User ID calling method", required = true)
                            @PathVariable("userid") int userid,
                            @PathVariable("token") int token) {
        accessManager.checkUser(userid, token);
        userService.setPassword(userid, password);
    }
}
