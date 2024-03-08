package com.example.checkin;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class UserProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView myImageView;
    private Uri imageUri;
    private Attendee currentUser;

    // Other UI elements
    private EditText nameEdit, emailEdit, homeEdit, countryEdit;
    private CheckBox locationBox;

    public UserProfileFragment() {
        currentUser = new Attendee("New Name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        myImageView = view.findViewById(R.id.myImageView);
        Button editPictureButton = view.findViewById(R.id.editPictureButton);
        editPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Initialize other UI elements
        nameEdit = view.findViewById(R.id.nameEdit);
        emailEdit = view.findViewById(R.id.emailEdit);
        homeEdit = view.findViewById(R.id.homeEdit);
        countryEdit = view.findViewById(R.id.countryEdit);
        locationBox = view.findViewById(R.id.locationBox);

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                myImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserProfile() {
        // Get user-entered information
        String name = nameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String homepage = homeEdit.getText().toString();
        String country = countryEdit.getText().toString();
        boolean locationPermission = locationBox.isChecked();

        // Check if image is set, if not, generate image with initials
        Bitmap profileImage;
        if (imageUri != null) {
            try {
                profileImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                profileImage = generateImageWithInitials(name);
            }
        } else {
            profileImage = generateImageWithInitials(name);
        }

        currentUser.updateProfile(name, email, homepage, country, locationPermission);

        // Display the generated image on the ImageView
        myImageView.setImageBitmap(profileImage);

        // You can save this information or pass it to another fragment for display
        // For now, let's display a toast message with the information
        String message = "Name: " + name + "\nEmail: " + email + "\nHomepage: " + homepage +
                "\nCountry: " + country + "\nLocation Permission: " + locationPermission;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap generateImageWithInitials(String name) {
        // Generate an image with the initials of the name
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(String.valueOf(name.charAt(0)), xPos, yPos, paint);
        return bitmap;
    }
}
