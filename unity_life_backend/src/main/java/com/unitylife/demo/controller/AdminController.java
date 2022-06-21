package com.unitylife.demo.controller;

import com.unitylife.demo.entity.DBPost;
import com.unitylife.demo.entity.Group;
import com.unitylife.demo.entity.User;
import com.unitylife.demo.exceptions.ResourceNotFoundException;
import com.unitylife.demo.service.GroupService;
import com.unitylife.demo.service.PostService;
import com.unitylife.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/")
@Api(description = "Controller for admin")
public class AdminController {
  @Autowired
  private PostService postService;

  @Autowired
  private UserService userService;

  @Autowired
  private GroupService groupService;

  @ApiOperation(value = "Returns every user in the database")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved list of user"),
      @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
      @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
  })
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public Collection<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @ApiOperation(value = "Return every post in the database")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved list of post"),
      @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
      @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
  })
  @RequestMapping(value = "/posts", method = RequestMethod.GET)
  public Collection<DBPost> getAllPosts() {
    return postService.getAllPosts();
  }

  @ApiOperation(value = "Return every post in group given group name")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved list of posts"),
      @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
      @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  @RequestMapping(value = "/groupname/{name}", method = RequestMethod.GET)
  public Collection<DBPost> getPostsFromGroup(@ApiParam(value = "Name of the group", required = true)
                                              @PathVariable("name") String name) {
    try {
      return postService.getPostsFromGroup(name);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException(name);
    }
  }

  @ApiOperation(value = "Return every post in group given Group ID")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved list of posts"),
      @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
      @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  @RequestMapping(value = "/group/{groupid}", method = RequestMethod.GET)
  public Collection<DBPost> getPostsFromGroup(@ApiParam(value = "Group ID", required = true)
                                              @PathVariable("groupid") int groupid) {
    try {
      return postService.getPostsFromGroup(groupid);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException(Integer.toString(groupid));
    }
  }

  @ApiOperation(value = "Return every group in the database")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved list of groups"),
      @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
      @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
  })
  @RequestMapping(value = "/groups", method = RequestMethod.GET)
  public Collection<Group> getAllGroups() {
    return groupService.getAllGroup();
  }

}
