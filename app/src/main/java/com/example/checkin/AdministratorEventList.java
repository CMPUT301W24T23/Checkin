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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdministratorEventList extends Fragment {
    FirebaseFirestore db;
    private ArrayAdapter<Event> EventAdapter;
    private EventList allevents;
    private ListView listView;
    Database database = new Database();


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

        // Query all events without filtering based on organizer ID
        db.collection("Events")
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

    // Deletes an event
    private void deleteEvent(Event event) {
        String eventId = event.getEventId();
        allevents.removeEvent(event);
        EventAdapter.notifyDataSetChanged(); // Refresh the list
        db.collection("Events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete Event", "Event successfully deleted"))
                .addOnFailureListener(e -> Log.w("Delete Event", "Error deleting event", e));
    }


//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        setupSwipeGesture();
//    }
//
//    // CHATGPT 3.5
//    @SuppressLint("ClickableViewAccessibility")
//    private void setupSwipeGesture() {
//        listView.setOnTouchListener(new View.OnTouchListener() {
//            private float startX;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = event.getX();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        float endX = event.getX();
//                        float deltaX = endX - startX;
//
//                        // Determine if it's a swipe
//                        if (Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(deltaX) > SWIPE_VELOCITY_THRESHOLD) {
//                            if (deltaX < 0) {
//                                // Left swipe
//                                int position = listView.pointToPosition((int) event.getX(), (int) event.getY());
//                                if (position != ListView.INVALID_POSITION) {
//                                    String selectedEvent = String.valueOf(EventAdapter.getItem(position));
//                                    Event selectedEvent2 = (EventAdapter.getItem(position));
//
//                                    // Show confirmation dialog
//                                    new AlertDialog.Builder(requireContext())
//                                            .setTitle("Delete Event")
//                                            .setMessage("Are you sure you want to delete this Event?")
//                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    // User confirmed deletion
//                                                    // Remove the attendee from the adapter
//                                                    EventAdapter.remove(selectedEvent2);
//                                                    // Notify adapter about the removal
//                                                    EventAdapter.notifyDataSetChanged();
//
//                                                    // Delete the attendee from the field "Attendees".
//                                                    db.collection("Attendees")
//                                                            .whereEqualTo("Name", selectedEvent)
//                                                            .get()
//                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                @SuppressLint("RestrictedApi")
//                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                    if (task.isSuccessful()) {
//                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                            document.getReference().delete();
//                                                                            Log.d(TAG, "Successfully deleted the event. ", task.getException());
//                                                                        }
//                                                                    } else {
//                                                                        Log.d(TAG, "Error getting documents: ", task.getException());
//                                                                    }
//                                                                }
//                                                            });
//                                                }
//                                            })
//                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    // User cancelled deletion
//                                                    dialog.dismiss();
//                                                }
//                                            })
//                                            .show();
//                                }
//                            }
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
//    }
//

}

