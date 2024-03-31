package com.example.checkin;

import android.app.AlertDialog;
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
import java.util.Map;

public class OrganizerFragment1 extends Fragment {
    private ArrayList<Event> datalist;
    private ListView eventslist;
    private ArrayAdapter<Event> EventAdapter;
    private EventList allevents;
    Button backbutton;
    Button addeventbutton;
    boolean update;
    Organizer organizer;

    ProgressBar p;

    private FirebaseFirestore db;

    RelativeLayout maincontent;

    // Shows the Organizer Home page, which includes list of events
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer1, container, false);
        ListView eventslist = (ListView) view.findViewById(R.id.events);
        backbutton = view.findViewById(R.id.backbtn);
        addeventbutton = view.findViewById(R.id.addeventbtn);
        p = view.findViewById(R.id.progress);
        maincontent = view.findViewById(R.id.maincontent);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomnavbar);
        bottomNavigationView.setVisibility(View.GONE);

        SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(getContext());
        int attendeeCount = preferences2.getInt("attendeeCount", 0);

    //EventList allevents  = new EventList();
        Bundle bundle = this.getArguments();
        // if (bundle != null) {
        //    allevents = (EventList) bundle.getSerializable("eventslist");
        //   } else {
        //  allevents = new EventList(); // Initialize only if bundle is null
        //  }
        allevents = new EventList();
        ArrayList<Attendee> attendees1 = new ArrayList<>();

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
        db = FirebaseFirestore.getInstance();
        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        // retreive organizer from firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference organizerRef = db.collection("Organizers").document(android_id);
        organizerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Organizer object using Database class method
                        organizer = database.getOrganizer(document);
                        database.updateOrganizer(organizer);
                        // Proceed with setting up the UI using the retrieved organizer object
                    } else {
                        Log.d("document", "No such document");
                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });

        // Query events collection based on organizer ID
        db.collection("Events")
                .whereEqualTo("Creator", android_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        p.setVisibility(View.GONE);
                        maincontent.setVisibility(View.VISIBLE);
                        bottomNavigationView.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Event event = database.getEvent(document);
                                allevents.addEvent(event);
                                Map<String, String> subscribersMap = (Map<String, String>) document.get("UserCheckIn");
                                if (subscribersMap != null) {
                                    int attendeeCount = subscribersMap.size();
                                    checkMilestone(attendeeCount, event);
                                }

                            }
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
                        }}
                });

        // add event button, open create event fragment
        addeventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEventFragment createeventfrag = new CreateEventFragment();
                Bundle args = new Bundle();
                args.putSerializable("eventslist", allevents);
                args.putSerializable("organizer", organizer);
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
                args.putSerializable("frameLayout", R.id.org_view);
                eventd_frag1.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, eventd_frag1).addToBackStack(null).commit();
            }
        });

        // Long click to delete an event
        eventslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Event eventToDelete = allevents.getEvents().get(position);
                // Confirm deletion with the organizer
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteEvent(eventToDelete))
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        return view;
    }

    // Deletes an event
    private void deleteEvent(Event event) {
        String eventId = event.getEventId();
        // Remove event from the organizer's list of created events
        organizer.removeEvent(eventId);
        // Remove event from the UI list
        allevents.removeEvent(event);
        EventAdapter.notifyDataSetChanged();
        // Delete event from the database
        db.collection("Events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete Event", "Event successfully deleted"))
                .addOnFailureListener(e -> Log.w("Delete Event", "Error deleting event", e));
    }

    private void checkMilestone(int attendeeCount, Event myevent) {
        ArrayList<Integer> milestones = new ArrayList<>();
        milestones.add(1);
        milestones.add(10);
        milestones.add(50);
        milestones.add(75);
        milestones.add(100);
        for (int milestone : milestones) {
            if (attendeeCount == milestone) {
                sendMilestoneNotification(myevent.getEventname()+ ": Milestone Reached!", "Attendee count: " + attendeeCount, myevent);
                break; // No need to continue checking other milestones
            }
        }
    }

    private void sendMilestoneNotification(String title, String body, Event myevent) {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), OrganizerView.class);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            String notificationKey = "milestone_" + myevent.getEventId();
            boolean notificationSent = sharedPreferences.getBoolean(notificationKey, false);
            if (!notificationSent) {
                int notificationId = Integer.parseInt(myevent.getEventId());
                MileStone.sendMilestoneNotification(requireContext(), title, body, myevent.getEventId(), intent, notificationId);
                sharedPreferences.edit().putBoolean(notificationKey, true).apply();
            }
        }
    }
}
