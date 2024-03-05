package com.example.checkin;

import android.media.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Attendee implements User {
    //TODO:
    //      - profile picture adding
    //      - profile picture removing
    //      - deterministic profile picture generation
    //      - current geolocation
    //      - listener for receiving notifications
    //      - FIREBASE INTEGRATION
    //      - unique UserID generation

    private int userId;     //the user's ID
    private Image profilePicture;               //TODO: the user's profile picture

    private EventList CheckInHistory = new EventList();      //array containing the user's check-in history

    private boolean geoTracking;

    //private Location location;                //TODO: user's current location

    //Optional information the user can provide
    private String name;
    private String homepage;        //user's website?
    private String email;
    private String phoneNumber;

    /**
     * Generates a new unique identifier for the user
     *
     * @return their assigned id.
     */


    /**
     * Generates a new Attendee
     */
    public Attendee() {
        this.userId = generateUserId();

        //test
        Random rand = new Random();
        this.userId = rand.nextInt(50);


        //this.profilePicture = generateProfilePicture(); //TODO:
        this.name = "";
        this.homepage = "";
        this.email = "";
        this.phoneNumber = "";
        this.geoTracking = true;                   //on by default
    }

    public Attendee(int id, String n, String home, String mail, String phone, boolean tracking){
        this.userId = id;
        this.name = n;
        this.homepage = home;
        this.email = mail;
        this.phoneNumber = phone;
        this.geoTracking = tracking;
    }

    private int generateUserId() {
        //TODO: Generate the userId for a new user
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user
        return 1;
    }

    //TODO: Deterministic generation of a user's profile picture
    //private Image generateProfilePicture(){
    //
    //    return image;
    //}

    //TODO: Profile picture removal
    //private void removeProfilePicture(){
    //    this.profilePicture = generateProfilePicture();
    //}

    //Event subscription===========================================================================

    /**
     * Subscribes a user to an event, consenting to receive notifications
     *
     * @param event a valid event object
     */
    public void EventSub(Event event) {
        //User subscribes to event, consents to receive notifications
        event.userSubs(this);
    }

    /**
     * Unsubscribes the user from an event
     *
     * @param event a valid event object
     */
    public void EventUnSub(Event event) {
        //User unsubscribes from event
        event.userUnSubs(this);
    }

    //CheckedInList=================================================================================

    /**
     * Returns the list of events that the user is checked in to. May or may not be needed but I
     * added it to start off with.
     *
     * @return returns CheckInHistory
     */
    public EventList getCheckIns() {
        //get the list of user check-in/outs
        //possibly not necessary
        return CheckInHistory;
    }

    /**
     * Checks a user into an event, adds that event to CheckInHistory
     *
     * @param event an event object
     */
    public void CheckIn(Event event) {
        //check a user in/out of an event
        CheckInHistory.addEvent(event);
    }

    /**
     * Check if the user is checked into the event
     *
     * @param event an event object
     * @return returns true or false
     */
    public boolean IsCheckedIn(Event event) {
        //is passed an event and returns whether that user is checked into
        //the event or not
        if (event.IsCheckedIn(this)) {
            return true;
        }
        return false;
    }

    //GEOLOCATION===========================================================

    /**
     * Toggles the user's geolocation tracking settings
     */
    public void toggleTracking() {
        if (geoTracking) {
            this.geoTracking = false;
            return;
        }
        this.geoTracking = true;
        return;
    }

    /**
     * Check if the user has tracking enabled
     *
     * @return returns their tracking status
     */
    public boolean trackingEnabled() {
        return geoTracking;
    }

    //TODO: Location tracking
    //public Location userLocation(){
    //}


    //Variables=================================================

    @Override
    //get the user's ID
    public int getUserId() {
        return this.userId;
    }

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
