package com.example.checkin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
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
import java.util.Objects;
import java.util.regex.Pattern;
import com.example.checkin.Attendee;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.regex.Pattern;

/*
Fragment displaying the user profile (using fragment_user_profile.xml layout)
including profile pic, name and other information along with the functionality of
generating, uploading profile pic based on the name initials and changing attendee information.
*/

public class UserProfileFragment extends Fragment {
    public static final int PICK_IMAGE_REQUEST = 1;
    private ImageView myImageView;
    private Uri imageUri;
    //private UserProfileViewModel viewModel;
    boolean newImage = false;

    private final Database db = new Database();
    private final ImageEncoder imgEncode = new ImageEncoder();

    private Attendee currentUser = new Attendee();    // Instance of the an attendee.

    private EditText nameEdit, emailEdit, homeEdit, phoneEdit;      //Text boxes
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

        //Set user information
        currentUser.setUserId(Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID));

        loadPrefs();
        //Load from firebase in case changes made there
        retrieveAttendee(currentUser.getUserId());

        // Initialize other UI elements
        nameEdit = view.findViewById(R.id.nameEdit);
        emailEdit = view.findViewById(R.id.emailEdit);
        homeEdit = view.findViewById(R.id.homeEdit);
        phoneEdit = view.findViewById(R.id.phoneEdit);
        locationBox = view.findViewById(R.id.locationBox);


        // Setting the contact information of the current user to the xml layout.
        nameEdit.setText(currentUser.getName());
        emailEdit.setText(currentUser.getEmail());
        homeEdit.setText(currentUser.getHomepage());
        phoneEdit.setText(currentUser.getPhoneNumber());
        locationBox.setChecked(currentUser.trackingEnabled());

        if(!(currentUser.getProfilePicture() == "")){
            Bitmap avi = imgEncode.base64ToBitmap(currentUser.getProfilePicture());
            myImageView.setImageBitmap(avi);
        }


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

    /**
     * Opens a file chooser for selecting an image.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the file chooser activity and sets the selected image to the ImageView.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     * @param resultCode  The result code returned by the child activity,
     * @param data        An Intent that carries the result data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            newImage = true;
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
            newImage = false;
            Button removePictureButton = getView().findViewById(R.id.removePictureButton);
            removePictureButton.setVisibility(View.VISIBLE); // Show the 'Remove Picture' button
        }
    }


    /**
     * Saves the user's profile information and picture.
     * Citing: Took the help of Chat gpt in order to understand and learn new concepts about Bitmap and
     * how to work with it.
     */
    private Bitmap originalBitmap; // Store the original Bitmap here

    private void saveUserProfile() {
        // Get user-entered information
        String name = nameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String homepage = homeEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        boolean locationPermission = locationBox.isChecked();

        // Validate email format
        if (!isValidEmail(email)) {
            emailEdit.setError("Invalid email format");
            return;
        }

        String imageBase64 = currentUser.getProfilePicture();;

        if (!(Objects.equals(currentUser.getProfilePicture(), ""))){
            imageBase64 = currentUser.getProfilePicture();
        }

        // Check if an image is uploaded
        if (imageUri != null && newImage) {
            try {
                originalBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                myImageView.setImageBitmap(originalBitmap);
                imageBase64 = imgEncode.BitmapToBase64(originalBitmap);
                currentUser.setProfilePicture(imageBase64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!(Objects.equals(currentUser.getProfilePicture(), ""))){
            imageBase64 = currentUser.getProfilePicture();
        } else{
                // Generate a temporary image with initials
                Log.d("UserProfileFragment", "Generating image with initials for name: " + name); // Add this line
                Bitmap bitmap = generateImageWithInitials(name);
                myImageView.setImageBitmap(bitmap);
                originalBitmap = bitmap;
                imageUri = Uri.parse("temp"); // Use a placeholder URI for the temporary image

                // Upload the generated image here
                uploadGeneratedImage(bitmap);

                // Show the 'Edit Picture' button
                Button editPictureButton = getView().findViewById(R.id.editPictureButton);
                editPictureButton.setVisibility(View.VISIBLE);

                imageBase64 = imgEncode.BitmapToBase64(originalBitmap);
                currentUser.setProfilePicture(imageBase64);
                // Log the visibility of the ImageView
                Log.d("ImageViewVisibility", "ImageView visibility after setting bitmap: " + myImageView.getVisibility());

        }

        //if (!(Objects.equals(currentUser.getProfilePicture(), ""))){
        //    imageBase64 = currentUser.getProfilePicture();
        //}
        //Encode the image into Base64
        //String imageBase64 = imgEncode.BitmapToBase64(originalBitmap);

        /*
        // Decode the Base64 string back to a Bitmap for checking
        Bitmap decodedBitmap = base64ToBitmap(imageBase64);

        // Check if the original Bitmap and the decoded Bitmap are the same
        if (originalBitmap.sameAs(decodedBitmap)) {
            Log.d("ImageCheck", "Image conversion successful");
        } else {
            Log.d("ImageCheck", "Image conversion failed");
        }
         */

        // Updating/Saving the new/changed user information of the current Attendee.
        //currentUser.updateProfile(name, email, homepage, country, locationPermission);
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setHomepage(homepage);
        currentUser.setPhoneNumber(phone);
        if(!(currentUser.trackingEnabled() == locationPermission)){
            currentUser.toggleTracking();
        }

        currentUser.setProfilePicture(imageBase64);

        db.updateAttendee(currentUser);

        savePrefs();        //Save user info to local preferences

        String message = "Name: " + name + "\nEmail: " + email + "\nHomepage: " + homepage +
                "\nPhone: " + phone + "\nLocation Permission: " + locationPermission;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Uploads the generated image to a desired location.
     *
     * @param bitmap The bitmap image to upload.
     */
    private void uploadGeneratedImage(Bitmap bitmap) {
        // Convert the Bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Upload the image data to your desired location
        Log.d("UserProfileFragment", "Uploading generated image...");

        // Simulate upload process
        try {
            // Simulate upload time
            Thread.sleep(2000);
            Log.d("UserProfileFragment", "Generated image uploaded successfully");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the selected picture from the ImageView.
     *
     * @param view The remove picture button view.
     */
    public void onRemovePictureButtonClick(View view) {
        myImageView.setImageResource(android.R.color.transparent); // Clear the image
        imageUri = null; // Set the imageUri to null
        Button removePictureButton = getView().findViewById(R.id.removePictureButton);
        removePictureButton.setVisibility(View.GONE); // Hide the 'Remove Picture' button
    }

    /**
     * Generates an image with the initials of the given name.
     *
     * @param name The name to generate initials from.
     * @return The generated image bitmap.
     */
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

        // Set ImageView visibility to VISIBLE
        myImageView.setVisibility(View.VISIBLE);

        // Set the bitmap to the ImageView
        myImageView.setImageBitmap(bitmap);

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
        Log.d("BitmapSize", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());

        // Log the visibility of the ImageView
        Log.d("ImageViewVisibility", "ImageView visibility after setting bitmap: " + myImageView.getVisibility());

        return bitmap;
    }

    /**
     * Validates an email address.
     *
     * @param email The email address to validate.
     * @return True if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * Validates a URL.
     *
     * @param url The URL to validate.
     * @return True if the URL is valid, false otherwise.
     */
    private boolean isValidUrl(String url) {
        String urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return Pattern.matches(urlRegex, url);
    }


    /**
     * Makes a query to firebase and retrieves the current user, updating the textboxes in the process
     * @param id
     * The id of the current user
     */
    public void retrieveAttendee(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Attendees").document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "Retrieve attendee: " + document.getData());
                        /*
                        //currentUser.setUserId(document.getId());
                        currentUser.setName(document.getString("Name"));
                        currentUser.setHomepage(document.getString("Homepage"));
                        currentUser.setPhoneNumber(document.getString("Phone"));

                        //set the tracking status of the attendee
                        //the empty constructor has tracking as true by default
                        boolean track = Boolean.TRUE.equals(document.getBoolean("Tracking"));
                        if(!(track == currentUser.trackingEnabled())){
                            currentUser.toggleTracking();
                        }*/
                        Database fireBase = new Database();
                        currentUser = fireBase.getAttendee(document);

                        //Set textboxes to the updated information
                        nameEdit.setText(currentUser.getName());
                        emailEdit.setText(currentUser.getEmail());
                        homeEdit.setText(currentUser.getHomepage());
                        phoneEdit.setText(currentUser.getPhoneNumber());
                        locationBox.setChecked(currentUser.trackingEnabled());

                        Bitmap bitmap = imgEncode.base64ToBitmap(currentUser.getProfilePicture());
                        myImageView.setImageBitmap(bitmap);

                        savePrefs();

                    } else {
                        Log.d("Firebase", "No such document");
                        //Database file is empty
                        nameEdit.setText("");
                        emailEdit.setText("");
                        homeEdit.setText("");
                        phoneEdit.setText("");
                        locationBox.setChecked(false);
                        savePrefs();
                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Load stored local data into view
     */
    private void loadPrefs(){
        //Restore saved local data for quick access
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentUser.setName(preferences.getString("Name", ""));
        currentUser.setEmail(preferences.getString("Email", ""));
        currentUser.setHomepage(preferences.getString("Homepage", ""));
        currentUser.setPhoneNumber(preferences.getString("Phone", ""));
        if(!(currentUser.trackingEnabled() == preferences.getBoolean("Tracking", false))){
            currentUser.toggleTracking();
        }

        currentUser.setProfilePicture(preferences.getString("ProfilePic", ""));
    }

    /**
     * Save the user information to local preferences
     */
    private void savePrefs(){
        //save data locally so it can be displayed instantly when this fragment is opened again
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Name", currentUser.getName());
        editor.putString("Email", currentUser.getEmail());
        editor.putString("Homepage", currentUser.getHomepage());
        editor.putString("Phone", currentUser.getPhoneNumber());
        editor.putBoolean("Tracking", currentUser.trackingEnabled());
        editor.putString("ProfilePic", currentUser.getProfilePicture());
        editor.apply();
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

    public void setPhone(String phone) {
        phoneEdit.setText(phone);
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

    public String getPhone() {
        return phoneEdit.getText().toString();
    }

    // Getter for CheckBox
    public boolean isLocationChecked() {
        return locationBox.isChecked();
    }


}