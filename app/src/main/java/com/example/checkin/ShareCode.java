package com.example.checkin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

// generates QR code and shares it to the other apps
public class ShareCode extends Fragment {
    Event myevent;
    Button sharebutton;
    Button backbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_code, container, false);
        backbutton = view.findViewById(R.id.backbtn);

        // move back to previous fragment
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Button btnGenerate = view.findViewById(R.id.btnGenerate);
        // EditText etText = view.findViewById(R.id.etText);

        // ----- Generate QR Code -------
        ImageView imageCode = view.findViewById(R.id.imageCode);

        Bundle bundle = this.getArguments();
        assert bundle != null;
        myevent = (Event) bundle.getSerializable("event");

        generateQRCode(myevent, imageCode);



        // ------- Sharing QR Code --------
        sharebutton = view.findViewById(R.id.sharebtn);
        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapdrawable = (BitmapDrawable) imageCode.getDrawable();
                Bitmap bitmap = bitmapdrawable.getBitmap();
                shareImage(bitmap, requireContext());
            }
        });

        return view;
    }

    /**
     * Shares generated qr code image to other apps
     * @param bitmap
     * @param context
     */

    // URL: https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
    private void shareImage(Bitmap bitmap, Context context) {
        Uri uri = getImageShare(bitmap, context);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing QR Code" );
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    /**
     *
     * @param bitmap
     * @param context
     * @return
     */

    // URL: https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
    private Uri getImageShare(Bitmap bitmap, Context context) {

        File images = new File(context.getCacheDir(), "app_images");
        Uri uri = null;

        try {
            images.mkdirs();
            File file = new File(images, "image.png");
            FileOutputStream fileoutput = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileoutput);
            fileoutput.flush();
            fileoutput.close();

            uri = FileProvider.getUriForFile(context, "com.example.checkin", file);
        }
        catch (Exception e){
            System.out.println("Error");
        }
        return uri;

    }

    /**
     * Generates Qr code
     * @param myevent
     * @param imageCode
     */
    public void generateQRCode(Event myevent, ImageView imageCode){
        String myText = myevent.getEventname();

        // use event id instead -> to retrieve event from firebase?
        // String myText = myevent.getEventId();
       // String myText = myevent.getEventId();

        // Appending timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        myText += "_" + timestamp;

        // Appending user's ID
        String userid = "123456"; // Change 123456 to user's ID
        myText += "_" + userid;

        // Initializing MultiFormatWriter for QR code

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            //https://stackoverflow.com/questions/51917881/zxing-android-qrcode-generator
            // BitMatrix class to encode entered text and set Width & Height
            BitMatrix matrix = writer.encode(myText, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(matrix); // Creating bitmap of code
            imageCode.setImageBitmap(mBitmap); // Setting generated QR code to imageView

            // To hide the keyboard
            InputMethodManager manager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(imageCode.getApplicationWindowToken(), 0);
        } catch (WriterException e) {
            e.printStackTrace();
        }



    }
}