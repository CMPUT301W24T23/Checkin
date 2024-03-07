package com.example.checkin;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    //TODO:
    //      - assign QR CODE
    //      - remove QR CODE
    //      - assign poster
    //      - remove poster
    //      - event ID generation
    //      - Geolocation integration
    //              - has: physical boundaries? i'm not sure how geolocation would work
    //      - Firebase Integration

    private String EventId;//unique identifier for event
    private Image poster;       //event poster
    //private QRCode code;
    private String eventname;

    private String eventdetails;
    private AttendeeList Subscribers = new AttendeeList();
    //Notation: "Subscribers" refers attendees who
    //are 'subscribed' to receive event notifications
    //TODO: Firebase Integration
    private AttendeeList CheckInList = new AttendeeList();
    //attendees CURRENTLY checked in to the event TODO: Firebase Integration

    public Event(String eventname, String eventdetails, ArrayList<Attendee> checkInList) {
        this.eventname = eventname;
        this.eventdetails = eventdetails;
        checkInList = new ArrayList<>();
    }



    public Event(String eventname, String eventdetails) {
        this.eventname = eventname;
        this.eventdetails = eventdetails;
    }

    private String generateEventId(){
        //TODO: Generate the EventId for a new event
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user
        return "test1";
    }

    /**
     * Creates a new Event
     */
    public Event() {
        this.EventId = generateEventId();
    }

    /**
     * Restore from id
     * @param id
     * the identifier for this event
     */
    public Event(String id) {
        this.EventId = id;
    }
    //Poster Image===============================================================

    //pri

    //QR CODE=====================================================================

    //TODO: Adding QR Code
    //public void addQRCode(QRCode qr){}

    //TODO: Removing QR Code
    //public void removeQRCODE(){}

    //Subscription=============================================================

    /**
     * Subscribes a user to the event to mark them as opting in to related notifications
     * @param a
     * a valid Attendee object
     */
    public void userSubs(Attendee a){
        //Attendee subscribes to receiving information updates
        Subscribers.addAttendee(a);
        Database db = new Database();
        db.updateEvent(this);
    }

    /**
     * Unsubscribes a user to the event to mark them as opting out to related notifications
     * @param a
     * a valid Attendee object
     */
    public void userUnSubs (Attendee a){
        //Attendee unsubscribes to receiving information updates
        Subscribers.removeAttendee(a);
    }

    /**
     * Check if a user is subscribed to the event
     * @param a
     * a valid Attendee object
     * @return
     * returns whether user is subscribed
     */
    public boolean IsSubscribed(Attendee a){
        for (Attendee user: Subscribers.getAttendees()){
            if (user == a){
                return true;
            }
        }
        return false;
    }

    //Attendee checkin==========================================================

    /**
     * Checks a user a into the event
     * @param a
     * a valid attendee object
     */
    public void userCheckIn (Attendee a){

        if (CheckInList == null) {
            CheckInList = new AttendeeList();
        }



        if (CheckInList.contains(a)){
            //if in list, the user is checking out of the event
            a.CheckIn(this);
            CheckInList.removeAttendee(a);
            Database db = new Database();
            db.updateEvent(this);
        } else{
            //otherwise the user is checking in
            a.CheckIn(this);
            CheckInList.addAttendee(a);
            Database db = new Database();
            db.updateEvent(this);
        }


    }
    /**
     * Check if the attendee is checked in
     * @param a
     * a valid Attendee object
     * @return
     * returns true or false
     */
    public boolean IsCheckedIn(Attendee a){
        for (Attendee user: CheckInList.getAttendees()){
            if (user == a){
                return true;
            }
        }
        return false;
    }

    //GETTER/SETTER=====================================================================

    /**
     * Return the array of attendees who are subscribed to the event
     * @return
     * Attendee List of subscribers
     */
    public AttendeeList getSubscribers() {
        return Subscribers;
    }

    /**
     * Return the array of attendees who are currently checked in to the event
     * @return
     */
    public AttendeeList getCheckInList() {
        return CheckInList;
    }

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getEventdetails() {
        return eventdetails;
    }

    public void setEventdetails(String eventdetails) {
        this.eventdetails = eventdetails;
    }

    public void setCheckInList(AttendeeList checkInList) {
        CheckInList = checkInList;
    }


}
