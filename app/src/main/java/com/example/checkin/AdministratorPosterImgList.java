package com.example.checkin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
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
        imagesCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageString = document.getString("Image"); // Assuming the field name is "image"
                        if (imageString == ""){
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.download);
                            imageAdapter.add(bitmap);
                        }
                        else if (imageString != null) {
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

}
