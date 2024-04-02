/*
An Android Fragment serving as the attendee home page.
It displays events in a ListView, uses a back button to return to `MainActivity`,
and handles event selection, transitioning to an `EventDetailAtten` fragment for detailed information.
 */
package com.example.checkin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// Home page for Attendee Perspective
public class AttendeeFragment1 extends Fragment {
    private ArrayList<Event> datalist;
    private ListView eventslist;
    private ArrayAdapter<Event> EventAdapter;
    private EventList allevents;
    Button backbutton;
    private FirebaseFirestore db;
    ProgressBar p;
    RelativeLayout maincontent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee1, container, false);
        ListView eventslist = (ListView) view.findViewById(R.id.events);
        backbutton = view.findViewById(R.id.backbtn);
        p = view.findViewById(R.id.progress);
        maincontent = view.findViewById(R.id.maincontent);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomnavbar);
        bottomNavigationView.setVisibility(View.GONE);

        allevents = new EventList();
        datalist = new ArrayList<>();
        // Add mock attendee and event for testing purposes
        ArrayList<Attendee> attendees1 = new ArrayList<>();
        attendees1.add(new Attendee("Amy"));
        Event event1 = new Event("Show", "Starts at 7", attendees1);
        allevents.addEvent(event1);
        datalist.add(event1);



        db = FirebaseFirestore.getInstance();
        Database database = new Database();

        // Query all events from firebase
        db.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            p.setVisibility(View.GONE);
                            maincontent.setVisibility(View.VISIBLE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            for (DocumentSnapshot document : task.getResult()) {
                                Event event = database.getEvent(document);
                                allevents.addEvent(event);
                            }

                            if (allevents!= null) {
                                EventAdapter = new EventArrayAdapter(getActivity(), allevents.getEvents());
                                eventslist.setAdapter(EventAdapter);
                            }

                        }}
                });

        // back button that goes to homepage
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // if eventlist is not null set EventAdapter to custom EventArrayAdapter
        if (allevents!= null) {
            EventAdapter = new EventArrayAdapter(getActivity(), allevents.getEvents());
            eventslist.setAdapter(EventAdapter);
        }

        // When event is selected from list, move to fragment that show event details
        eventslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventDetailAtten event_frag1= new EventDetailAtten();
                Bundle args = new Bundle();
                args.putSerializable("event", allevents.getEvents().get(i));
                event_frag1.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, event_frag1).addToBackStack(null).commit();

            }
        });
        return view;
    }
}