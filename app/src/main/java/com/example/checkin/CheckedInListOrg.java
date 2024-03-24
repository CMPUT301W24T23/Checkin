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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// Shows list of checked in attendees for an event
public class CheckedInListOrg extends Fragment {
    private AttendeeList attendeedatalist;
    private ListView attendeesList;
    private ArrayAdapter<Attendee> AttendeesAdapter;
    Event myevent;
    private FirebaseFirestore db;
    Button backbutton;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checked_in_list, container, false);

        attendeesList = view.findViewById(R.id.attendees_list);
        backbutton = view.findViewById(R.id.backbtn);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ArrayList<Attendee> attendees = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }
        attendeedatalist = new AttendeeList();



        String eventid = myevent.getEventId();


        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(myevent.getEventId());
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve subscribers from the document
                        Map<String, String> subscribersMap = (Map<String, String>) document.get("UserCheckIn");
                        if (subscribersMap != null) {
                            int attendeeCount = subscribersMap.size();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("attendeeCount", attendeeCount);
                            editor.apply();
                            checkMilestone(attendeeCount);
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



    private void checkMilestone(int attendeeCount) {
        ArrayList<Integer> milestones = new ArrayList<>();

        milestones.add(1);
        milestones.add(10);
        milestones.add(50);
        milestones.add(100);
        for (int milestone : milestones) {
            if (attendeeCount == milestone) {
                sendMilestoneNotification(myevent.getEventname()+ ": Milestone Reached!", "Attendee count: " + attendeeCount);
                break; // No need to continue checking other milestones
            }
        }
    }

    private void sendMilestoneNotification(String title, String body) {
        // Create an intent and call the MileStone class's method to send a notification
        Intent intent = new Intent(getContext(), OrganizerView.class); // Replace YourActivity with the appropriate activity
        MileStone.sendMilestoneNotification(requireContext(), title, body, myevent.getEventId(), intent);
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

                        if (attendeedatalist != null) {
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