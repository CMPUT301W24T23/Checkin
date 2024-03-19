package com.example.checkin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

// Shows the Organizer Home page, which includes list of events
public class OrganizerFragment1 extends Fragment {
    private ArrayList<Event> datalist;
    private ListView eventslist;
    private ArrayAdapter<Event> EventAdapter;
    private EventList allevents;
    Button backbutton;
    Button addeventbutton;
    boolean update;
    Organizer organizer;


    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer1, container, false);
        ListView eventslist = (ListView) view.findViewById(R.id.events);
        backbutton = view.findViewById(R.id.backbtn);
        addeventbutton = view.findViewById(R.id.addeventbtn);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            allevents = (EventList) bundle.getSerializable("eventslist");
        } else {
            allevents = new EventList(); // Initialize only if bundle is null
        }


        // allevents = new EventList();
        ArrayList<Attendee> attendees1 = new ArrayList<>();
        Bundle bundle2 = this.getArguments();
        if (bundle2 != null) {
            organizer = (Organizer) bundle2.getSerializable("organizer");
        }

        // Add attendees and check them in/ sign up to test functionality
        Attendee attendee1 = new Attendee("Amy");
        Attendee attendee2 = new Attendee("John");
        attendees1.add(attendee1);
        Event event1 = new Event("Show", "Starts at 7, ends at 9 PM", attendees1);
        attendee1.CheckIn(event1);
        attendee2.CheckIn(event1);
        event1.userCheckIn(attendee1);
        event1.userCheckIn(attendee2);
        event1.userSubs(attendee2);
        allevents.addEvent(event1);


        // add event button, open create event fragment
        addeventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEventFragment createeventfrag = new CreateEventFragment();
                Bundle args = new Bundle();
                args.putSerializable("eventslist", allevents);
                createeventfrag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, createeventfrag).addToBackStack(null).commit();
                update = true;

            }
        });

        // move back to previous fragment when clicked
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        // if event list is not null, then set eventlist
        if (allevents.getEvents() != null) {
            EventAdapter = new ArrayAdapter<Event>(getActivity(), R.layout.content, allevents.getEvents()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.content, null);
                    }

                    TextView textView = view.findViewById(R.id.event_text);
                    textView.setText(allevents.getEvents().get(position).getEventname());
                    return view;
                }
            };
            eventslist.setAdapter(EventAdapter);
        }

        // move to details of event fragment when an event is selected
        eventslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventsDetailOrg eventd_frag1= new EventsDetailOrg();
                Bundle args = new Bundle();
                args.putSerializable("event", allevents.getEvents().get(i));
                eventd_frag1.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, eventd_frag1).addToBackStack(null).commit();


            }
        });
        return view;
    }
}