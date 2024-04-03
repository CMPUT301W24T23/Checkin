package com.example.checkin;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class signedupeventdetail extends Fragment {

    Event myevent;
    TextView eventnametxt;
    TextView eventdetails;
    Button backbutton;
    Button signupbutton;
    Button posterbutton;

    TextView eventDate;
    TextView eventTime;
    TextView eventlocation;
    String eventid;

    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signedupeventdetail, container, false);

        eventnametxt =  view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventinfo);
        backbutton = view.findViewById(R.id.backbtn);
        posterbutton = view.findViewById(R.id.eventposterbtn);
        eventDate = view.findViewById(R.id.EventDatetxt);
        eventTime = view.findViewById(R.id.EventTimetxt);
        eventlocation = view.findViewById(R.id.editlocation);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        String time = "Time: " + myevent.getEventTime();
        String date = "Date: " + myevent.getEventDate();
        String location = "Location: " + myevent.getLocation();
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventDetails());
        eventDate.setText(date);
        eventTime.setText(time);
        eventlocation.setText(location);

        Database database = new Database();



        eventid = myevent.getEventId();


        db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");



        // get event object from previous fragment


        // move back to previous fragment when clicked
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventDetails());

        //Display no poster available if the event does not have a poster
        if (myevent.getPoster().equals("")){
            //no poster for this event
            //posterbutton.setError(String.format("%s has no poster.", myevent.getEventname()));
            posterbutton.setText("No Poster Available");
        }

        //move to poster fragment
        posterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myevent.getPoster().equals("")){
                    //no poster for this event
                    //posterbutton.setError(String.format("%s has no poster.", myevent.getEventname()));
                    return;
                }

                //Create fragment
                EventPosterFrag posterShareFrag = new EventPosterFrag();

                UserImage poster = new UserImage();
                poster.setImageB64(myevent.getPoster());
                poster.setID(myevent.getEventId());

                Bundle args = new Bundle();
                args.putSerializable("Poster", poster);

                posterShareFrag.setArguments(args);
                //ShareCode code_frag = new ShareCode();
                //Bundle args = new Bundle();
                //args.putSerializable("event", myevent);
                //code_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("Poster",args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, posterShareFrag).addToBackStack(null).commit();



                //getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void fetchAttendee(OnSuccessListener<Attendee> onSuccessListener) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Database database = new Database();
        DocumentReference attendeeRef = db.collection("Attendees").document(android_id);
        attendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Attendee object using Database class method
                        Attendee attendee = database.getAttendee(document);
                        onSuccessListener.onSuccess(attendee);
                    } else {
                        Log.d("document", "No such document");
                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });
    }
    /**
     * Retrieve attendee and sub/check in to the event
     * @param id
     * ID of the user to be retrieved
     * @param CheckIn
     * A boolean indicating whether the user is being checked in or subscribed to the event
     */
    public void retrieveAttendee(String id, boolean CheckIn){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Attendees").document(id);
        Database fireBase = new Database();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "Retrieve attendee: " + document.getData());
                        Attendee a = fireBase.getAttendee(document);

                        //If the user is to be checked in, check them in
                        if(CheckIn){
                            myevent.userCheckIn(a);
                        } else{
                            //Otherwise sub them
                            myevent.userSubs(a);
                        }
                    } else {
                        Log.d("Firebase", "No such document");
                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });
    }




}