package com.example.checkin;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AttendeeSignUpEvents extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_attendee_sign_up_events, container, false);

        ListView eventslist = (ListView) view.findViewById(R.id.events);
        backbutton = view.findViewById(R.id.backbtn);
        p = view.findViewById(R.id.progress);
        maincontent = view.findViewById(R.id.maincontent);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomnavbar);
        bottomNavigationView.setVisibility(View.GONE);

        allevents = new EventList();
        datalist = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        db.collection("Attendees").document(android_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        p.setVisibility(View.GONE);
                        maincontent.setVisibility(View.VISIBLE);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Retrieve the map of event IDs from the attendee document
                                Map<String, String> eventIdsMap = (Map<String, String>) document.get("SignedUpEvents"); // change when signups added

                                // Now you have the map of event IDs, you can iterate through the map
                                // and query the Events collection to retrieve the event details
                                for (Map.Entry<String, String> entry : eventIdsMap.entrySet()) {
                                    String eventId = entry.getKey(); // Get the event ID

                                    // Query the Events collection using the event ID
                                    db.collection("Events").document(eventId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot eventDoc = task.getResult();
                                                        if (eventDoc.exists()) {
                                                            // Retrieve event details here

                                                            Event event = database.getEvent(document);
                                                            allevents.addEvent(event);

                                                        } else {
                                                            Log.d("no document", "No such event document");
                                                        }
                                                    } else {
                                                        Log.d("Error", "Error getting event document", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d("No document", "No such document");
                            }
                        } else {
                            Log.d("Error", "Error getting document", task.getException());
                        }
                    }
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