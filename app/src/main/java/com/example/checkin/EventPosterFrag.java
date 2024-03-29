package com.example.checkin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Displays the Event's poster and allows sharing it with other apps
 * Code mostly copied from the ShareCode fragment
 */
public class EventPosterFrag extends Fragment {
    Button backbutton;
    Button sharebutton;
    UserImage poster = new UserImage();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_poster, container, false);
        ImageView imagePoster = view.findViewById(R.id.imagePoster);

        //Retrieve Poster and display
        Bundle bundle = this.getArguments();
        assert bundle != null;
        poster = (UserImage) bundle.getSerializable("Poster");
        assert poster != null;
        imagePoster.setImageBitmap(poster.imageBitmap());

        //Share the poster
        sharebutton = view.findViewById(R.id.sharebtn);
        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapdrawable = (BitmapDrawable) imagePoster.getDrawable();
                Bitmap bitmap = bitmapdrawable.getBitmap();
                shareImage(bitmap, requireContext());
            }
        });

        //Return
        backbutton = view.findViewById(R.id.backbtn);
        // move back to previous fragment
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
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
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Poster" );
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

}