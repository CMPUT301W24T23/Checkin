package com.example.checkin;

import java.io.Serializable;
import java.util.ArrayList;

/*
`AttendeeList` is a serializable class representing a list of attendees.
Provides functionality to count checked-in and subscribed attendees for a given event,
checks if an attendee exists in the list, and supports adding or removing attendees.
The class facilitates easy management and retrieval of attendee information within an event-check-in system.
 */

public class AttendeeList implements Serializable {
    //has list of attendees
    //see # of checked-in users when passed an event
    private ArrayList<Attendee> Attendees = new ArrayList<Attendee>();

    /**
     * Clears the list of attendees.
     */
    public void clear() {
        Attendees.clear();
    }

    public AttendeeList(){}

    /**
     * Returns the number of attendees checked in to the event
     * @param e
     * a valid event object
     * @return
     * the number of attendees currently checked in
     */
    public int CheckedInCount(Event e){
        int count = 0;
        for(Attendee a: Attendees){
            if(e.IsCheckedIn(a)){
                count += 1;
            }
        }
        return count;
    }

    /**
     * Return number of subscribers to the event
     * @param e
     * @return
     */
    public int SubscribedCount(Event e){
        int count = 0;
        for(Attendee a: Attendees){
            if(e.IsSubscribed(a)){
                count += 1;
            }
        }
        return count;
    }

    /**
     * Find if Attendee exists in list
     * @param toFind
     * a valid Attendee object to be found
     * @return
     * boolean
     */
    public boolean contains (Attendee toFind){
        if (toFind == null || toFind.getUserId() == null || Attendees.isEmpty()) {
            return false;
        }
        for (Attendee a : Attendees) {
            if (toFind.getUserId().equals(a.getUserId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an attendee to the list
     * @param a
     * a valid Attendee object
     */
    public void addAttendee(Attendee a){
        Attendees.add(a);
    }

    /**
     * Removes an attendee from the list
     * @param a
     * a valid Attendee object
     */
    public void removeAttendee(Attendee a){
        Attendees.remove(a);
        Attendees.removeIf(AinList -> a.getUserId().equals(AinList.getUserId()));
    }

    public ArrayList<Attendee> getAttendees() {
        return Attendees;
    }

    public Attendee getAttendeePosition(int position){
        Attendee attendee = Attendees.get(position);
        return attendee;
    }


}
