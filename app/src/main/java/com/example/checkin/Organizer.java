package com.example.checkin;

import java.util.ArrayList;
import java.util.UUID;

public class Organizer implements User{
    private String userId;
    private ArrayList<String> CreatedEvents = new ArrayList<>(); //event ids of events this organizer has created
    private boolean geoTracking;

    //private QRCodeList QRCodes;       //the qr codes this organizer has generated
    //private ImageList images;         //posters uploaded by this organizer

    //TODO:     Location
    //          Milestone listener
    //          Add QR code
    //          Remove QR code
    //          add event posters
    //          remove event posters
    //          has: created events
    //          UserID generation, firebase integration

    //TODO:
    //public addQR(){}              //organizer generates a QR Code
    //TODO:
    //public deleteQR(){}           //organizer deletes one of their QR Codes

    private String generateUserId() {
        //TODO: Generate the userId for a new user
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a new Organizer
     */
    public Organizer() {
        this.userId = generateUserId();
    }

    public Organizer(String oID) {
        this.userId = oID;
        this.geoTracking = true;
    }

    /*This user has created an event, add to list of event ids and upload to firebase*/
    public void EventCreate(Event e){
        CreatedEvents.add(e.getEventId());
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
}
