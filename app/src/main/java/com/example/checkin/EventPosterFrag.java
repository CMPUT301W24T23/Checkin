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



}