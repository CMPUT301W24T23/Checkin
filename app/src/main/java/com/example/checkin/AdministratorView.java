package com.example.checkin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// This class is responsible for executing the fragment "fragment_admin_menu".
public class AdministratorView extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);

        Button adminevents = view.findViewById(R.id.adminevent);
        Button adminprofiles = view.findViewById(R.id.adminprofile);
        Button adminimages = view.findViewById(R.id.adminimages);
        Button adminposters = view.findViewById(R.id.adminposter);


        adminimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AdministratorProfileImgList fragment = new AdministratorProfileImgList();
                fragmentTransaction.replace(R.id.adminFrame, fragment);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFrame, fragment) // Replace R.id.fragment_container with the ID of your fragment container
                        .addToBackStack(null)
                        .commit();

            }
        });

        adminposters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AdministratorPosterImgList fragment = new AdministratorPosterImgList();
                fragmentTransaction.replace(R.id.adminFrame, fragment);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFrame, fragment) // Replace R.id.fragment_container with the ID of your fragment container
                        .addToBackStack(null)
                        .commit();
            }
        });


        // Displays the new fragment showing the list of attendees.
        adminprofiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AdministratorAttendeeList fragment = new AdministratorAttendeeList();
                fragmentTransaction.replace(R.id.adminFrame, fragment);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFrame, fragment) // Replace R.id.fragment_container with the ID of your fragment container
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Displays the new fragment showing the list of events.
        adminevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AdministratorEventList fragment = new AdministratorEventList();
                fragmentTransaction.replace(R.id.adminFrame, fragment);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFrame, fragment) // Replace R.id.fragment_container with the ID of your fragment container
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}
