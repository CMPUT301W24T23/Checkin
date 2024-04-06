package com.example.checkin;

import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_THRESHOLD;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_VELOCITY_THRESHOLD;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.List;

public class AdministratorPosterImgList extends Fragment {

    private FirebaseFirestore db;
    private ListView listView;
    private Bitmap noImgMap;
    private String noImagePic;

    private ArrayAdapter<Bitmap> imageAdapter;
    ImageEncoder imageEncoder = new ImageEncoder();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout and represent the list of events.
        View view = inflater.inflate(R.layout.admin_poster_img, container, false);
        listView = view.findViewById(R.id.admin_posterlist);


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
        CollectionReference imagesCollectionRef = db.collection("Posters");


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
        db.collection("Posters").get().
        addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageString = document.getString("Image"); // Assuming the field name is "image"
                        if (imageString != null) {
                            Bitmap bitmap = base64ToBitmap(imageString);
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

    public Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
                                    Bitmap posterImg = imageAdapter.getItem(position);

                                    // Show confirmation dialog
                                    new AlertDialog.Builder(requireContext())
                                            .setTitle("Delete Poster Image")
                                            .setMessage("Are you sure you want to delete this Poster Image?")

                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    // Check if there is already any poster Image or not.
                                                    String posterImg2 = imageEncoder.BitmapToBase64(posterImg);
                                                    if (posterImg2 == noImagePic){
                                                        Log.d(TAG, "Already No Poster Image.");
                                                        dialog.dismiss();
                                                    }
                                                    Log.d(TAG, "Poster Image found.");
                                                    if (posterImg == noImgMap){
                                                        Log.d(TAG, "Already No Poster Image.");
                                                        dialog.dismiss();
                                                    }
                                                    Log.d(TAG, "Poster Image found.");

                                                    // User confirmed deletion
                                                    // Remove the attendee from the adapter
                                                    imageAdapter.remove(posterImg);
                                                    // Notify adapter about the removal
                                                    imageAdapter.notifyDataSetChanged();

                                                    // Delete the profile pic from the field "ProfilePics"
                                                    db.collection("Posters")
                                                            .whereEqualTo("Image", posterImg2)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @SuppressLint("RestrictedApi")
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            // Deletes the poster.
                                                                            document.getReference().delete();
                                                                            Log.d(TAG, "Deleted Poster Image");
                                                                            imageAdapter.notifyDataSetChanged();
                                                                        }
                                                                    } else {
                                                                        Log.d(TAG, "Error deleting Poster Image: ", task.getException());
                                                                    }
                                                                }
                                                            });

                                                    // Delete the profile pic from the field "Attendees and subfield 'ProfilePic'".
                                                    db.collection("Events")
                                                            .whereEqualTo("Poster", posterImg2)
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
