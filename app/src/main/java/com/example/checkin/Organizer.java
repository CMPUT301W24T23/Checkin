package com.example.checkin;

import java.util.ArrayList;

public class Organizer implements User{
    private int userId;
    private EventList CreatedEvents = new EventList();     //events this user has created
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

    private int generateUserId() {
        //TODO: Generate the userId for a new user
        //      Integration with firebase needed in order to have unique IDs
        //      idea: increment from zero, check if ID is in use, when
        //            vacant ID is found, assign that to this user
        return 1;
    }

    /**
     * Generates a new Organizer
     */
    public Organizer() {
        this.userId = generateUserId();
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
    public int getUserId() {
        return this.userId;
    }

    @Override
    public void setUserId(int id) {
        this.userId = id;
    }
}