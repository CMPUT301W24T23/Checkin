package com.example.checkin;

// shows signed in list of attendees to an event
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class SignedInList extends Fragment {

    private AttendeeList attendeedatalist;
    private ListView attendeesList;
    private ArrayAdapter<Attendee> AttendeesAdapter;
    Event myevent;

    Organizer organizer;
    Button backbutton;

    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signed_in_list, container, false);

        backbutton = view.findViewById(R.id.backbtn);
        attendeesList = view.findViewById(R.id.signedin_attendees_list);

        attendeedatalist = new AttendeeList();

        ArrayList<Attendee> attendees = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }
        String eventid = myevent.getEventId();



        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(myevent.getEventId());
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve subscribers from the document
                        Map<String, String> subscribersMap = (Map<String, String>) document.get("Subscribers");
                        if (subscribersMap != null) {
                            for (String attendeeId : subscribersMap.keySet()) {
                                // Fetch each attendee document and create Attendee objects
                                fetchAttendeeFromFirestore(attendeeId, attendeedatalist);
                            }
                        }
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

        return view;
    }

    private void fetchAttendeeFromFirestore(String attendeeId, AttendeeList attendees) {
        DocumentReference attendeeRef = db.collection("Attendees").document(attendeeId);
        attendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Attendee object using Database class method
                        Attendee attendee = new Database().getAttendee(document);
                        // Add the attendee to the list
                        attendees.addAttendee(attendee);
                        // Update the UI with the attendees list

                        if (attendeedatalist!= null) {
                            AttendeesAdapter = new AttendeeArrayAdapter(requireContext(), attendees.getAttendees());
                            attendeesList.setAdapter(AttendeesAdapter);
                        }
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }
}