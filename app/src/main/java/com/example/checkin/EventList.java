package com.example.checkin;

import java.util.ArrayList;

/**
 * Tracks all the events currently in use
 */
public class EventList {
    //TODO:
    //      - Firebase Integration
    private ArrayList<Event> Events;        //array of all events

    public void addEvent (Event e){
        Events.add(e);
        //TODO: Firebase integration
    }

    public void removeEvent(Event e){
        Events.remove(e);
        //TODO: Firebase integration
    }


}
