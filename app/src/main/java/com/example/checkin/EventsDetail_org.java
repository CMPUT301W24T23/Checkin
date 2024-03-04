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


public class EventsDetail_org extends Fragment {

    Button attendeelistbutton;

    Button qrcodebutton;
    Event myevent;

    EditText eventnametxt;

    EditText eventdetails;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events_detail_org, container, false);
        // Inflate the layout for this fragment
        Button attendeelistbutton = (Button) view.findViewById(R.id.attendeeslistbtn);
        Button qrcodebutton = (Button) view.findViewById(R.id.codebtn);
        eventnametxt = view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventdetails_txt);




        Bundle bundle = this.getArguments();
        assert bundle != null;
        myevent = (Event) bundle.getSerializable("event");

        qrcodebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add share qr code page fragment

                ShareCode code_frag = new ShareCode();

                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                code_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, code_frag).commit();


            }
        });

        attendeelistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add attendees list fragment
                Attendeeslisted list_frag = new Attendeeslisted();

                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                list_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, list_frag).commit();

            }
        });
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventdetails());
        // Inflate the layout for this fragment
        return view;


    }
}