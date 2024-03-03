package com.example.checkin;


import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;


import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    //TODO:
    //      - assign QR CODE
    //      - remove QR CODE
    //      - assign poster
    //      - remove poster
    //      - event ID generation
    //      - Firebase Integration

    private int EventId;        //unique identifier for event
    private Image poster;

    private String eventname;

    private String eventdetails;

    public Image getPoster() {
        return poster;
    }

    public String getEventname() {
        return eventname;
    }

    public String getEventdetails() {
        return eventdetails;
    }

//private QRCode code;

    private ArrayList<Attendee> Attendees;      //list of attendees subscribed to the event TODO: Firebase Integration
    private ArrayList<Attendee> CheckInList;



    //attendees CURRENTLY checked in to the event TODO: Firebase Integration

    private int eventqrcodeid;

    public int getEventqrcodeid() {
        return eventqrcodeid;
    }

    public void setEventqrcodeid(int eventqrcodeid) {
        this.eventqrcodeid = eventqrcodeid;
    }

    public void setCheckInList(ArrayList<Attendee> checkInList) {
        CheckInList = checkInList;
    }

    public ArrayList<Attendee> getCheckInList() {
        return CheckInList;
    }

    public Event(String eventname, String eventdetails, ArrayList<Attendee> checkInList) {
        this.eventname = eventname;
        this.eventdetails = eventdetails;
        checkInList = new ArrayList<>();
    }

    public Event(String eventname, String eventdetails) {
        this.eventname = eventname;
        this.eventdetails = eventdetails;
    }

    public void assignQrCode(){

    }

    ArrayList events = new ArrayList<>();
    private boolean useridInuse(int eventid){

        boolean inuse = false;

        if (events.contains(eventid)){
            return true;
        }
        return inuse;

    }

    private int generateEventId(){
        //TODO: Generate the EventId for a new event
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user

        int eventid = 0;

        while (useridInuse(eventid)){
            eventid = eventid+1;
        }
        events.add(eventid);

        return eventid;

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

        if (CheckInList == null) {
            CheckInList = new ArrayList<>();
        }

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


    static Event newInstance (Event event){
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        return event;
    }
}

