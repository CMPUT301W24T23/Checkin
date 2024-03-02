package com.example.checkin;

import java.util.ArrayList;

public class AttendeeList {
    //has list of attendees
    //see # of checked-in users when passed an event
    private ArrayList<Attendee> Attendees;

    /**
     * Returns the number of attendees checked in to the event
     * @param e
     * @return
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
     * Find if Attendee exists in list
     * @param toFind
     * @return
     * boolean
     */
    public boolean contains (Attendee toFind){
        for (Attendee a: Attendees){
            if (a == toFind){
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an attendee to the list
     * @param a
     */
    public void addAttendee(Attendee a){
        Attendees.add(a);
    }

    /**
     * Removes an attendee from the list
     * @param a
     */
    public void removeAttendee(Attendee a){
        Attendees.remove(a);
    }

    public ArrayList<Attendee> getAttendees() {
        return Attendees;
    }
}
