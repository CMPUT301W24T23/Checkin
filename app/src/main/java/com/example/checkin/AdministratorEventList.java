package com.example.checkin;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    Database database = new Database();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_eventlist, container, false);
        ListView listView = view.findViewById(R.id.admin_events);
        Button backbtn = view.findViewById(R.id.back_button);
        allevents = new EventList(); // Initialize the list of events

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
                                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        // Sets the view to the content layout.
                                        view = inflater.inflate(R.layout.content, null);
                                    }

                                    // Setting the text of content layout to the name of the event.
                                    TextView textView = view.findViewById(R.id.event_text);
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

        return view;
    }

}

