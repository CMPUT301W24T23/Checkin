package com.example.checkin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Class to be used for encoding images into base64 and decoding base64 back into strings
 */
public class ImageEncoder {
    public ImageEncoder(){};

    /**
     * Takes a base64 String and converts it back into a bitmap
     * @param base64String
     * A string containing the encoded base64 information of an image
     * @return
     * Returns the bitmap encoded in the base64 string
     */
    public Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Takes a bitmap of an image and converts it into a Base64 string
     * @param bitmap
     * the bitmap of an image
     * @return
     * Returns the encoded base64 string of a bitmap
     */
    public String BitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
