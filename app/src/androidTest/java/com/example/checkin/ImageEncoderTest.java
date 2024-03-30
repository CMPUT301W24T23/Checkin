package com.example.checkin;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.junit.Test;

/**
 * Test whether image base64 encoding is successful
 * must be done in instrumented tests since the bitmap code relies on the android framework
 */
public class ImageEncoderTest {
    private Bitmap generateImage(){
        String name = "M";
        // Generate an image with the initials of the name
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Log.d("BitmapSize", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setTextSize(40);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);            //set text color
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(String.valueOf(name.charAt(0)), xPos - 1, yPos, paint);

        return bitmap;
    }

    @Test
    public void testEncoder(){
        ImageEncoder encoder = new ImageEncoder();

        Bitmap image = generateImage();

        //Encode the image into Base64
        String imageBase64 = encoder.BitmapToBase64(image);


        // Decode the Base64 string back to a Bitmap for checking
        Bitmap decodedBitmap = encoder.base64ToBitmap(imageBase64);

        assertTrue(image.sameAs(decodedBitmap));
    }

    @Test
    public void testSymbolReplace(){
        ImageEncoder encoder = new ImageEncoder();
        Bitmap image = generateImage();

        //Encode to Base64
        String imageBase64 = encoder.BitmapToBase64(image);
        //Firebase can't have '/' in document names so it has to be replaced
        imageBase64 = imageBase64.replace("/", "~");
        imageBase64 = imageBase64.replace("~", "/");
        Bitmap decodedBitmap = encoder.base64ToBitmap(imageBase64);
        assertTrue(image.sameAs(decodedBitmap));
    }
}