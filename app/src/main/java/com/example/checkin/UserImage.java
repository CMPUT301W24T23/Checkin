package com.example.checkin;

/**
 * A class for the purpose of being used to retrieve user image data from firebase and
 * allowing for the user's id to remain attached to the information
 *
 * Used when retrieving profile pictures and posters from firebase
 */
public class UserImage {
    private String imageB64;        //the image in base64
    private String ID;          //the ID of the user or event using this image

    public UserImage(){
        imageB64 = "";
        ID = "";
    }

    public void setImageB64(String imageB64) {
        this.imageB64 = imageB64;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageB64() {
        return imageB64;
    }

    public String getID() {
        return ID;
    }
}
