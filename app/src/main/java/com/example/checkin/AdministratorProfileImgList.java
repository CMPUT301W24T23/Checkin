package com.example.checkin;

import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_THRESHOLD;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_VELOCITY_THRESHOLD;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
This class is responsible for showing the list of the profile images of all the attendees/users signed in to the app.
 */
public class AdministratorProfileImgList extends Fragment {

    private FirebaseFirestore db;
    private ListView listView;
    private ArrayAdapter<Bitmap> imageAdapter;
    ImageEncoder imageEncoder = new ImageEncoder();
    UserProfileFragment userProfileFragment = new UserProfileFragment();
    CollectionReference imagesCollectionRef;
    CollectionReference attendeeProfileCollectionRef;
    String finalNewPic;

    /**
     * Inflates the layout showing the list of the profile pics providing the functionality of deleting them when
     * long pressed on that particular profile pic.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout and represent the list of events.
        View view = inflater.inflate(R.layout.admin_profile_img_list, container, false);
        listView = view.findViewById(R.id.admin_profileimglist);
        Button backbtn = view.findViewById(R.id.back_button);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Initialize Firebase Firestore.
        db = FirebaseFirestore.getInstance();

        // Get the collection reference for images
        imagesCollectionRef = db.collection("ProfilePics");

        // Initialize image list
        List<Bitmap> imageList = new ArrayList<>();

        // Adapter for displaying images in ListView
        imageAdapter = new ArrayAdapter<Bitmap>(getContext(), android.R.layout.simple_list_item_1, imageList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_img_layout, parent, false);
                }

                ImageView imageView = convertView.findViewById(R.id.admin_img_view);
                Bitmap bitmap = getItem(position);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
                return convertView;
            }
        };
        listView.setAdapter(imageAdapter);

        // Retrieve images from Firestore
        db.collection("Attendees").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageString = document.getString("ProfilePic"); // Assuming the field name is "image"
                        Boolean hasDefaultAvi = document.getBoolean("HasDefaultAvi"); // Checks if the profile pic is auto-gen or not.

                        if (imageString != null) {
                            if (Boolean.FALSE.equals(hasDefaultAvi)){
                                // Convert string to bitmap and add to the list
                                Bitmap bitmap = imageEncoder.base64ToBitmap(imageString);
    //                            Bitmap bitmap1 = resizeBitmap(bitmap, 20, 20);
                                if (bitmap != null) {
                                    imageAdapter.add(bitmap);
                                }
                            }
                        }
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap profilePic = imageAdapter.getItem(position);
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Attendee")
                        .setMessage("Are you sure you want to delete this attendee?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteProfilePic(profilePic))
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        return view;

    }

    /**
     * Function responsible of deleting the selected profile picture.
     * @param profilePic : Profile picture selected to delete.
     */
    private void deleteProfilePic(Bitmap profilePic) {
        String profilePic2 = imageEncoder.BitmapToBase64(profilePic);
        imageAdapter.remove(profilePic);
        imageAdapter.notifyDataSetChanged();
        attendeeProfileCollectionRef = db.collection("Attendees");

        attendeeProfileCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the value of the "Name" subsection
                        String imageString = document.getString("ProfilePic"); // Assuming the field name is "image"

                        assert imageString != null;
                        if (imageString.equals(profilePic2)) {

                            String name = document.getString("Name");
                            if (name != null) {
                                // Generated a new default profile pic.
                                Bitmap newPic = userProfileFragment.generateImageWithInitials(name);

                                // Converted the bitmap to strong 64 (Compatible to firebase).
                                finalNewPic = imageEncoder.BitmapToBase64(newPic);

                                //
                                document.getReference().update("ProfilePic", finalNewPic)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Default Profile picture generated successfully");
                                                imageAdapter.add(newPic);
                                                imageAdapter.notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Error updating profile picture", e);
                                            }
                                        });
                            } else {
                                Log.d(TAG, "Error generating default image (No Name).");
                            }

                        }
                    }
                } else {
                    Log.d(TAG, "Error getting the profile pic: ", task.getException());
                }
            }
        });

        // Removing the profile pic from the profilePic field in the firebase.
        imagesCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageString = document.getString("Image"); // Assuming the field name is "image"

                        if (imageString.equals(profilePic2)) {
                            // Convert string to bitmap and add to the list
                            document.getReference().update("Image", finalNewPic);
                        }
                    }
                }
            }
        });
    }
}
