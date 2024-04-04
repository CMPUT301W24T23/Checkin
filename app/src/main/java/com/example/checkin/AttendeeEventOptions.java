package com.example.checkin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.checkerframework.checker.units.qual.A;


public class AttendeeEventOptions extends Fragment {

    Button browsebutton;
    Button signupevents;

    Button backbutton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_event_options, container, false);
        browsebutton = view.findViewById(R.id.browseeventsbtn);
        signupevents = view.findViewById(R.id.signedupeventsbtn);
        backbutton = view.findViewById(R.id.backbtn);

        signupevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendeeSignUpEvents signup_frag = new AttendeeSignUpEvents();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, signup_frag).addToBackStack(null).commit();

            }
        });

        browsebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendeeFragment1 attendeeFragment1 = new AttendeeFragment1();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, attendeeFragment1).addToBackStack(null).commit();

            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });







        return view;
    }
}