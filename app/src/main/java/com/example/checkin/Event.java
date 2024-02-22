package com.example.checkin;

import android.media.Image;

import java.util.ArrayList;

public class Event {
    //TODO:
    //      - assign QR CODE
    //      - remove QR CODE
    //      - assign poster
    //      - remove poster
    //      - event ID generation
    //      - Firebase Integration

    private int EventId;        //unique identifier for event
    private Image poster;
    //private QRCode code;

    private ArrayList<Attendee> Attendees;      //list of attendees subscribed to the event TODO: Firebase Integration
    private ArrayList<Attendee> CheckInList;    //attendees CURRENTLY checked in to the event TODO: Firebase Integration


    private int generateEventId(){
        //TODO: Generate the EventId for a new event
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user
        return 1;
    }
    public Event() {
        this.EventId = generateEventId();
    }

    public void userSubs (Attendee a){
        //Attendee subscribes to receiving information updates
        Attendees.add(a);
    }

    public void userUnSubs (Attendee a){
        //Attendee unsubscribes to receiving information updates
        Attendees.remove(a);
    }

    public void userCheckIn (Attendee a){
        if (CheckInList.contains(a)){
            //if in list, the user is checking out of the event
            a.CheckIn(this);
            CheckInList.remove(a);
        } else{
            //otherwise the user is checking in
            a.CheckIn(this);
            CheckInList.add(a);
        }


    }




    public int getEventId() {
        return EventId;
    }

    public void setEventId(int eventId) {
        EventId = eventId;
    }
}
