package com.example.checkin;

import java.util.ArrayList;

/**
 * Tracks all the events currently in use
 */
public class EventList {
    //TODO:
    //      - Firebase Integration
    private ArrayList<Event> Events = new ArrayList<Event>();        //array of all events

    /**
     * Adds an event to the EventList
     * @param e
     * a valid event object
     */
    public void addEvent (Event e){
        Events.add(e);
        //TODO: Firebase integration
    }

    /**
     * Removes an event from the EventList
     * @param e
     * a valid Event object
     */
    public void removeEvent(Event e){
        Events.remove(e);
        //TODO: Firebase integration
    }

    public ArrayList<Event> getEvents(){
        return Events;
    }

}
