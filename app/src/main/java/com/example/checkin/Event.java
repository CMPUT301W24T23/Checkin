/*
A class managing event details, attendee subscriptions, and check-ins in an event-check-in system.
It includes methods for subscribing/unsubscribing attendees for notifications, checking them in/out,
and checking subscription and check-in status.
The class supports QR code and poster management.
 */
package com.example.checkin;

import android.media.Image;
import android.util.Log;

import com.example.checkin.AttendeeList;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

/**
 * This class outlines an Event object that stores all the information needed for an event
 */
public class Event implements Serializable {
    //TODO:
    //      - Geolocation integration

    private String EventId;//unique identifier for event
    private String poster;        //Poster uploaded to this Event
    //private QRCode code;
    private String eventname;

    private Map<String, String> CheckInsId;

    private String qrcodeid;
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

    }

    /**
     * Generates a unique event ID for the event
     * @param creatorID
     * The User ID of the organizer creating this event
     * @return
     * appends the user ID to the current timestamp
     */
    private String generateEventId(String creatorID){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String eventID = String.format(creatorID + timestamp);
        //Log.d("EventID Generate", String.format("Event ID (%s)", eventID));
        return eventID;
    }

    /**
     * Creates an event, requires a name and a creator at bare minimum
     * @param name
     */
    public Event(String name, String creatorID){
        this.eventname = name;
        this.eventdetails = "";
        this.creator = creatorID;
        this.EventId = generateEventId(creatorID);
        this.Subscribers = new AttendeeList();
        this.CheckInList = new AttendeeList();
        this.poster = "";
        this.CheckInsId = new Hashtable<>();
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

    //Subscription=============================================================

    /**
     * Subscribes a user to the event to mark them as opting in to related notifications
     * @param a
     * a valid Attendee object
     */
    /*
    public void userSubs(Attendee a){
        //Attendee subscribes to event
        Subscribers.addAttendee(a);
    }*/

    public void userSubs (Attendee a){
        if(Subscribers.contains(a)){
            //Check the user out
            Subscribers.removeAttendee(a);
            System.out.println("REMOVE");
        } else{
            //Check the user in
            Subscribers.addAttendee(a);
        }
    }

    /**
     * Unsubscribes a user to the event to mark them as opting out to related notifications
     * @param a
     * a valid Attendee object
     */
    /*
    public void userUnSubs (Attendee a){
        //Attendee unsubscribes to event
        Subscribers.removeAttendee(a);
    }
    */

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
        if(CheckInList.contains(a)){
            //Check the user out
            CheckInList.removeAttendee(a);
            System.out.println("REMOVE");
        } else{
            //Check the user in
            CheckInList.addAttendee(a);
            a.CheckIn(this);
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
     * Attendee List of checked in users
     */

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

    public String getQrcodeid() {
        return qrcodeid;
    }

    public void setQrcodeid(String qrcodeid) {
        this.qrcodeid = qrcodeid;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public AttendeeList getCheckInList() {
        return CheckInList;
    }

    public Map<String, String> getCheckInsId() {
        return CheckInsId;
    }

    public void setCheckInsId(Map<String, String> checkInsId) {
        CheckInsId = checkInsId;
    }
    // String attendeeId = a.getUserId();
    //
    //        if (CheckInsId == null) {
    //           CheckInsId = new HashMap<>();
    //        }
    //
    //       else if (CheckInsId.containsKey(attendeeId)) {
    //            // Attendee is already checked in, remove the
    //            CheckInList.removeAttendee(a);
    //            CheckInsId.remove(attendeeId);
    //            System.out.println("Attendee " + attendeeId + " removed from check-in list");
    //        } else {
    //            // Attendee is not checked in, add them
    //            CheckInList.addAttendee(a);
    //            CheckInsId.put(attendeeId, "checked-in");
    //            System.out.println("Attendee " + attendeeId + " added to check-in list");
    //        }
}
