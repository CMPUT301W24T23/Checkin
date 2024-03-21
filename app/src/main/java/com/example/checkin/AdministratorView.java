package com.example.checkin;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdministratorView extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);

//
//        Button adminevents = findViewById(R.id.adminevent);
//        Button adminprofiles = findViewById(R.id.adminprofile);
//        Button adminimages = findViewById(R.id.adminimages);
//        Button adminposters = findViewById(R.id.adminposter);
//
//        adminevents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                AdministratorEventList fragment = new AdministratorEventList();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//
//                // Commit the transaction
//                fragmentTransaction.commit();
//            }
//        });
        return view;
    }
}

