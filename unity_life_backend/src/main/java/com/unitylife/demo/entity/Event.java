package com.unitylife.demo.entity;

import com.unitylife.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

public class Event {
    private int eventId;
    private int authorId;
    private String title;
    private String categoryTitle;
    private String eventAvatar = null;
    private String address;
    private String description = null;
    private float latitude;
    private float longitude;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp timeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp timeEnd;

    @Autowired
    UserService userService;

    public Event(int authorId, String title, String categoryTitle, String eventAvatar, String address,
                 String description, Timestamp timeStart, Timestamp timeEnd, float latitude, float longitude) {
        this.authorId = authorId;
        this.title = title;
        this.categoryTitle = categoryTitle;
        this.eventAvatar = eventAvatar;
        this.address = address;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Event() {
    }

    @Override
    public int hashCode() {
        int result = getEventId();
        result = 31 * result + Integer.hashCode(authorId);
        result = 31 * result + title.hashCode();
        result = 31 * result + categoryTitle.hashCode();
        result = 31 * result + (eventAvatar != null ? eventAvatar.hashCode() : 0);
        result = 31 * result + address.hashCode();
        result = 31 * result + categoryTitle.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + timeStart.hashCode();
        result = 31 * result + timeEnd.hashCode();
        result = 31 * result + Float.hashCode(latitude);
        result = 31 * result + Float.hashCode(longitude);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", authorId='" + authorId + '\'' +
                ", title='" + title + '\'' +
                ", categoryTitle='" + categoryTitle + '\'' +
                ", eventAvatar=" + eventAvatar +
                ", address=" + address +
                ", description=" + description +
                ", timeStart='" + timeStart +
                ", timeEnd=" + timeEnd +
                ", latitude=" + latitude +
                ", longitude=" + longitude + '\'' +
                '}';
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getEventAvatar() {
        return eventAvatar;
    }

    public void setEventAvatar(String eventAvatar) {
        this.eventAvatar = eventAvatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
