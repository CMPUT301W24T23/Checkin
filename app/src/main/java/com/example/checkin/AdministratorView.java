package com.example.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/*
This class is responsible for executing the fragment "fragment_admin_menu" which provides the options to navigate for admin.
 */
public class AdministratorView extends Fragment {

    /**
     * Responsible for inflating the fragment showing options to admin.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);

        // Button for displaying the list of events.
        Button adminevents = view.findViewById(R.id.adminevent);

        // Button for displaying the list of profiles.
        Button adminprofiles = view.findViewById(R.id.adminprofile);

        // Button for displaying the list of profile pictures.
        Button adminimages = view.findViewById(R.id.adminimages);

        // Button for displaying the list of poster pictures.
        Button adminposters = view.findViewById(R.id.adminposter);

        // Button to go back to the main menu.
        Button homeButton = view.findViewById(R.id.adminhomebtn);


        // Functionality for button which displays the profile pictures.
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

        // Setting the functionality of the home button.
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jump back to the main screen.
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Setting the functionality of the poster images button.
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
