package com.unitylife.demo.controller;

import com.unitylife.demo.entity.Event;
import com.unitylife.demo.entity.User;
import com.unitylife.demo.helperMethods.AccessManager;
import com.unitylife.demo.service.EventService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/{userid}/event")
@Api(description = "Event Controller")
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    AccessManager accessManager = new AccessManager();

    @ApiOperation(value = "Return every event in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of event"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
    @RequestMapping(value = "/events/", method = RequestMethod.GET)
    public Collection<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @ApiOperation(value = "Return current events in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of event"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
    @RequestMapping(value = "/currentEvents/", method = RequestMethod.GET)
    public Collection<Event> getCurrentEvents() {
        return eventService.getCurrentEvents();
    }

    @ApiOperation(value = "Removes event from database given Event ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed event from database"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
    @RequestMapping(value = "/eventId/{eventId}/token/{token}", method = RequestMethod.DELETE)
    public void removeEventById(@ApiParam(value = "User ID", required = true)
                               @PathVariable("userid") int userId,
                               @ApiParam(value = "Event ID", required = true)
                               @PathVariable("eventId") int eventId,
                                @PathVariable("token") int token) {
        accessManager.checkUser(userId, token);
        eventService.removeEventById(eventId);
    }

    @ApiOperation(value = "Create event WHERE id is not required")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created event"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/createEvent/{userId}/token/{token}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Event createEvent(@ApiParam(value = "Event", required = true) @RequestBody Event assignment,
                                          @ApiParam(value = "User ID calling method", required = true)
                           @PathVariable("userId") int userId,
                                          @PathVariable("token") int token) {
        //accessManager.checkUser(userId, token);
        eventService.createEvent(assignment);
        return eventService.getEventByTitleAndAuthorId(assignment.getTitle(), userId);
    }

    @ApiOperation(value = "Updates Event given user ID and event ID")
    @RequestMapping(value = "/update/eventId/{eventId}/userId/{userId}/token/{token}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateEvent(@ApiParam(value = "User ID", required = true)
                           @PathVariable("userId") int userId,
                           @ApiParam(value = "Event ID", required = true)
                           @PathVariable("eventId") int eventId,
                            @PathVariable("token") int token,
                           @RequestBody Event event) {
        accessManager.checkUser(userId, token);
        eventService.updateEvent(eventId, event);
    }
}
