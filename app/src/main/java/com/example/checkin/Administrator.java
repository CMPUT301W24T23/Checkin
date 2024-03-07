package com.example.checkin;

import android.media.Image;

import java.util.ArrayList;

public class Administrator {

    /**
     * Returns a list of all images, including posters and profile pictures
     * @return  An array of type Images.
     */

    public ArrayList<Image> getAllImages() {
        ArrayList<Image> allImages = new ArrayList<>();

        // Initialize ImageList (you might pass it through constructor or other methods)
        ImageList imageList = new ImageList();

        // Get the list of posters
        allImages.addAll(imageList.getPosters());

        // Get the list of profile pictures
        allImages.addAll(imageList.getProfilePictures());

        return allImages;
    }
}
