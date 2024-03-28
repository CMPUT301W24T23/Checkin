package com.example.checkin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.checkin.AdministratorEventList;

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


        // Displays the new fragment showing the list of attendees.
        adminprofiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AdministratorAttendeeList fragment = new AdministratorAttendeeList();
                fragmentTransaction.replace(R.id.adminFrame, fragment);

                // Commit the transaction
                fragmentTransaction.commit();
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

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}
