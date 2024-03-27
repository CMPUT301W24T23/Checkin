/*
A class managing event details, attendee subscriptions, and check-ins in an event-check-in system.
It includes methods for subscribing/unsubscribing attendees for notifications, checking them in/out,
and checking subscription and check-in status.
The class supports QR code and poster management.
 */
package com.example.checkin;

import android.media.Image;

import com.example.checkin.AttendeeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Event implements Serializable {
    //TODO:
    //      - assign QR CODE
    //      - remove QR CODE
    //      - assign poster
    //      - remove poster
    //      - Geolocation integration
    //              - has: physical boundaries? i'm not sure how geolocation would work
    //      - Firebase Integration

    private String EventId;//unique identifier for event
    //private Image poster;       //event poster
    private String poster;        //Poster uploaded to this Event
    //private QRCode code;
    private String eventname;
    private String eventdetails;
    private AttendeeList Subscribers = new AttendeeList();
    //Notation: "Subscribers" refers attendees who
    //are 'subscribed' to receive event notifications
    private String creator;     //The organizer who created this event

    private AttendeeList CheckInList = new AttendeeList();
    //attendees CURRENTLY checked in to the event

    public Event(String eventname, String eventdetails, ArrayList<Attendee> checkInList) {
        this.eventname = eventname;
        this.eventdetails = eventdetails;
        checkInList = new ArrayList<>();
    }

    /*
    public Event(String eventname, String eventdetails) {
        this.eventname = eventname;
        this.eventdetails = eventdetails;
    }
    */


    //TODO: ID generation
    private String generateEventId(){
        Random rand = new Random();
        return Integer.toString(rand.nextInt(1000));
    }

    /*
    public Event() {
        this.eventname = "";
        this.eventdetails = "";
        this.creator = "";
        this.EventId = generateEventId();
        this.Subscribers = new AttendeeList();
        this.CheckInList = new AttendeeList();
        this.poster = "";
    }*/

    /**
     * Creates an event, requires a name and a creator at bare minimum
     * @param name
     */
    public Event(String name, String creatorID){
        this.eventname = name;
        this.eventdetails = "";
        this.creator = creatorID;
        this.EventId = generateEventId();       //TODO: Generate event ID (CreatorID + Year + Month + Day + Minute + Second)
        this.Subscribers = new AttendeeList();
        this.CheckInList = new AttendeeList();
        this.poster = "";
    }

    /**
     * Restore from id
     * @param id
     * the identifier for this event
     */
    public Event(String id) {
        this.EventId = id;
        this.eventname = "";
        this.eventdetails = "";
        this.creator = "";
        this.Subscribers = new AttendeeList();
        this.CheckInList = new AttendeeList();
        this.poster = "";
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

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}