package com.example.checkin;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Database {
    FirebaseFirestore db;
    public Database(){}


    /* OLD STORE ATTENDEES, does not store the checkins
    public void storeAttendees(AttendeeList aList){
        for (Attendee a: aList.getAttendees()){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference attendeeRef = db.collection("Attendees");

            HashMap<String, String> data = new HashMap<>();
            data.put("Name", a.getName());
            data.put("Homepage", a.getHomepage());
            data.put("Email", a.getEmail());
            data.put("Phone", a.getPhoneNumber());
            data.put("Tracking", Boolean.toString(a.trackingEnabled()));

            attendeeRef.document(Integer.toString(a.getUserId())).set(data);

            //attendeeRef.set(a);
        }
    }*/

    public void storeAttendees(AttendeeList aList){
        for (Attendee a: aList.getAttendees()){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference attendeeRef = db.collection("Attendees");


            Map<String, Object> data = new HashMap<>();
            data.put("Name", a.getName());
            data.put("Homepage", a.getHomepage());
            data.put("Email", a.getEmail());
            data.put("Phone", a.getPhoneNumber());
            data.put("Tracking", a.trackingEnabled());


            //Stores the check in list as a key value pair of (Event Id : # of checkins)
            Map<String, Object> checkins = new HashMap<>();
            EventList events = a.getCheckIns();
            for (Event e : events.getEvents()){
                checkins.put(Integer.toString(e.getEventId()), Collections.frequency(a.getCheckIns().getEvents(), e));
            }
            data.put("Checkins", checkins);

            attendeeRef.document(Integer.toString(a.getUserId())).set(data);

            //attendeeRef.set(a);
        }
    }
    /*
    public AttendeeList loadAttendees(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //CollectionReference attendeeRef = db.collection("Attendees");
        AttendeeList aList = new AttendeeList();

        //ArrayList<String> uids = new ArrayList<String>();


        DocumentReference attendeeRef = db.collection("Attendees").document("SF");
        attendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return aList;
    }

     */
}
