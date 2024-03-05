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
    //      - Geolocation integration
    //              - has: physical boundaries? i'm not sure how geolocation would work
    //      - Firebase Integration

    private int EventId;        //unique identifier for event
    private Image poster;       //event poster
    //private QRCode code;
    private AttendeeList Subscribers = new AttendeeList();
                                                //Notation: "Subscribers" refers attendees who
                                                  //are 'subscribed' to receive event notifications
                                                  //TODO: Firebase Integration
    private AttendeeList CheckInList = new AttendeeList();
                            //attendees CURRENTLY checked in to the event TODO: Firebase Integration

    private int generateEventId(){
        //TODO: Generate the EventId for a new event
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user
        return 1;
    }

    /**
     * Creates a new Event
     */
    public Event() {
        this.EventId = generateEventId();
    }

    public Event(int id) {
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
     */
    public void userCheckIn (Attendee a){
        if (CheckInList.contains(a)){
            //if in list, the user is checking out of the event
            a.CheckIn(this);
            CheckInList.removeAttendee(a);
        } else{
            //otherwise the user is checking in
            a.CheckIn(this);
            CheckInList.addAttendee(a);
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

    public int getEventId() {
        return EventId;
    }

    public void setEventId(int eventId) {
        EventId = eventId;
    }
}