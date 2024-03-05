package com.example.checkin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

// Page for an events attendees, including those who checked in
public class Attendeeslisted extends Fragment {

    Button checkedinlistbtn;
    Event myevent;
    Button backbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendeeslisted, container, false);
        checkedinlistbtn = view.findViewById(R.id.checkedinbtn);
        backbutton = view.findViewById(R.id.backbtn);

        // get event object from previous fragment
        Bundle bundle = this.getArguments();
        if (bundle != null){
            myevent = (Event) bundle.getSerializable("event");
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, check_frag).commit();

            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }
}