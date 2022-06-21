package com.unitylife.demo.controller;

import com.unitylife.demo.entity.Group;
import com.unitylife.demo.exceptions.ResourceNotFoundException;
import com.unitylife.demo.helperMethods.AccessManager;
import com.unitylife.demo.service.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
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
 * Represents a controller for the Group Service.
 */

@RestController
@RequestMapping("/{userid}/group")
@Api(description = "Group Controller")

public class GroupController {

    @Autowired
    private AccessManager accessManager = new AccessManager();

    @Autowired
    private GroupService groupService;

    @ApiOperation(value = "Return every group in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of groups"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
    @RequestMapping(value = "/all/", method = RequestMethod.GET)
    public Collection<Group> getAllGroups(@ApiParam(value = "User ID", required = true)
                                            @PathVariable("userid") int userid,
                                          @RequestHeader("authorization") String token) {
        System.out.println("Before manager");
        accessManager.checkUser(userid, token);
        System.out.println("After manager");
        return groupService.getAllGroup();
    }

    @ApiOperation(value = "Returns every group given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of groups"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/name/{name}/", method = RequestMethod.GET)
    public Collection<Group> getGroupByName(@ApiParam(value = "Group name", required = true)
                                              @PathVariable("name") String name,
                                            @ApiParam(value = "User ID calling method", required = true)
                                              @PathVariable("userid") int userid,
                                            @RequestHeader("authorization") String token) {
        try {
            accessManager.checkUser(userid, token);
            return groupService.getGroupByName(name);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(name);
        }
    }


    @ApiOperation(value = "Returns every group given Admin ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of groups"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/admin/{admin}/", method = RequestMethod.GET)
    public Collection<Group> getGroupByAdmin(@ApiParam(value = "Group Admin", required = true)
                                               @PathVariable("admin") String admin,
                                             @ApiParam(value = "User ID", required = true)
                                               @PathVariable("userid") int userid,
                                             @RequestHeader("authorization") String token) {
        accessManager.checkUser(userid, token);
        Collection<Group> result = groupService.getGroupByAdmin(userid);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(admin);
        }
        return result;
    }

    @ApiOperation(value = "Returns every group member is in given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of groups"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/memberid/{memberid}/", method = RequestMethod.GET)
    public Collection<Group> getAllGroupsForUser(@ApiParam(value = "Membership ID", required = true)
                                                   @PathVariable("memberid") int memberid,
                                                 @ApiParam(value = "User ID calling method", required = true)
                                                   @PathVariable("userid") int userid,
                                                 @RequestHeader("authorization") String token) {
        accessManager.checkUser(userid, token);
        Collection<Group> result = groupService.getAllGroupsForUser(memberid);
        if (result.size() == 0) {
            throw new ResourceNotFoundException(Integer.toString(memberid));
        }
        return result;
    }

    @ApiOperation(value = "Create a new group where id is not required")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created group"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createGroup(@ApiParam(value = "Group", required = true)
                            @RequestBody Group group,
                            @ApiParam(value = "User ID calling method", required = true)
                            @PathVariable("userid") int userid,
                            @RequestHeader("authorization") String token) {
        accessManager.checkUser(userid, token);
        groupService.createGroup(group);
    }

    @ApiOperation(value = "Sends a joining request to a group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully sent a joining request to a group"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/join/groupid/{groupid}/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendJoinRequest(@ApiParam(value = "group ID", required = true)
                                @PathVariable("groupid") int groupid,
                                @ApiParam(value = "User ID calling method", required = true)
                                @PathVariable("userid") int userid,
                                @RequestHeader("authorization") String token) {
        accessManager.checkUser(userid, token);
        groupService.sendJoinRequest(groupid, userid);
    }

    @ApiOperation(value = "Adds user to group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added user to group"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/groupid/{groupid}/add/userid/{userid}/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addUserToGroup(@ApiParam(value = "group ID", required = true)
                                        @PathVariable("groupid") int groupid,
                                        @ApiParam(value = "User ID calling method", required = true)
                                        @PathVariable("userid") int userid) {
        groupService.addUserToGroup(groupid, userid);
    }

    @ApiOperation(value = "Adds member to group by admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added member to group"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/groupid/{groupid}/memberid/{memberid}/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addMemberToGroupByAdmin(@ApiParam(value = "group ID", required = true)
                                 @PathVariable("groupid") int groupid,
                                        @ApiParam(value = "Membership ID", required = true)
                                 @PathVariable("memberid") int memberid,
                                        @ApiParam(value = "User ID calling method", required = true)
                                 @PathVariable("userid") int userid,
                                        @RequestHeader("authorization") String token) {
        accessManager.checkUser(userid, token);
        groupService.addMemberToGroupByAdmin(userid, groupid, memberid);
    }

    @ApiOperation(value = "Removes member from a group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed member from group"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/groupid/{groupid}/memberid/{memberid}", method = RequestMethod.DELETE)
    public void removeMemberFromGroup(@ApiParam(value = "group ID", required = true)
                                      @PathVariable("groupid") int groupid,
                                      @ApiParam(value = "Membership ID", required = true)
                                      @PathVariable("memberid") int memberid,
                                      @ApiParam(value = "User ID calling method", required = true)
                                      @PathVariable("userid") int userid,
                                      @RequestHeader("authorization") String token) {
        accessManager.checkUser(userid, token);
        groupService.removeMemberFromGroup(userid, groupid, memberid);
    }

    @ApiOperation(value = "Removes a group given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed group"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/remove/{groupid}/", method = RequestMethod.DELETE)
    public void removeGroupById(@ApiParam(value = "group ID", required = true)
                                @PathVariable("groupid") int groupid,
                                @ApiParam(value = "User ID", required = true)
                                @PathVariable("userid") int userid,
                                @RequestHeader("authorization") String token) {
        accessManager.checkUser(userid, token);
        groupService.removeGroupById(groupid);
    }
}