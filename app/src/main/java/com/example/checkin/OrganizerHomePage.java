package com.example.checkin;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

// Organizer Home page
public class OrganizerHomePage extends Fragment {

    private ArrayList<Event> datalist;
    private ListView eventslist;
    private ArrayAdapter<Event> EventAdapter;

    private EventList allevents;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee1, container, false);
        ListView eventslist = (ListView) view.findViewById(R.id.events);



        datalist = new ArrayList<>();
        allevents = new EventList();
        ArrayList<Attendee> attendees1 = new ArrayList<>();
        Attendee attendee1 = new Attendee("Amy");
        Attendee attendee2 = new Attendee("John");
        attendees1.add(attendee1);
        Event event1 = new Event("Show", "Starts at 7, ends at 9 PM", attendees1);
        attendee1.CheckIn(event1);
        attendee2.CheckIn(event1);
        event1.userCheckIn(attendee1);
        event1.userCheckIn(attendee2);
        datalist.add(event1);
        allevents.add(event1);


        if (datalist != null) {
            EventAdapter = new ArrayAdapter<Event>(getActivity(), R.layout.content, datalist) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.content, null);
                    }

                    TextView textView = view.findViewById(R.id.event_text);
                    textView.setText(datalist.get(position).getEventname());
                    return view;
                }
            };
            eventslist.setAdapter(EventAdapter);
        }

        eventslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventsDetail_org eventd_frag1= new EventsDetail_org();
                Bundle args = new Bundle();
                args.putSerializable("event", datalist.get(i));
                eventd_frag1.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);


                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, eventd_frag1).commit();


            }
        });
        return view;
    }
}