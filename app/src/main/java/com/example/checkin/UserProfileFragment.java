package com.example.checkin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.checkin.Attendee;

import java.io.IOException;
import java.util.regex.Pattern;

/*
Fragment displaying the user profile (using fragment_user_profile.xml layout)
including profile pic, name and other information along with the functionality of
generating, uploading profile pic based on the name initials and changing attendee information.
*/

public class UserProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView myImageView;
    private Uri imageUri;
    //private UserProfileViewModel viewModel;

    // Instance of the an attendee.
    private Attendee currentUser = new Attendee();

    // Other UI elements
    private EditText nameEdit, emailEdit, homeEdit, countryEdit;
    private CheckBox locationBox;

    public UserProfileFragment() {
        // Required empty public constructor
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

        // Setting the contact information of the current user to the xml layout.
        nameEdit.setText(currentUser.getName());
        emailEdit.setText(currentUser.getEmail());
        homeEdit.setText(currentUser.getHomepage());
        countryEdit.setText(currentUser.getCountry());
        locationBox.setChecked(currentUser.trackingEnabled());


        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        Button removePictureButton = view.findViewById(R.id.removePictureButton);
        removePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemovePictureButtonClick(v);
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

                // Check if the image is loaded into the ImageView
                if (myImageView.getDrawable() != null && myImageView.getVisibility() == View.VISIBLE) {
                    Log.d("ImageViewVisibility", "Image is visible");
                } else {
                    Log.d("ImageViewVisibility", "Image is not visible");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_CANCELED
                && !nameEdit.getText().toString().isEmpty()) {
            // If no picture is uploaded but a name is saved, generate an image with initials
            String name = nameEdit.getText().toString();
            Bitmap bitmap = generateImageWithInitials(name);
            myImageView.setImageBitmap(bitmap);
            imageUri = null; // Set the imageUri to null
            Button removePictureButton = getView().findViewById(R.id.removePictureButton);
            removePictureButton.setVisibility(View.VISIBLE); // Show the 'Remove Picture' button
        }
    }


    private void saveUserProfile() {
        // Get user-entered information
        String name = nameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String homepage = homeEdit.getText().toString();
        String country = countryEdit.getText().toString();
        boolean locationPermission = locationBox.isChecked();

        // Updating/Saving the new/changed user information of the current Attendee.
        currentUser.updateProfile(name, email, homepage, country, locationPermission);

        // Validate email format
        if (!isValidEmail(email)) {
            emailEdit.setError("Invalid email format");
            return;
        }

        // Check if an image is uploaded
        if (imageUri != null) {
            // Show the 'Remove Picture' button
            Button removePictureButton = getView().findViewById(R.id.removePictureButton);
            removePictureButton.setVisibility(View.VISIBLE);

            // Check if the ImageView is visible
            if (myImageView.getVisibility() != View.VISIBLE) {
                Log.d("ImageViewVisibility", "ImageView is not visible");
            } else {
                Log.d("ImageViewVisibility", "ImageView is visible");
            }
        } else {
            // Generate a temporary image with initials
            Log.d("UserProfileFragment", "Generating image with initials for name: " + name); // Add this line
            Bitmap bitmap = generateImageWithInitials(name);
            myImageView.setImageBitmap(bitmap);
            imageUri = Uri.parse("temp"); // Use a placeholder URI for the temporary image
        }

        // You can save this information or pass it to another fragment for display
        // For now, let's display a toast message with the information
        String message = "Name: " + name + "\nEmail: " + email + "\nHomepage: " + homepage +
                "\nCountry: " + country + "\nLocation Permission: " + locationPermission;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }




    public void onRemovePictureButtonClick(View view) {
        myImageView.setImageResource(android.R.color.transparent); // Clear the image
        imageUri = null; // Set the imageUri to null
        Button removePictureButton = getView().findViewById(R.id.removePictureButton);
        removePictureButton.setVisibility(View.GONE); // Hide the 'Remove Picture' button
    }

    private Bitmap generateImageWithInitials(String name) {
        // Generate an image with the initials of the name
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Log.d("BitmapSize", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());
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
        // Log the content of the bitmap
        StringBuilder bitmapContent = new StringBuilder();
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);
                bitmapContent.append(String.format("#%06X", (0xFFFFFF & pixel)));
            }
            bitmapContent.append("\n");
        }
        Log.d("BitmapContent", "Bitmap content:\n" + bitmapContent.toString());
        return bitmap;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidUrl(String url) {
        String urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return Pattern.matches(urlRegex, url);
    }


    // Setters for EditText fields for TestCases.
    public void setName(String name) {
        nameEdit.setText(name);
    }

    public void setEmail(String email) {
        emailEdit.setText(email);
    }

    public void setHome(String home) {
        homeEdit.setText(home);
    }

    public void setCountry(String country) {
        countryEdit.setText(country);
    }

    // Setter for CheckBox
    public void setLocationChecked(boolean checked) {
        locationBox.setChecked(checked);
    }

    // Getters for EditText fields
    public String getName() {
        return nameEdit.getText().toString();
    }

    public String getEmail() {
        return emailEdit.getText().toString();
    }

    public String getHome() {
        return homeEdit.getText().toString();
    }

    public String getCountry() {
        return countryEdit.getText().toString();
    }

    // Getter for CheckBox
    public boolean isLocationChecked() {
        return locationBox.isChecked();
    }


}

