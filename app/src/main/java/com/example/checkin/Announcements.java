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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
Announcements page that shows messages received by event organizers - will implement
sending notifications in next part of project, has mock data for now
*/
public class Announcements extends Fragment {
    ListView announcements;
    private ArrayList<Message> announcelist;
    private MessageAdapter Announcements_Adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        // initialize announcements page
        announcements = view.findViewById(R.id.announcements_list);
        announcelist = new ArrayList<>();

        // Add example announcements
        //announcelist.add("First Message");
        //announcelist.add("Second Message");



        // retrieve attendee from firebase and get the checkinlist for each attendee
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");
        DocumentReference attendeeRef = db.collection("Attendees").document(android_id);
        attendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData(); // Retrieve data as a Map
                        if (data != null && data.containsKey("Checkins")) {
                            Map<String, Long> checkedInMap = (Map<String, Long>) data.get("Checkins"); // Cast to the appropriate type
                            List<String> eventIds = new ArrayList<>();

                            // Iterate over the map entries
                            for (Map.Entry<String, Long> entry : checkedInMap.entrySet()) {
                                String eventId = entry.getKey();
                                System.out.println("key" + eventId);
                                eventIds.add(eventId);
                                // Fetch messages for the current event ID
                                fetchMessagesForEvent(db, eventId);
                            }

                        } else {
                            Log.d("Firestore", "Attendee has no checked-in map");
                        }
                    } else {
                        // Log.d("Firestore", "No such document for attendee with ID: " + attendeeId);
                    }
                } else {
                    Log.e("Firestore", "Error fetching attendee document", task.getException());
                }
            }
        });

        // if list of announcements is not null, then add messages to Announcements
        if (announcelist != null) {
            Announcements_Adapter = new MessageAdapter(getActivity(), announcelist);
            announcements.setAdapter(Announcements_Adapter);
        }
        else{

        }
        return view;
    }

    /**
     * Gets messages for events that attendee is checked into
     * @param db
     * @param eventId
     */
    private void fetchMessagesForEvent(FirebaseFirestore db, String eventId) {
        CollectionReference messagesRef = db.collection("Messages");
        messagesRef.whereEqualTo("Event Id", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> eventMessages = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Message message = document.toObject(Message.class);
                        if (message != null) { // Check if message is not null
                            message.setType(document.getString("Type"));}
                        if (message.getType() != null && message.getType().equals("Message")) {
                            Message m = new Message();
                            m.setTitle(document.getString("Title"));
                            m.setBody(document.getString("Body"));
                            announcelist.add(m);
                            System.out.println("message" + message.getTitle());
                        }

                    }

                    if (announcelist != null) {
                        Announcements_Adapter = new MessageAdapter(getActivity(), announcelist);
                        announcements.setAdapter(Announcements_Adapter);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Fetch Messages", "Error fetching messages for event: " + eventId, e);
                });
    }

}