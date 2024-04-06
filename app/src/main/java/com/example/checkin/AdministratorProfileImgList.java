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

public class AdministratorProfileImgList extends Fragment {

    private FirebaseFirestore db;
    private ListView listView;
    private ArrayAdapter<Bitmap> imageAdapter;
    ImageEncoder imageEncoder = new ImageEncoder();
    UserProfileFragment userProfileFragment = new UserProfileFragment();


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
        CollectionReference imagesCollectionRef = db.collection("ProfilePics");

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
        imagesCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageString = document.getString("Image"); // Assuming the field name is "image"

                        if (imageString != null) {
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
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSwipeGesture();
    }


    // CHATGPT 3.5
    @SuppressLint("ClickableViewAccessibility")
    private void setupSwipeGesture() {
        listView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float deltaX = endX - startX;

                        // Determine if it's a swipe
                        if (Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(deltaX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (deltaX < 0) {
                                // Left swipe
                                int position = listView.pointToPosition((int) event.getX(), (int) event.getY());
                                if (position != ListView.INVALID_POSITION) {
                                    Bitmap profilePic = imageAdapter.getItem(position);

                                    // Show confirmation dialog
                                    new AlertDialog.Builder(requireContext())
                                            .setTitle("Delete Attendee")
                                            .setMessage("Are you sure you want to delete this Profile Pic?")

                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // User confirmed deletion
                                                    // Remove the attendee from the adapter
                                                    imageAdapter.remove(profilePic);
                                                    // Notify adapter about the removal
                                                    imageAdapter.notifyDataSetChanged();
                                                    String profilePic2 = imageEncoder.BitmapToBase64(profilePic);

                                                    // Delete the profile pic from the field "ProfilePics"
                                                    db.collection("ProfilePics")
                                                            .whereEqualTo("Image", profilePic2)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @SuppressLint("RestrictedApi")
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            document.getReference().delete();
                                                                            Log.d(TAG, "Deleted Profile Pic");
                                                                            imageAdapter.notifyDataSetChanged();
                                                                        }
                                                                    } else {
                                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                                    }
                                                                }
                                                            });

                                                    // Delete the profile pic from the field "Attendees and subfield 'ProfilePic'".
                                                    db.collection("Attendees")
                                                            .whereEqualTo("ProfilePic", profilePic2)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            document.getReference().delete();
                                                                            imageAdapter.notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                    // Generates a new profile pic based on the initials of the name and replace it with the deleted profile pic.
                                                    db.collection("Attendees")
                                                            .whereEqualTo("ProfilePic", profilePic2)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @SuppressLint("RestrictedApi")
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            // Get the value of the "Name" subsection
                                                                            String name = document.getString("Name");
                                                                            if (name != null) {
                                                                                // Generated a new default profile pic.
                                                                                Bitmap newPic = userProfileFragment.generateImageWithInitials(name);

                                                                                // Converted the bitmap to strong 64 (Compatible to firebase).
                                                                                String finalNewPic = imageEncoder.BitmapToBase64(newPic);

                                                                                //
                                                                                document.getReference().update("ProfilePic", finalNewPic)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                Log.d(TAG, "Default Profile picture generated successfully");
                                                                                                imageAdapter.notifyDataSetChanged();
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Log.e(TAG, "Error updating profile picture", e);
                                                                                            }
                                                                                        });
                                                                            }
                                                                            else {
                                                                                Log.d(TAG, "Error generating default image (No Name).");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                                    }
                                                                }
                                                            });
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // User cancelled deletion
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }
}
