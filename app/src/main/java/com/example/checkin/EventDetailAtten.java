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

        eventnametxt = view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventinfo);
        backbutton = view.findViewById(R.id.backbtn);
        checkinbutton = view.findViewById(R.id.checkinbtn);
        signupbutton = view.findViewById(R.id.signupbtn);
        posterbutton = view.findViewById(R.id.eventposterbtn);
        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        db = FirebaseFirestore.getInstance();

        System.out.println("checkincount first " + myevent.getCheckInList().getAttendees().size());
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
                                myevent = database.getEvent(document);
                                System.out.println("checkinpeople first " + myevent.getCheckInList().getAttendees().size());

                                Map<String, String> checkInMap = (Map<String, String>) document.get("UserCheckIn");
                                for (String a : checkInMap.keySet()) {
                                    retrieveAttendee(a, true, myevent);
                                }
                                Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                                for (String a : subbedMap.keySet()) {
                                    retrieveAttendee(a, false, myevent);
                                }

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                fetchAttendeeFromFirestore(android_id, true);
                                System.out.println("checkinpeople last " + myevent.getCheckInList().getAttendees().size());

                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });

                FirebaseMessaging.getInstance().subscribeToTopic(myevent.getEventId())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Subscribe", "subscribe to event");
                            }
                        });
            }
        });


        db = FirebaseFirestore.getInstance();

        System.out.println("checkincount first " + myevent.getCheckInList().getAttendees().size());

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
                                myevent = database.getEvent(document);
                                System.out.println("checkinpeople first " + myevent.getCheckInList().getAttendees().size());

                                Map<String, String> checkInMap = (Map<String, String>) document.get("UserCheckIn");
                                for (String a : checkInMap.keySet()) {
                                    retrieveAttendee(a, true, myevent);
                                }

                                Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                                for (String a : subbedMap.keySet()) {
                                    retrieveAttendee(a, false, myevent);
                                }

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                fetchAttendeeFromFirestore(android_id, false);

                                System.out.println("checkinpeople last " + myevent.getCheckInList().getAttendees().size());

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

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventdetails());

        if (myevent.getPoster().isEmpty()) {
            posterbutton.setText("No Poster Available");
        }

        posterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myevent.getPoster().equals("")) {
                    return;
                }
                EventPosterFrag posterShareFrag = new EventPosterFrag();
                UserImage poster = new UserImage();
                poster.setImageB64(myevent.getPoster());
                poster.setID(myevent.getEventId());

                Bundle args = new Bundle();
                args.putSerializable("Poster", poster);
                posterShareFrag.setArguments(args);
                getParentFragmentManager().setFragmentResult("Poster", args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, posterShareFrag).addToBackStack(null).commit();
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

    public void retrieveAttendee(String id, boolean CheckIn, Event event) {
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
                        if (CheckIn) {
                            myevent.addToCheckIn(a);
                            //a.CheckIn(myevent);
                            fireBase.updateAttendee(a);
                        } else {
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
                        Attendee attendee = d.getAttendee(document);
                        if (CheckIn) {
                            myevent.userCheckIn(attendee);
                        } else {
                            myevent.userSubs(attendee);
                        }
                        d.updateEvent(myevent);
                        d.updateAttendee(attendee);
                    }
                }
            }
        });
    }

}