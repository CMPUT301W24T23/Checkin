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

    Button checkoutbtn;

    private FirebaseFirestore db;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        eventnametxt =  view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventinfo);
        backbutton = view.findViewById(R.id.backbtn);
        checkinbutton  = view.findViewById(R.id.checkinbtn);
        checkoutbtn  = view.findViewById(R.id.checkoutbtn);
        signupbutton =  view.findViewById(R.id.signupbtn);
        posterbutton = view.findViewById(R.id.eventposterbtn);
        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        db = FirebaseFirestore.getInstance();

        System.out.println("checkincount first "+ myevent.getCheckInList().getAttendees().size());
        String eventid = myevent.getEventId();

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
                                //Event will have empty lists
                                //Event event = database.getEvent(document);
                                myevent = database.getEvent(document);
                                System.out.println("checkinpeople first "+ myevent.getCheckInList().getAttendees().size());

                                //Populate event lists
                                Map<String, String> checkInMap = (Map<String, String>) document.get("UserCheckIn");
                                for(String a : checkInMap.keySet()) {
                                    //Retrieve checked in users
                                    retrieveAttendee(a, true);
                                }
                                Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                                for(String a : subbedMap.keySet()){
                                    //Retrieve subscribed users
                                    retrieveAttendee(a, false);
                                }

                                //wait for execution FOR TESTING
                                //delay for the previous async calls
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                //get current attendee and upload
                                //pass true because the user is checking in
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

        checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fetch the attendee

                // Fetch the event
                fetchEvent(myevent.getEventId(), new OnSuccessListener<Event>() {
                    @Override
                    public void onSuccess(Event event) {
                        // Update 'myevent' with the fetched event details
                        //myevent = event;

                        // Perform the check-in process
                        fetchAttendeeForCheckout(android_id, event);
                        //database1.updateEvent(myevent);
                    }
                });
            }
        });



        db = FirebaseFirestore.getInstance();

        System.out.println("checkincount first "+ myevent.getCheckInList().getAttendees().size());



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
                                    //call retrieve Attendee with true
                                    //indicates that this is for getting the checkins
                                    retrieveAttendee(a, true);
                                }

                                Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                                for(String a : subbedMap.keySet()){
                                    //call retrieveAttendee with 'false'
                                    //indicates that this is for getting the subscribers
                                    retrieveAttendee(a, false);
                                }

                                //short wait, allows for the previous async calls to catch up
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                //get current attendee and upload
                                //call with false because this is for signing up for notifications
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
        if (myevent.getPoster().isEmpty()){
            //no poster for this event
            posterbutton.setText("No Poster Available");
        }

        //move to poster fragment
        posterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myevent.getPoster().equals("")){
                    //no poster for this event
                    return;
                }
                //Create fragment
                EventPosterFrag posterShareFrag = new EventPosterFrag();

                //convert poster and pass to fragment
                UserImage poster = new UserImage();
                poster.setImageB64(myevent.getPoster());
                poster.setID(myevent.getEventId());

                Bundle args = new Bundle();
                args.putSerializable("Poster", poster);
                posterShareFrag.setArguments(args);
                getParentFragmentManager().setFragmentResult("Poster",args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, posterShareFrag).addToBackStack(null).commit();
            }
        });

        return view;
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

    /**
     * Fetch the current attendee from firestore, check them in or sub them, and then update to the database
     * @param attendeeid
     * the ID of an attendee to be checked in or subbed to the current event
     * @param CheckIn
     * boolean indicating whether they are checking in or subscribing
     */
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
                            //System.out.println("NUMBERS " + myevent.getCheckInList().getAttendees().size());
                            Log.d("Test Current Checkin", "NUMBERS AFTER" + myevent.getCheckInList().getAttendees().size());
                        }
                        //Otherwise, user is subscribing.
                        else{
                            Log.d("Test Current Checkin", "NUMBERS BEFORE" + myevent.getSubscribers().getAttendees().size());
                            myevent.userSubs(attendee);
                            Log.d("Test Current Checkin", "NUMBERS AFTER" + myevent.getSubscribers().getAttendees().size());
                        }
                        //update the event in the database
                        d.updateEvent(myevent);
                        //update the attendee in the database
                        d.updateAttendee(attendee);
                    }
                }
            }
        });
    }

    private void fetchAttendeeForCheckout(String attendeeid, Event event) {

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
                        System.out.println("NUMBERS BEFORE" + event.getCheckInList().getAttendees().size());
                        //event.userCheckOut(attendee);
                        System.out.println("NUMBERS " + event.getCheckInList().getAttendees().size());
                        attendee.CheckIn(event);
                        d.updateEvent(event);
                        d.updateAttendee(attendee);

                    }

                }
            }


        });
    }

}