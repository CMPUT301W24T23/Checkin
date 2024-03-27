package com.example.checkin;

import android.media.Image;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/*
This Java class, named `Attendee`, represents a user participating in an event-check-in system.
The class implements the `User` interface and is serializable.
It includes features for managing user profiles, event subscriptions, check-ins,
geolocation tracking, and potential integration with Firebase.
The class also outlines methods for updating profile information,
subscribing/unsubscribing from events, checking in/out from events,
toggling geolocation tracking, and retrieving user details.
Overall, the class serves as a foundation for handling user-related functionalities
in an event management application.
 */
public class Attendee implements User, Serializable {
    //TODO:
    //      - profile picture adding
    //      - profile picture removing
    //      - deterministic profile picture generation
    //      - current geolocation
    //      - listener for receiving notifications
    //      - FIREBASE INTEGRATION

    private String userId;     //the user's ID

    //private Image profilePicture;               //TODO: the user's profile picture
    private String profilePicture;              //user's profile picture as an encoded 64bit string

    private Map<String, Long> CheckInHist = new Hashtable<>();

    private boolean geoTracking;

    //private Location location;                //TODO: user's current location

    //Optional information the user can provide
    private String name;
    private String homepage;        //user's website?
    private String email;
    private String phoneNumber;
    private String country;



    public Attendee(String name) {
        this.name = name;
    }

    /**
     * Updates the profile information according to the parameters.
     * @param geoTracking if user allows its geolocation tracking or not
     * @param name name of the user
     * @param homepage homepage representing as a other form of profile of the user
     * @param email email associated to that user
     */
    public void updateProfile(String name, String email, String homepage, String country, boolean geoTracking ) {
        this.name = name;
        this.homepage = homepage;
        this.email = email;
        this.country = country;
        this.geoTracking = geoTracking;
    }

    /* Empty constructor for attendee
     */
    public Attendee() {
        this.userId = String.valueOf(generateUserId());
        this.name = "";
        this.homepage = "";
        this.email = "";
        this.phoneNumber = "";
        this.geoTracking = true;        //on by default
        this.profilePicture = "";           //Generate a new profile picture
        this.CheckInHist = new Hashtable<>();
    }

    /**
     * Generates a new attendee with preset data
     * @param id
     * user's id identifier
     * @param n
     * user's name
     * @param home
     * user's homepage
     * @param mail
     * user's email
     * @param phone
     * user's phone number
     * @param tracking
     * user's tracking permission setting
     */
    public Attendee(String id, String n, String home, String mail, String phone, boolean tracking){
        this.userId = id;
        this.name = n;
        this.homepage = home;
        this.email = mail;
        this.phoneNumber = phone;
        this.geoTracking = tracking;
        this.CheckInHist = new Hashtable<>();
    }

    /**
     * Generates a new unique identifier for the user
     *
     * @return their assigned id.
     */
    private String generateUserId() {
        //Random ID for the empty constructor
        Random rand = new Random();
        return Integer.toString(rand.nextInt(1000));
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

    /** Return the dictionary with the keys as the eventIds and values of number
     * of checkins.
     * @return
     * dictionary of check in counts
     */
    public Map<String, Long> getCheckIns() {
        //get the list of user check-in/outs
        //possibly not necessary
        return CheckInHist;
        //edit to return dictionary
    }

    /**
     * Updates the user's check in count so that their number of check ins for each event
     * can be calculated
     * @param event
     * an event object
     */
    public void CheckIn(Event event) {
        //increment user check in count
        if (this.CheckInHist.isEmpty()) {
            // If the CheckInHist map is empty, initialize the count to 1
            CheckInHist.put(String.valueOf(event.getEventId()), 1L);
        } else {
            // If the map is not empty, retrieve the current count and increment it by 1
            Long checkInCount = CheckInHist.get(String.valueOf(event.getEventId()));
            if (checkInCount != null) {
                long count = checkInCount + 1;
                CheckInHist.put(String.valueOf(event.getEventId()), count);
            } else {
                // If the value for the event ID is null, initialize it to 1
                CheckInHist.put(String.valueOf(event.getEventId()), 1L);
            }
        }
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
        return event.IsCheckedIn(this);

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
    public String getUserId() {
        return this.userId;
    }
    @Override
    //set the user's ID
    public void setUserId(String userId) {
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
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setCheckInHist(Map<String, Long> checkInHist) {
        CheckInHist = checkInHist;
    }
}