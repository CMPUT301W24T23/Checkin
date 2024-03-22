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

        announcements = view.findViewById(R.id.announcements_list);
        announcelist = new ArrayList<>();

        // Add example announcements
        //announcelist.add("First Message");
        //announcelist.add("Second Message");

        // if attendeeslist is not null set AttendeesAdapter to custom AttendeeArrayAdapter


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
                        if (document.contains("CheckedIn")) {
                            Map<String, Object> checkedInMap = document.get("CheckedIn", Map.class);
                            List<String> eventIds = new ArrayList<>(checkedInMap.keySet());

                            // Fetch messages for each event ID
                            for (String eventId : eventIds) {
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
        // Represented as strings now, will create announcement objects in next part
        if (announcelist != null) {
            Announcements_Adapter = new MessageAdapter(getActivity(), announcelist);
            announcements.setAdapter(Announcements_Adapter);
        }

        return view;

    }

    private void fetchMessagesForEvent(FirebaseFirestore db, String eventId) {
        CollectionReference messagesRef = db.collection("messages");
        messagesRef.whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> eventMessages = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Message message = document.toObject(Message.class);
                        eventMessages.add(message);
                    }
                    announcelist.addAll(eventMessages); // Add messages to the announcelist
                    Announcements_Adapter.notifyDataSetChanged(); // Notify adapter of data change
                })
                .addOnFailureListener(e -> {
                    Log.e("Fetch Messages", "Error fetching messages for event: " + eventId, e);
                });
    }

}