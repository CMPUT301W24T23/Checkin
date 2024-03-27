package com.example.checkin;
// shows list of milestones reached for events
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

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


public class DisplayMilestones extends Fragment {

    ListView milestones;
    private ArrayList<Message> announcelist;
    private MessageAdapter Announcements_Adapter;
    Button backbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_display_milestones, container, false);

        milestones = view.findViewById(R.id.milestones_list);
        announcelist = new ArrayList<>();
        backbutton = view.findViewById(R.id.backbtn);


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Add example announcements
        //announcelist.add("First Message");
        //announcelist.add("Second Message");

        // if attendeeslist is not null set AttendeesAdapter to custom AttendeeArrayAdapter


        // retrieve organizer's events from firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        DocumentReference OrganizerRef = db.collection("Organizers").document(android_id);
        OrganizerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData(); // Retrieve data as a Map
                        if (data != null && data.containsKey("Events")) {
                            Map<String, String> checkedInMap = (Map<String, String>) data.get("Events");
                            List<String> eventIds = new ArrayList<>();

                            // Iterate over the map entries
                            for (Map.Entry<String, String> entry : checkedInMap.entrySet()) {
                                String eventId = entry.getKey();
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
        // Represented as strings now, will create announcement objects in next part
        if (announcelist != null) {
            Announcements_Adapter = new MessageAdapter(getActivity(), announcelist);
            milestones.setAdapter(Announcements_Adapter);
        }

        return view;

    }

    /**
     * Retrive milestone messages from firebase
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
                        message.setType(document.getString("Type"));
                        if (message != null && message.getType() != null && message.getType().equals("Milestone")) {
                            // If it's a milestone message, add it to the list

                            Message m = new Message();
                            m.setTitle(document.getString("Title"));
                            m.setBody(document.getString("Body"));
                            if (!checkmilestoneexists(m, announcelist)) {
                                announcelist.add(m);
                            }
                        }
                    }

                    if (announcelist != null) {
                        Announcements_Adapter = new MessageAdapter(getActivity(), announcelist);
                        milestones.setAdapter(Announcements_Adapter);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Fetch Messages", "Error fetching messages for event: " + eventId, e);
                });
    }


    public boolean checkmilestoneexists(Message message, ArrayList<Message> announcelist){

        for (Message existingMessage : announcelist) {
            if (existingMessage.getTitle().equals(message.getTitle()) && existingMessage.getBody().equals(message.getBody())) {
                return true;
            }
        }
        return false;

    }

}