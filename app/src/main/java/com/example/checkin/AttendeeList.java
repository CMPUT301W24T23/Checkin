package com.example.checkin;

import java.util.ArrayList;

public class AttendeeList {
    //has list of attendees
    //see # of checked-in users when passed an event
    private ArrayList<Attendee> Attendees = new ArrayList<Attendee>();

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
        if (Attendees.isEmpty()){
            return false;
        }
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
     * a valid Attendee object
     */
    public void addAttendee(Attendee a){
        Attendees.add(a);

        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //CollectionReference attendeeRef;
        //attendeeRef = db.collection("Attendees");

        //HashMap<String, String> data = new HashMap<>();
        //data.put("Name", a.getName());
        //data.put("Homepage", a.getHomepage());
        //data.put("Email", a.getEmail());
        //data.put("Phone", a.getPhoneNumber());
        //data.put("Tracking", Boolean.toString(a.trackingEnabled()));

        // attendeeRef.document(Integer.toString(a.getUserId())).set(data);

        //attendeeRef.set(a);
    }

    /**
     * Removes an attendee from the list
     * @param a
     * a valid Attendee object
     */
    public void removeAttendee(Attendee a){
        Attendees.remove(a);
    }

    public ArrayList<Attendee> getAttendees() {
        return Attendees;
    }
}
