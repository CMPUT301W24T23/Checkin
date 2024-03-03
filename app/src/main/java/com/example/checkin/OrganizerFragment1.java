package com.example.checkin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class OrganizerFragment1 extends Fragment {

    private ArrayList<Event> datalist;
    private ListView eventslist;
    private ArrayAdapter<Event> EventAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee1, container, false);
        ListView eventslist = (ListView) view.findViewById(R.id.events);



        datalist = new ArrayList<>();
        ArrayList<Attendee> attendees1 = new ArrayList<>();
        attendees1.add(new Attendee("Amy"));
        datalist.add(new Event("Show", "Starts at 7", attendees1));

        // if eventlist is not null set EventAdapter to custom EventArrayAdapter
        if (eventslist != null) {
            EventAdapter = new EventArrayAdapter(getActivity(), datalist);
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