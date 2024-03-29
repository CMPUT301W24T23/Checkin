package com.example.checkin;

// represents message with a title and body
public class Message {

    private String title;
    private String body;

    private String type;

    private String eventid;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String title, String body) {
        this.title = title;
        this.body = body;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }
}
