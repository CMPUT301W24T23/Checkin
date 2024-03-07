package com.example.checkin;// Administrator class

import android.media.Image;

import com.example.checkin.ImageList;

import java.util.ArrayList;

public class Administrator {

    /**
     * Returns a list of all users' profile pictures used in the app.
     * @return An array of type Images containing profile pictures.
     */
    public ArrayList<Image> getUsersProfilePictures() {
        // Initialize ImageList.
        ImageList imageList = new ImageList();

        // Get the list of profile pictures.
        return imageList.getProfilePictures();
    }

    /**
     * Returns a list of all posters used in the app.
     * @return An array of type Images containing posters.
     */
    public ArrayList<Image> getPosters() {
        // Initialize ImageList.
        ImageList imageList = new ImageList();

        // Get the list of posters.
        return imageList.getPosters();
    }
}
