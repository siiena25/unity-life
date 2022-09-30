package com.unitylife.demo.dao_impl;

import com.unitylife.demo.dao.EventDao;
import com.unitylife.demo.entity.Event;
import com.unitylife.demo.entity.User;
import com.unitylife.demo.exceptions.AuthenticationException;
import com.unitylife.demo.exceptions.ResourceNotFoundException;
import com.unitylife.demo.row_mappers.IntegerRowMapper;
import com.unitylife.demo.row_mappers.UserRowMapper;
import com.unitylife.demo.service.FriendsService;
import com.unitylife.demo.service.GroupService;
import com.unitylife.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository("PostgresEventRepo")
public class PostgreSQLEventDaoImpl implements EventDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FriendsService friendsService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Override
    public Collection<Event> getAllEvents() {
        final String sql = "SELECT * FROM events";
        List<Event> events = jdbcTemplate.query(sql, new PostgreSQLEventDaoImpl.EventRowMapper());
        return events;
    }

    @Override
    public Collection<Event> getCurrentEvents() {
        final String sql = "SELECT * FROM events WHERE CURRENT_TIMESTAMP < timeend";
        List<Event> events = jdbcTemplate.query(sql, new PostgreSQLEventDaoImpl.EventRowMapper());
        return events;
    }

    @Override
    public Event getEventByEventId(String eventId) {
        final String sql = "SELECT * FROM events WHERE eventid = ?";
        Event event = jdbcTemplate.queryForObject(sql, new EventRowMapper(), eventId);
        return event;
    }

    @Override
    public int getEventIdByTitle(String title) {
        final String sql = "SELECT eventid FROM events WHERE title = ?";
        Integer eventId = jdbcTemplate.queryForObject(sql, new IntegerRowMapper("eventid"), title);
        return eventId;
    }

    @Override
    public Event getEventByTitle(String title, int authorId) {
        final String sql = "SELECT * FROM events WHERE title = ? AND authorid = ?";
        Event event = jdbcTemplate.queryForObject(sql, new EventRowMapper(), title, authorId);
        return event;
    }

    @Override
    public void createEvent(Event event) {
        final String sql = "INSERT INTO events (authorId, title, categoryTitle, eventAvatar, address, description, " +
                "timeStart, timeEnd, latitude, longitude) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                event.getAuthorId(),
                event.getTitle(),
                event.getCategoryTitle(),
                event.getEventAvatar(),
                event.getAddress(),
                event.getDescription(),
                event.getTimeStart(),
                event.getTimeEnd(),
                event.getLatitude(),
                event.getLongitude());
    }

    @Override
    public void updateEvent(int id, Event event) {
        checkEventAuthor(id, event.getAuthorId());
        final String sql =
                "UPDATE events SET title = ?, categoryTitle = ?, eventAvatar = ?, address = ?, " +
                "description = ?, latitude = ?, longitude = ? WHERE eventid = ?";
        jdbcTemplate.update(sql,
                event.getTitle(),
                event.getCategoryTitle(),
                event.getEventAvatar(),
                event.getAddress(),
                event.getDescription(),
                event.getLatitude(),
                event.getLongitude(),
                event.getEventId()
        );
    }

    //to see if the post(id) is written by the user(id1)
    public void checkEventAuthor(int id1, int id) {
        String result;
        String eventAuthorId = "eventAuthorId";
        try {
            eventAuthorId = jdbcTemplate.queryForObject
                    ("SELECT authorid FROM events WHERE authorid = ?",
                            new Object[]{id},
                            String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(eventAuthorId);
        }
        try {
            String sql = "SELECT * FROM users WHERE userid = ?";
            result = jdbcTemplate.queryForObject(sql, new Object[]{eventAuthorId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }
        if (result == null) {
            throw new AuthenticationException(id1);
        }
    }

    @Override
    public void removeEventById(int id) {
        final String sql = "DELETE FROM events WHERE eventId = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet resultSet, int i) throws SQLException {
            Event event = new Event();
            event.setEventId(resultSet.getInt("eventId"));
            event.setAuthorId(resultSet.getInt("authorId"));
            event.setTitle(resultSet.getString("title"));
            event.setCategoryTitle(resultSet.getString("categoryTitle"));
            event.setEventAvatar(resultSet.getString("eventAvatar"));
            event.setAddress(resultSet.getString("address"));
            event.setDescription(resultSet.getString("description"));
            event.setTimeStart(resultSet.getTimestamp("timeStart"));
            event.setTimeEnd(resultSet.getTimestamp("timeEnd"));
            event.setLatitude(resultSet.getFloat("latitude"));
            event.setLongitude(resultSet.getFloat("longitude"));
            return event;
        }
    }
}
