package com.example.checkin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MemoryLruGcSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Map;

// Event details page for Attendee
public class EventDetailAtten extends Fragment {
    Event myevent;
    TextView eventnametxt;
    TextView eventdetails;
    Button backbutton;
    Button eventmessagesbtn;

    Button checkinbutton;
    Button signupbutton;
    Button posterbutton;

    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        eventnametxt =  view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventinfo);
        backbutton = view.findViewById(R.id.backbtn);
        eventmessagesbtn = view.findViewById(R.id.eventmessg);
        checkinbutton  = view.findViewById(R.id.checkinbtn);
        signupbutton =  view.findViewById(R.id.signupbtn);
        posterbutton = view.findViewById(R.id.eventposterbtn);
        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");






        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        String eventid = myevent.getEventId();



        db = FirebaseFirestore.getInstance();

        System.out.println("checkincount first "+ myevent.getCheckInList().getAttendees().size());

        checkinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db = FirebaseFirestore.getInstance();
                Database d = new Database();
                DocumentReference eventRef = db.collection("Events").document(myevent.getEventId());
                eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Retrieve event from firebase
                                //Event event = database.getEvent(document);
                                myevent = database.getEvent(document);
                                System.out.println("checkinpeople first "+ myevent.getCheckInList().getAttendees().size());

                                //Retrieve event lists
                                Map<String, String> checkInMap = (Map<String, String>) document.get("UserCheckIn");
                                for(String a : checkInMap.keySet()){
                                    //Check each user in to the event
                                    //db.getAttendee(document);
                                    retrieveAttendee(a, true);
                                    //myevent.userCheckIn();
                                    //Log.d("Retrieved event checkin", String.format("Checkin %s", a));
                                }

                                Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                                for(String a : subbedMap.keySet()){
                                    //Check each user in to the event
                                    //db.getAttendee(document);
                                    retrieveAttendee(a, false);
                                    //Log.d("Retrieved event Sub", String.format("Sub %s", a));
                                    //myevent.userCheckIn();
                                }
                                //wait for execution FOR TESTING
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }


                                //get current attendee and upload
                                fetchAttendeeFromFirestore(android_id, true);
                                System.out.println("checkinpeople last "+ myevent.getCheckInList().getAttendees().size());

                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
                        //fetchAttendee(new OnSuccessListener<Attendee>() {
                           // @Override
                           // public void onSuccess(Attendee attendee) {

                               // FetchEventCheckIN(attendee);
                                //attendee.CheckIn(myevent);
                               // myevent.userCheckIn(attendee);

                                FirebaseMessaging.getInstance().subscribeToTopic(myevent.getEventId())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Subscribe", "subscribe to event");
                                            }
                                        });

                                //Database database = new Database();
                                 // database.updateEvent(myevent);
                                 // database.updateAttendee(attendee);
                            }
                      //  });
           // }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();
                Database d = new Database();
                DocumentReference eventRef = db.collection("Events").document(myevent.getEventId());
                eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Retrieve event from firebase
                                //Event event = database.getEvent(document);
                                myevent = database.getEvent(document);
                                System.out.println("checkinpeople first "+ myevent.getCheckInList().getAttendees().size());

                                //Retrieve event lists
                                Map<String, String> checkInMap = (Map<String, String>) document.get("UserCheckIn");
                                for(String a : checkInMap.keySet()){
                                    //Check each user in to the event
                                    //db.getAttendee(document);
                                    retrieveAttendee(a, true);
                                    //myevent.userCheckIn();
                                    //Log.d("Retrieved event checkin", String.format("Checkin %s", a));
                                }

                                Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                                for(String a : subbedMap.keySet()){
                                    //Check each user in to the event
                                    //db.getAttendee(document);
                                    retrieveAttendee(a, false);
                                    //Log.d("Retrieved event Sub", String.format("Sub %s", a));
                                    //myevent.userCheckIn();
                                }
                                //wait for execution FOR TESTING
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                //get current attendee and upload
                                fetchAttendeeFromFirestore(android_id, false);

                                System.out.println("checkinpeople last "+ myevent.getCheckInList().getAttendees().size());

                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
       // database.updateEvent(myevent);



        // get event object from previous fragment



        // move to announcements page when see announcements button is clicked

        // move back to previous fragment when clicked
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventdetails());

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
    public void fetchEvent(String eventId, OnSuccessListener<Event> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(eventId);
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Event object using Database class method
                        Event event = new Database().getEvent(document);
                        onSuccessListener.onSuccess(event);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Fetch all the users checked in to the event
     * @param CheckedInUsers
     */
    public void FetchEventCheckIn(Map<String, String> CheckedInUsers){
       Database db = new Database();
        FirebaseFirestore.getInstance().collection("Events")
                .document(myevent.getEventId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, String> checkedAttendees = (Map<String, String>) document.get("UserCheckIn");
                        for(String a : checkedAttendees.keySet()){
                            //Check each user in to the event
                            //db.getAttendee(document);
                            retrieveAttendee(a, true);
                            //myevent.userCheckIn();
                            //Log.d("Retrieved event checkin", String.format("Checkin %s", a));
                        }

                        Map<String, String> subbedAttendees = (Map<String, String>) document.get("Subscribers");
                        for(String a : subbedAttendees.keySet()){
                            //Check each user in to the event
                            //db.getAttendee(document);
                            retrieveAttendee(a, false);
                            //Log.d("Retrieved event Sub", String.format("Sub %s", a));
                            //myevent.userCheckIn();
                        }

                    }
                });
    }

    /**
     * Fetch all users subbed to the event
     * @param SubbedUsers
     */
    public void FetchEventSub(Map<String, String> SubbedUsers){
        Database db = new Database();
        FirebaseFirestore.getInstance().collection("Events")
                .document(myevent.getEventId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, String> subbedAttendees = (Map<String, String>) document.get("UserCheckIn");
                        for(String a : subbedAttendees.keySet()){
                            //Check each user in to the event
                            //db.getAttendee(document);
                            retrieveAttendee(a, false);
                            //Log.d("Retrieved event Sub", String.format("Sub %s", a));
                            //myevent.userCheckIn();
                        }
                    }
                });
    }

    /**
     * Retrieve attendee and sub/check in to the event
     * @param id
     * @param CheckIn
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
                        //Otherwise sub them
                        if(CheckIn){
                            myevent.userCheckIn(a);
                        } else{
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

        private void fetchAttendeeFromFirestore(String attendeeid, boolean CheckIn) {

        Database d = new Database();

            DocumentReference attendeeRef = db.collection("Attendees").document(attendeeid);
            attendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Convert the document snapshot to an Attendee object using Database class method
                            Attendee attendee = d.getAttendee(document);

                            //User is checking in/out
                            if(CheckIn){
                                Log.d("Test Current Checkin", "NUMBERS BEFORE" + myevent.getCheckInList().getAttendees().size());
                                myevent.userCheckIn(attendee);
                                System.out.println("NUMBERS " + myevent.getCheckInList().getAttendees().size());
                            } else{
                                //otherwise they are subbing/unsubbing
                                myevent.userSubs(attendee);
                            }


                            Log.d("Test Current Checkin", "NUMBERS AFTER" + myevent.getCheckInList().getAttendees().size());

                            //attendee.CheckIn(event);
                            d.updateEvent(myevent);
                            d.updateAttendee(attendee);



                        }

                    }
                }


            });
        }





}