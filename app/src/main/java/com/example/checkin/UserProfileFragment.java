package com.example.checkin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class UserProfileFragment extends Fragment {
    public static final int PICK_IMAGE_REQUEST = 1;
    private ImageView myImageView;
    private Uri imageUri;

    private final Database db = new Database();
    private Attendee currentUser = new Attendee();

    private EditText nameEdit, emailEdit, homeEdit, phoneEdit;
    private CheckBox locationBox;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        myImageView = view.findViewById(R.id.myImageView);
        Button editPictureButton = view.findViewById(R.id.editPictureButton);
        editPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        currentUser.setUserId(Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID));

        loadPrefs();
        retrieveAttendee(currentUser.getUserId());

        nameEdit = view.findViewById(R.id.nameEdit);
        emailEdit = view.findViewById(R.id.emailEdit);
        homeEdit = view.findViewById(R.id.homeEdit);
        phoneEdit = view.findViewById(R.id.PhoneEdit);
        locationBox = view.findViewById(R.id.locationBox);

        nameEdit.setText(currentUser.getName());
        emailEdit.setText(currentUser.getEmail());
        homeEdit.setText(currentUser.getHomepage());

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

    private void loadPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentUser.setName(preferences.getString("Name", ""));
        currentUser.setEmail(preferences.getString("Email", ""));
        currentUser.setHomepage(preferences.getString("Homepage", ""));
        currentUser.setPhoneNumber(preferences.getString("Phone", ""));
        if (!(currentUser.trackingEnabled() == preferences.getBoolean("Tracking", false))) {
            currentUser.toggleTracking();
        }
    }

    private void savePrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Name", currentUser.getName());
        editor.putString("Email", currentUser.getEmail());
        editor.putString("Homepage", currentUser.getHomepage());
        editor.putString("Phone", currentUser.getPhoneNumber());
        editor.putBoolean("Tracking", currentUser.trackingEnabled());
        editor.apply();
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
        String name = nameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String homepage = homeEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        boolean locationPermission = locationBox.isChecked();

        if (!isValidEmail(email)) {
            emailEdit.setError("Invalid email format");
            return;
        }

        if (imageUri != null) {
            // Upload the image
        }

        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setHomepage(homepage);
        currentUser.setPhoneNumber(phone);
        if (!(currentUser.trackingEnabled() == locationPermission)) {
            currentUser.toggleTracking();
        }
        db.updateAttendee(currentUser);

        savePrefs();

        String message = "Name: " + name + "\nEmail: " + email + "\nHomepage: " + homepage +
                "\nPhone: " + phone + "\nLocation Permission: " + locationPermission;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void onRemovePictureButtonClick(View view) {
        myImageView.setImageResource(android.R.color.transparent);
        imageUri = null;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidUrl(String url) {
        String urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return Pattern.matches(urlRegex, url);
    }

    public void retrieveAttendee(String id) {
        // Retrieve attendee from Firebase
    }

    public void setName(String name) {
        nameEdit.setText(name);
    }

    public void setEmail(String email) {
        emailEdit.setText(email);
    }

    public void setHome(String home) {
        homeEdit.setText(home);
    }

    public void setPhone(String phone) {
        currentUser.setPhoneNumber(phone);
    }

    public void setLocationChecked(boolean checked) {
        locationBox.setChecked(checked);
    }

    public String getName() {
        return nameEdit.getText().toString();
    }

    public String getEmail() {
        return emailEdit.getText().toString();
    }

    public String getHome() {
        return homeEdit.getText().toString();
    }

    public String getPhone() {
        return currentUser.getPhoneNumber();
    }

    public boolean isLocationChecked() {
        return locationBox.isChecked();
    }
}
