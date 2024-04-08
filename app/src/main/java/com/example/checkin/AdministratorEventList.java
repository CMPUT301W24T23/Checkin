package com.example.checkin;

import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_THRESHOLD;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_VELOCITY_THRESHOLD;
import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fragment responsible of showing the list of events in the app and gives the functionality for admin to
 * navigate through each event and see the details and also delete the event.
 */
public class AdministratorEventList extends Fragment {
    FirebaseFirestore db;
    private ArrayAdapter<Event> EventAdapter;
    private EventList allevents;
    private ListView listView;
    Database database = new Database();
    CollectionReference attendeeDetails;
    CollectionReference eventDetails;


    /**
     * Responsible for generating the fragment displaying the list of events, buttons for navigating back to
     * the main menu fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_eventlist, container, false);
        Button backbtn = view.findViewById(R.id.back_button);
        allevents = new EventList(); // Initialize the list of events
        ListView listView = view.findViewById(R.id.admin_events);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        db = FirebaseFirestore.getInstance();
        eventDetails = db.collection("Events");

        // Query all events without filtering based on organizer ID
        eventDetails
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Event event = database.getEvent(document);
                                allevents.addEvent(event);
                            }

                            // Set up the adapter and list view.
                            EventAdapter = new ArrayAdapter<Event>(getActivity(), R.layout.content, allevents.getEvents()) {
                                @Override

                                // This getView is responsible for creating the View for each item in the ListView.
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = convertView;
                                    if (view == null) {

                                        view = LayoutInflater.from(getContext()).inflate(R.layout.admin_list_layout, parent, false);
//                                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                                        // Sets the view to the content layout.
//                                        view = inflater.inflate(R.layout.content, null);
                                    }

                                    // Setting the text of content layout to the name of the event.
                                    TextView textView = view.findViewById(R.id.admin_text_view);
                                    textView.setText(allevents.getEvents().get(position).getEventname());

                                    return view;
                                }
                            };
                            listView.setAdapter(EventAdapter);
                        } else {
                            Log.d("OrganizerFragment1", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // Shows the details of the event selected.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventsDetailOrg eventd_frag1= new EventsDetailOrg();
                Bundle args = new Bundle();
                args.putSerializable("event", allevents.getEvents().get(i));
                args.putSerializable("frameLayout", R.id.adminFrame);
                eventd_frag1.setArguments(args);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFrame, eventd_frag1) // Replace R.id.fragment_container with the ID of your fragment container
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Long click to delete an event
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
     * Deletes the selected event from both the event field and from the each attendees list who were checked-in/signed-in to that
     * particular event.
     * @param event : selected event to delete.
     */
    @SuppressLint("RestrictedApi")
    private void deleteEvent(Event event) {
        String eventId = event.getEventId();
        attendeeDetails = db.collection("Attendees");
        final List<Map<String, Object>> subscribedList = new ArrayList<>();
        final List<Map<String, Object>> checkedList = new ArrayList<>();

        eventDetails.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        // Getting the map of the attendeesId who are checked in and subscribed.

                        Map<String, Object> subscribedMap = (Map<String, Object>) document.get("Subscribers");
                        Map<String, Object> checkedMap = (Map<String, Object>) document.get("UserCheckIn");

                    }


                    // Removing the event from the attendee's subscribed list.
                    for (Map<String, Object> subscribedMap : subscribedList){

                        for(String key : subscribedMap.keySet()){

                            DocumentReference docRef;
                            docRef = attendeeDetails.document(key);

                            // Document of the particular attendee that is subscribed to the event.
                            docRef
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            DocumentSnapshot document = task.getResult();

                                            // Getting the map from the firebase.
                                            Map<String, Object> checkinsMap = (Map<String, Object>) document.get("Signups");
                                            assert checkinsMap != null;
                                            checkinsMap.remove(key);

                                            // Update the new map on the firebase.
                                            docRef.update("Signups", checkinsMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Update", "Error updating document", e);
                                                        }
                                                    });

                                        } else {
                                            Log.d("Query", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        }
                    }

                    // Removing the event from the attendee's checked in list.
                    for (Map<String, Object> checkedMap : checkedList){

                        for(String key : checkedMap.keySet()){

                            DocumentReference docRef;
                            docRef = attendeeDetails.document(key);

                            // Document of the particular attendee that is checked in to the event.
                            docRef
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            DocumentSnapshot document = task.getResult();

                                            // Getting the map from the firebase.
                                            Map<String, Object> checkinsMap = (Map<String, Object>) document.get("Checkins");
                                            checkinsMap.remove(key);

                                            // Update the new map on the firebase.
                                            docRef.update("Checkins", checkinsMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Update", "Error updating document", e);
                                                        }
                                                    });

                                        } else {
                                            Log.d("Query", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        }
                    }

                }
            }
        });

        allevents.removeEvent(event);
        EventAdapter.notifyDataSetChanged(); // Refresh the list

        // Removing the event from the Events field in the firebase.
        eventDetails.document(eventId).delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete Event", "Event successfully deleted"))
                .addOnFailureListener(e -> Log.w("Delete Event", "Error deleting event", e));

    }

}

