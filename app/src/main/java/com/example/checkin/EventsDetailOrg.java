package com.example.checkin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

// Shows event information for an organizer
public class EventsDetailOrg extends Fragment {
    Button attendeelistbutton;
    Button qrcodebutton;
    Event myevent;
    EditText eventnametxt;
    EditText eventdetails;
    Button backbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events_detail_org, container, false);
        // Inflate the layout for this fragment
        attendeelistbutton = (Button) view.findViewById(R.id.attendeeslistbtn);
        qrcodebutton = (Button) view.findViewById(R.id.codebtn);
        backbutton = view.findViewById(R.id.backbtn);
        eventnametxt = view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventdetails_txt);



        // get event object from previous fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        myevent = (Event) bundle.getSerializable("event");


        // move back to pevious fragment when clicked
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // move to fragment where qr code is displayed
        qrcodebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareCode code_frag = new ShareCode();
                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                code_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, code_frag).addToBackStack(null).commit();


            }
        });

        // move to fragment that shows attendees list options
        attendeelistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add attendees list fragment
                Attendeeslisted list_frag = new Attendeeslisted();

                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                list_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, list_frag).addToBackStack(null).commit();

            }
        });
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventdetails());

        return view;


    }
}