package com.example.checkin;

import android.media.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Attendee implements User{
    //TODO:
    //      - profile picture adding
    //      - profile picture removing
    //      - deterministic profile picture generation
    //      - unique UserID generation
    //      - geolocation tracking toggle
    //      - current geolocation
    //      - listener for receiving notifications
    //      - FIREBASE INTEGRATION

    private int userId;     //the user's ID
    private Image profilePicture;       //TODO: the user's profile picture

    private ArrayList<Event> CheckInList;      //array containing the user's check-in history

    private ArrayList<Event> subscribedEvents;  //events that the user is subscribed to and
                                                //can potentially receive notifications from

    private boolean geoTracking;                //TODO: enable disabled geotracking

    //private Location location;                //TODO: user's current location

    //Optional information the user can provide
    private String name;
    private String homepage;        //user's website?
    private String email;
    private String phoneNumber;

    /**
     * Generates a new unique identifier for the user
     * @return
     * their assigned id.
     */
    private int generateUserId(){
        //TODO: Generate the userId for a new user
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user
        return 1;
    }

    //private Image generateProfilePicture(){
    //    //TODO: Deterministic generation of a user's profile picture
    //    return image;
    //}

    //TODO:
    //private void removeProfilePicture(){
    //    this.profilePicture = generateProfilePicture();
    //}

    /**
     * Generates a new Attendee
     */
    public Attendee() {
        this.userId = generateUserId();
        //this.profilePicture = generateProfilePicture(); //TODO:
    }


    //Event subscription===========================================================================

    /**
     * Subscribes a user to an event, consenting to receive notifications
     * @param event
     * a valid event object
     */
    public void EventSub (Event event){
        //User subscribes to event, consents to receive notifications
        event.userSubs(this);
    }

    /**
     * Unsubscribes the user from an event
     * @param event
     * a valid event object
     */
    public void EventUnSub (Event event){
        //User unsubscribes from event
        event.userUnSubs(this);
    }

    //CheckedInList=================================================================================
    /**
     * Returns the list of events that the user is checked in to. May or may not be needed but I
     * added it to start off with.
     * @return
     * returns CheckInList
     */
    public ArrayList<Event> getCheckIns(){
        //get the list of user check-in/outs
        //possibly not necessary
        return CheckInList;
    }

    /**
     * Checks a user into an event, adds that event to CheckInList
     * @param event
     * an event object
     */
    public void CheckIn(Event event){
        //check a user in/out of an event
        CheckInList.add(event);
    }

    /**
     * Method that determines whether the user is checked in to an event by checking the number of
     * occurrences in CheckInList. The user being checked in is indicated by the number of scans of
     * an event being odd. The user is checked out of the number of scans is zero or even.
     * @param event
     * an event object
     * @return
     * returns true or false
     */
    public boolean IsCheckedIn(Event event){
        //is passed an event and returns whether that user is checked into
        //the event or not
        int occurrences = Collections.frequency(CheckInList, event);

        if (occurrences % 2 == 0){
            //if the number of occurrences is even (or zero), IE. the attendee has checked in and out
            //of the event, then the attendee is not checked in
            return false;
        } else{
            //otherwise the attendee is checked in
            return true;
        }
    }


    //Variable getters and setters==========================================

    @Override
    //get the user's ID
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the userId
     * @param userId
     * an integer
     */
    @Override
    //set the user's ID
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
