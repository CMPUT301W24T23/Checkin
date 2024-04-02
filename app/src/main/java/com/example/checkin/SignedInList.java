package com.example.checkin;

// shows signed in list of attendees to an event
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SignedInList extends Fragment {

    private AttendeeList attendeedatalist;
    private ListView attendeesList;
    private ArrayAdapter<Attendee> AttendeesAdapter;
    Event myevent;

    Button backbutton;

    private FirebaseFirestore db;

    private TextView totalsignups;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signed_in_list, container, false);

        backbutton = view.findViewById(R.id.backbtn);
        attendeesList = view.findViewById(R.id.signedin_attendees_list);
        totalsignups = view.findViewById(R.id.total_signup);

        attendeedatalist = new AttendeeList();


        if (attendeedatalist != null) {
            AttendeesAdapter = new ArrayAdapter<Attendee>(getActivity(), R.layout.content2, attendeedatalist.getAttendees()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.content2, null);
                    }
                    TextView textView = view.findViewById(R.id.attendee_name);
                    textView.setText(attendeedatalist.getAttendees().get(position).getName());
                    TextView textView2 = view.findViewById(R.id.checkin_times);
                    textView2.setVisibility(View.GONE);
                    return view;
                }
            };
            attendeesList.setAdapter(AttendeesAdapter);

        }


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }



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
        eventRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listener", "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Retrieve subscribers from the document
                    Map<String, String> subscribersMap = (Map<String, String>) documentSnapshot.get("Subscribers");
                    if (subscribersMap != null) {
                        for (String attendeeId : subscribersMap.keySet()) {
                            // Fetch each attendee document and create Attendee objects
                            fetchAttendeeFromFirestore(attendeeId, attendeedatalist);
                        }

                            for (Attendee existingAttendee : attendeedatalist.getAttendees()) {
                               if (!subscribersMap.containsKey(existingAttendee.getUserId())) {
                                    attendeedatalist.removeAttendee(existingAttendee);
                                }
                            }
                        }
                        AttendeesAdapter.notifyDataSetChanged();
                    String text = "Total Signed Up Attendees: " + attendeedatalist.getAttendees().size();
                    totalsignups.setText(text);
                } else {
                    Log.d("Firestore", "No such document");
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
                        if (!attendees.contains(attendee)) {
                            attendees.addAttendee(attendee);
                        }

                        AttendeesAdapter.notifyDataSetChanged();
                        String text = "Total Signed Up Attendees: " + attendeedatalist.getAttendees().size();
                        totalsignups.setText(text);
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