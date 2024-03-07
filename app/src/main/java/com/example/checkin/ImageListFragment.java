package com.example.checkin;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ImageListFragment extends Fragment {

    private Administrator administrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Administrator (you might pass it through constructor or other methods)
        administrator = new Administrator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_image_browsing, container, false);

        // Get reference to ListView widget
        ListView allImagesListView = view.findViewById(R.id.allImagesListView);

        // Create ArrayAdapter for all images
        ArrayAdapter<Image> allImagesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, administrator.getAllImages());
        allImagesListView.setAdapter(allImagesAdapter);

        return view;
    }
}
