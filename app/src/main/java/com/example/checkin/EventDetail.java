package com.example.checkin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// Event details page for Attendee
public class EventDetail extends Fragment {


    Event myevent;

    TextView eventnametxt;

    TextView eventdetails;



    Button eventmessagesbtn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        eventnametxt =  view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventinfo);
        Button eventmessagesbtn = (Button) view.findViewById(R.id.eventmessg);



        Bundle bundle = this.getArguments();
        assert bundle != null;
        myevent = (Event) bundle.getSerializable("event");

        eventmessagesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Announcements announce_frag1= new Announcements();


                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, announce_frag1).commit();

            }
        });
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventdetails());

        // Inflate the layout for this fragment
        return view;
    }
}