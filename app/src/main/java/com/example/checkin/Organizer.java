
package com.example.checkin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * A class representing an organizer in an event-check-in system.
 * It manages created events, allows toggling of geolocation tracking, and retrieves user details.
 * The class supports event creation and geolocation preferences.
 * In the main application, organizers can create and manage events seamlessly.
 */
public class Organizer implements User, Serializable {
    private String userId;
    private boolean IsAdmin;
    private boolean geoTracking;
    private ArrayList<String> CreatedEvents = new ArrayList<>(); //event ids of events this organizer has created
    private ArrayList<String> QRCodes = new ArrayList<>(); //encoded qr codes created by this organizer

    //TODO:     Location

    /**
     * Generate a temporary ID for the generation of the class object
     * @return
     */
    private String generateTempId() {
        Random rand = new Random();
        return Integer.toString(rand.nextInt(1000));
    }

    /**
     * Generates a new Organizer
     */
    public Organizer() {
        this.userId = generateTempId();
        this.geoTracking = true;
        this.IsAdmin = false;
        this.CreatedEvents = new ArrayList<>();
        this.QRCodes = new ArrayList<>();

    }

    /**
     * Generates organizer from oID
     * @param oID
     * a user's organizer ID
     */
    public Organizer(String oID) {
        this.userId = oID;
        this.geoTracking = true;
        //TODO:         loading event list from firebase
    }

    /*This user has created an event, add to list of event ids*/
    public void EventCreate(String e){
        CreatedEvents.add(e);
    }

    public void QRCreate(String qr) {
        QRCodes.add(qr);
    }

    //GEOLOCATION===================================================================================

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


    //Variables=====================================================================================

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public void setUserId(String id) {
        this.userId = id;
    }

    public ArrayList<String> getCreatedEvents() {
        return CreatedEvents;
    }

    public boolean isAdmin() {
        return IsAdmin;
    }

    public void setAdmin(boolean admin) {
        IsAdmin = admin;
    }

    public ArrayList<String> getQRCodes() {
        return QRCodes;
    }


}

