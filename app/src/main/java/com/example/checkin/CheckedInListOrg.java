package com.example.checkin;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/*
 Shows list of checked in attendees for an event
*/
public class CheckedInListOrg extends Fragment {
    private AttendeeList attendeedatalist;
    private ListView attendeesList;
    private ArrayAdapter<Attendee> AttendeesAdapter;
    Event myevent;
    private FirebaseFirestore db;
    Button backbutton;

    TextView totalcheckin;


    /**
     * Called to have the fragment instantiate its user interface view.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checked_in_list, container, false);

        attendeesList = view.findViewById(R.id.attendees_list);
        backbutton = view.findViewById(R.id.backbtn);
        totalcheckin = view.findViewById(R.id.total_checkins);

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

        if (attendees != null) {
            AttendeesAdapter = new AttendeeArrayAdapter(requireContext(), attendeedatalist.getAttendees());
            attendeesList.setAdapter(AttendeesAdapter);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        // retrieve events from firebase
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
                    Map<String, String> subscribersMap = (Map<String, String>) documentSnapshot.get("UserCheckIn");
                    if (subscribersMap != null) {
                        for (String attendeeId : subscribersMap.keySet()) {
                            fetchAttendeeFromFirestore(attendeeId, attendeedatalist, myevent.getEventId());
                        }

                        List<Attendee> attendeesCopy = new ArrayList<>(attendeedatalist.getAttendees());
                        Set<String> checkedInAttendeeIds = subscribersMap.keySet();
                        for (Attendee existingAttendee : attendeesCopy) {
                            if (!checkedInAttendeeIds.contains(existingAttendee.getUserId())) {
                               attendeedatalist.removeAttendee(existingAttendee);
                            }
                        }

                        AttendeesAdapter.notifyDataSetChanged();
                        String text = "Total Checked In Attendees: " + attendeedatalist.getAttendees().size();
                        totalcheckin.setText(text);
                    }
                } else {
                    Log.d("Firestore", "No such document");
                }
            }
        });
        return view;
    }


    /**
     * Retreives attendee from firebase
     * @param attendeeId
     * @param attendees
     * @param eventId
     */
    private void fetchAttendeeFromFirestore(String attendeeId, AttendeeList attendees, String eventId) {

        DocumentReference attendeeRef = db.collection("Attendees").document(attendeeId);
        attendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Attendee object using Database class method
                        Attendee attendee = new Database().getAttendee(document);

                        Map<String, Long> checkIns = (Map<String, Long>) document.get("Checkins");
                        if (checkIns != null) {
                            // Retrieve the check-in count for the specified eventId
                            Long checkInValue = checkIns.get(eventId);
                            System.out.println("checkin"+checkInValue);
                            if (checkInValue != null) {
                                // Set the check-in count for the attendee
                                attendee.setCheckInValue(checkInValue);
                                // Add the attendee to the list
                                if (!attendees.contains(attendee)) {
                                    attendees.addAttendee(attendee);
                                }

                                List<Attendee> attendeesCopy = new ArrayList<>(attendees.getAttendees());
                                for (Attendee existingAttendee : attendeesCopy) {
                                    if (!document.exists() || !checkIns.containsKey(eventId)) {
                                        // Remove the attendee from the list if it's not present in the Firestore document or has no check-in for this event
                                        attendees.removeAttendee(existingAttendee);
                                    }
                                }


                                //if (attendees != null) {
                                AttendeesAdapter.notifyDataSetChanged();
                                  //  AttendeesAdapter = new AttendeeArrayAdapter(requireContext(), attendees.getAttendees());
                                    //attendeesList.setAdapter(AttendeesAdapter);
                                //}
                                if(attendees.getAttendees().size() ==0){
                                   // String text = "No Checked In Attendees: ";
                                    //totalcheckin.setText(text);

                                }
                                else {
                                    String text = "Total Checked In Attendees: " + attendees.getAttendees().size();
                                    totalcheckin.setText(text);
                                }
                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }

                }
            }


                 });
    }
}