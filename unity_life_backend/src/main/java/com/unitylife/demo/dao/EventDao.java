package com.unitylife.demo.dao;

import com.unitylife.demo.entity.Event;

import java.util.Collection;

public interface EventDao {
    void createEvent(Event event);

    void updateEvent(int id, Event event);

    void removeEventById(int id);

    Collection<Event> getAllEvents();

    Collection<Event> getCurrentEvents();

    Event getEventByTitle(String title, int authorId);

    Event getEventByEventId(String eventId);

    int getEventIdByTitle(String title);
}
