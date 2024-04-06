package com.example.checkin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/*
An Android Fragment displaying options for an event's attendees,
featuring buttons to view the checked-in list and navigate back.
 */

public class AttendeesOptions extends Fragment {

    Button checkedinlistbtn;
    Event myevent;
    Button backbutton;
    Button signedinlistbtn;
    int frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendeeslisted, container, false);
        checkedinlistbtn = view.findViewById(R.id.checkedinbtn);
        backbutton = view.findViewById(R.id.backbtn);
        signedinlistbtn = view.findViewById(R.id.signedinbtn);

        // get event object from previous fragment
        Bundle bundle = this.getArguments();

        if (bundle != null){
            myevent = (Event) bundle.getSerializable("event");

            // Frame layout on which to display the other sub informational fragment.
            frameLayout = (int) bundle.getSerializable("frameLayout");
        }
        // checked in list button, switches to fragment that displays, attendees checked into the event
        checkedinlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckedInListOrg check_frag = new CheckedInListOrg();
                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                check_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, check_frag).addToBackStack(null).commit();

            }
        });

        // moves to fragment that shows signed up attendees for an event
        signedinlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignedInList sign_frag = new SignedInList();
                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                sign_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, sign_frag).addToBackStack(null).commit();


            }
        });
        // moves back to previous fragment
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }
}