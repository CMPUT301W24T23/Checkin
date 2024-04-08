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

import java.util.ArrayList;
import java.util.List;


/**
 * Displays the list of all the poster images present in the app for the administrator.
 */
public class AdministratorPosterImgList extends Fragment {

    private FirebaseFirestore db;
    private ListView listView;
    private Bitmap noImgMap;
    private String noImagePic;
    CollectionReference eventProfileCollectionRef;
    CollectionReference imagesCollectionRef;

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
        imagesCollectionRef = db.collection("Posters");


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
        imagesCollectionRef.get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageString = document.getString("Image"); // Assuming the field name is "image"
                                if (imageString != null) {
                                    Bitmap bitmap = imageEncoder.base64ToBitmap(imageString);
                                    if (bitmap != null) {
                                        imageAdapter.add(bitmap);
                                    }
                                }
                            }
                        }
                    }
                });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap posterPic = imageAdapter.getItem(position);
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Poster Image")
                        .setMessage("Are you sure you want to delete this Poster Image?")
                        .setPositiveButton("Yes", (dialog, which) -> deletePosterPic(posterPic))
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });


        return view;
    }


    /**
     * Deletes the selected poster image from the list of all the poster in the app.
     * @param posterPic : Selected poster picture.
     */
    private void deletePosterPic(Bitmap posterPic){
        String posterPic2 = imageEncoder.BitmapToBase64(posterPic);
        imageAdapter.remove(posterPic);
        imageAdapter.notifyDataSetChanged();
        eventProfileCollectionRef = db.collection("Events");

        eventProfileCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String imageString = document.getString("Poster"); // Assuming the field name is "Poster"

                        assert imageString != null;
                        if (imageString.equals(posterPic2)){
                            document.getReference().update("Poster", "");
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting the poster pic: ", task.getException());
                }
            }
        });


        /*
          Deletes the selected poster pic from the posters field.
         */
        imagesCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageString = document.getString("Image"); // Assuming the field name is "image"

                        if (imageString.equals(posterPic2)) {
                            // Convert string to bitmap and add to the list
                            document.getReference().update("Image", "");
                        }
                    }
                }
            }
        });

    }

}
