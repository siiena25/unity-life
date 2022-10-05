package com.unitylife.demo.service;

import com.unitylife.demo.dao.*;
import com.unitylife.demo.dao_impl.PostgreSQLEventDaoImpl;
import com.unitylife.demo.dao_impl.PostgreSQLGroupDaoImpl;
import com.unitylife.demo.entity.Event;
import com.unitylife.demo.entity.Group;
import com.unitylife.demo.row_mappers.IntegerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class EventService {
    PostgreSQLEventDaoImpl postgreSQLEventDao;
    PostgreSQLGroupDaoImpl postgreSQLGroupDao;

    EventService(PostgreSQLEventDaoImpl postgreSQLEventDao,
                 PostgreSQLGroupDaoImpl postgreSQLGroupDao) {
        this.postgreSQLEventDao = postgreSQLEventDao;
        this.postgreSQLGroupDao = postgreSQLGroupDao;
    }

    @Autowired
    @Qualifier("PostgresEventRepo")
    private EventDao eventDao;

    @Autowired
    @Qualifier("PostgresGroupRepo")
    private GroupDao groupDao;

    public Collection<Event> getAllEvents() {
        return this.eventDao.getAllEvents();
    }

    public Event getEventByEventId(String eventId) {
        return this.eventDao.getEventByEventId(Integer.parseInt(eventId));
    }


    public Event getEventByTitleAndAuthorId(String title, int authorId) {
        return this.eventDao.getEventByTitle(title, authorId);
    }

    public Collection<Event> getCurrentEvents() {
        return this.eventDao.getCurrentEvents();
    }

    public void updateEvent(int id, Event event) {
        this.eventDao.updateEvent(id, event);
    }

    //then Event created, also automatically created Group with groupId = eventId
    public void createEvent(Event event) {
        this.eventDao.createEvent(event);
        int eventId = this.eventDao.getEventIdByTitle(event.getTitle());
        Group group = new Group(eventId, event.getAuthorId(), event.getTitle());
        this.groupDao.createGroup(group);
        this.groupDao.addUserToGroup(group.getGroupID(), group.getAdminId());
    }

    public void removeEventById(int id) {
        this.eventDao.removeEventById(id);
    }

}
