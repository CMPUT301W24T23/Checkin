package com.example.checkin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.checkerframework.checker.units.qual.A;

/*
 Fragment that provides attendee with options to browse all events
 or signed up events
 */
public class AttendeeEventOptions extends Fragment {

    Button browsebutton;
    Button signupevents;
    Button backbutton;

    /**
     * Called to have the fragment instantiate its user interface view.
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_event_options, container, false);
        browsebutton = view.findViewById(R.id.browseeventsbtn);
        signupevents = view.findViewById(R.id.signedupeventsbtn);
        backbutton = view.findViewById(R.id.backbtn);

        // move to signed up events fragment
        signupevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendeeSignUpEvents signup_frag = new AttendeeSignUpEvents();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, signup_frag).addToBackStack(null).commit();

            }
        });

        // move to events fragment
        browsebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendeeFragment1 attendeeFragment1 = new AttendeeFragment1();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, attendeeFragment1).addToBackStack(null).commit();

            }
        });

        // move to previous activity
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