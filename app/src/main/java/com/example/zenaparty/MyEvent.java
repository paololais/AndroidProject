package com.example.zenaparty;

public class MyEvent {
    Long event_id;
    String price;
    String event_name, date, location, time, type;

    public Long getEvent_id() {
        return event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getDate() {return date;}

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }
}