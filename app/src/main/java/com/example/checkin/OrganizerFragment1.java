package com.example.checkin;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.provider.Settings;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
// represents fragment that displays an organizer's events
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

    // List to store deleted QR codes
    private ArrayList<String> deletedQRCodes = new ArrayList<>();

    /**
     * Inflates the layout for the Organizer Home fragment.
     * Initializes UI components, retrieves events from Firestore, and sets up event listeners.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * @return The View for the fragment's UI
     */
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

        // Retrieve the previously deleted QR code IDs from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Set<String> previousDeletedQRCodes = sharedPreferences.getStringSet("deletedQRCodes", new HashSet<>());

        // Add the previous QR code IDs to the deletedQRCodes array
        deletedQRCodes.addAll(previousDeletedQRCodes);

        SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(getContext());
        int attendeeCount = preferences2.getInt("attendeeCount", 0);

        //EventList allevents  = new EventList();
        //Bundle bundle = this.getArguments();
        // if (bundle != null) {
        //    allevents = (EventList) bundle.getSerializable("eventslist");
        //   } else {
        //  allevents = new EventList(); // Initialize only if bundle is null
        //  }
        allevents = new EventList();


        db = FirebaseFirestore.getInstance();
        Database database = new Database();
        String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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
                            EventAdapter = new ArrayAdapter<Event>(getContext(), R.layout.content, allevents.getEvents()) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = convertView;
                                    if (view == null) {
//                                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                                        view = inflater.inflate(R.layout.admin_list_layout, null);
                                        view = LayoutInflater.from(getContext()).inflate(R.layout.admin_list_layout, parent, false);


                                    }

                                    TextView textView = view.findViewById(R.id.admin_text_view);
                                    textView.setText(allevents.getEvents().get(position).getEventname());
                                    return view;
                                }
                            };
                            eventslist.setAdapter(EventAdapter);
                        }
                    }
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
                EventsDetailOrg eventd_frag1 = new EventsDetailOrg();
                Bundle args = new Bundle();
                args.putSerializable("event", allevents.getEvents().get(i));
                args.putSerializable("frameLayout", R.id.org_view);
                eventd_frag1.setArguments(args);
                getParentFragmentManager().setFragmentResult("event", args);
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

    /**
     * Deletes the specified event.
     *
     * @param event The event to be deleted
     */
    private void deleteEvent(Event event) {
        String qrCodeid = event.getQRCode();
        organizer.removeEvent(event.getEventId());
        allevents.removeEvent(event);

        // Upload the QR code to the DeletedQR collection in Firebase
        Database database = new Database();
        database.uploadDeletedQR(qrCodeid, organizer.getOrganizerId());

        EventAdapter.notifyDataSetChanged(); // Refresh the list
        db.collection("Events").document(event.getEventId()).delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete Event", "Event successfully deleted"))
                .addOnFailureListener(e -> Log.w("Delete Event", "Error deleting event", e));
    }

    /**
     * Checks if an event has reached any milestones
     * @param attendeeCount
     * @param myevent
     */
    // checks if event has reached any milestones
    private void checkMilestone(int attendeeCount, Event myevent) {
        ArrayList<Integer> milestones = new ArrayList<>();
        milestones.add(1);
        milestones.add(10);
        milestones.add(50);
        milestones.add(75);
        milestones.add(100);
        SharedPreferences sharedPreferences =null;
        if (getContext() != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }


        // Retrieve the set of reached milestones for this event
        Set<String> reachedMilestones = sharedPreferences.getStringSet("reachedMilestones_" + myevent.getEventId(), new HashSet<>());

        for (int milestone : milestones) {
            if (attendeeCount >= milestone) {
                String milestoneKey = "milestone_" + milestone;
                if (!reachedMilestones.contains(milestoneKey)) {
                    // This milestone is reached for the first time
                    sendMilestoneNotification(myevent.getEventname() + ": Milestone Reached!", "Attendee count: " + attendeeCount, myevent, milestone);

                    // Add the milestone ID to the set of reached milestones
                    reachedMilestones.add(milestoneKey);

                    // Save the updated set in SharedPreferences
                    sharedPreferences.edit().putStringSet("reachedMilestones_" + myevent.getEventId(), reachedMilestones).apply();
                }
            } else {
                // If the attendee count drops below the milestone, remove it from the set, so milestones trigger once
                String milestoneKey = "milestone_" + milestone;
                reachedMilestones.remove(milestoneKey);
                sharedPreferences.edit().putStringSet("reachedMilestones_" + myevent.getEventId(), reachedMilestones).apply();
            }
        }

    }


    /**
     * Sends notifcation for milestones
     * @param title
     * @param body
     * @param myevent
     * @param attendeecount
     */
    private void sendMilestoneNotification(String title, String body, Event myevent, int attendeecount) {
        // Create an intent and call the MileStone class's method to send a notification

        if (getContext() != null) {
            Intent intent = new Intent(getContext(), OrganizerView.class);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());


            // Construct the notification key including the attendee count
            // Check if the notification for this milestone has already been sent
            //  boolean notificationSent = sharedPreferences.getBoolean(notificationKey, false);

            //if (!notificationSent) {
            //int notificationId = Integer.parseInt(myevent.getEventId());

            String notificationKey = "milestone_" + myevent.getEventId();
            boolean notificationSent = sharedPreferences.getBoolean(notificationKey, false);
            if (!notificationSent) {

                int notificationId = 1;
                MileStone.sendMilestoneNotification(requireContext(), title, body, myevent.getEventId(), intent, notificationId);

            }
        }
    }

    /**
     * Adds event to eventlist
     * @param event
     */
    public void addEvent(Event event) {
        allevents.addEvent(event);
    }


}
