package com.example.checkin;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is for updating the other class objects to firebase
 *
 * Firebase Database: https://console.firebase.google.com/u/0/project/checkin-6a54e/firestore/data/~2FAttendees~2F10
 *
 * Guide for getting data from firebase:
 * https://firebase.google.com/docs/firestore/query-data/get-data#java_2
 * Another example:
 * https://stackoverflow.com/a/63700530
 *
 * I also have some sample code at the bottom that should go onto an activity in order to retrieve
 * the data
 *
 */
public class Database {
    private FirebaseFirestore db;
    public Database(){
    }


    /**
     * Update an attendee in firestore
     * @param a
     * a valid Attendee object, presumably with some aspect of it changed from what is on the cloud
     */
    public void updateAttendee(Attendee a) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendeeRef = db.collection("Attendees");

        //Upload User info
        Map<String, Object> data = new HashMap<>();
        data.put("Name", a.getName());
        data.put("Homepage", a.getHomepage());
        data.put("Email", a.getEmail());
        data.put("Phone", a.getPhoneNumber());
        data.put("Tracking", a.trackingEnabled());
        data.put("ProfilePic", a.getProfilePicture());

        //Upload check in counts
        Map<String, Integer> checkins = a.getCheckIns();
        data.put("Checkins", checkins);

        attendeeRef.document(a.getUserId()).set(data);
        Log.d("New Attendee", String.format("Added Attendee to Firebase, ID: %s", a.getUserId()));
    }

    /**
     * Update a single organizer class on firebase
     * @param o
     */
    public void updateOrganizer(Organizer o){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference orgRef = db.collection("Organizers");

        //Upload User info
        Map<String, Object> data = new HashMap<>();
        data.put("Tracking", o.trackingEnabled());
        data.put("Admin", o.isAdmin());

        //Upload created events array
        Map<String, String> events = new HashMap<>();
        for (String eventId: o.getCreatedEvents()){
            events.put(eventId, "");
        }
        data.put("Events", events);

        //upload created qr codes array
        Map<String, String> qrCodes = new HashMap<>();
        for(String QR: o.getQRCodes()){
            events.put(QR, "");
        }
        data.put("QRCodes", qrCodes);

        orgRef.document(o.getUserId()).set(data);
        Log.d("New Organizer", String.format("Added Organizer to Firebase, ID: %s", o.getUserId()));
    }

    /**
     * Update an event onto firebase
     * @param e
     */
    public void updateEvent(Event e){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendeeRef = db.collection("Events");

        //Upload Event info
        Map<String, Object> data = new HashMap<>();
        data.put("Name", e.getEventname());
        data.put("Details", e.getEventdetails());
        data.put("Poster", e.getPoster());
        data.put("Creator", e.getCreator());

        //Upload userIds of subscribers
        Map<String, String> subs = new HashMap<>();
        for (Attendee a: e.getSubscribers().getAttendees()){
            subs.put(a.getUserId(), "");
        }

        data.put("Subscribers", subs);

        //Upload userIds of Checked in users
        Map<String, String> checkedIn = new HashMap<>();
        for (Attendee a: e.getCheckInList().getAttendees()){
            checkedIn.put(a.getUserId(), "");
        }
        data.put("UserCheckIn", checkedIn);


        Log.d("UpdateEvent", String.format("Event(%s, %s)", e.getEventId(), e.getEventname()));
        attendeeRef.document(e.getEventId()).set(data);

    }

    /**
     * Retrieves an attendee from the database using their id
     * @param id
     * the user's ID (device id)
     * @return
     * the Attendee object containing their information
     */
    public Attendee getAttendee(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Attendee a = new Attendee(id);

        DocumentReference docRef = db.collection("Attendees").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "Retrieve attendee: " + document.getData());
                        a.setUserId(document.getId());
                        a.setName(document.getString("Name"));
                        a.setHomepage(document.getString("Homepage"));
                        a.setPhoneNumber(document.getString("Phone"));

                        //set the tracking status of the attendee
                        //the empty constructor has tracking as true by default
                        boolean track = Boolean.TRUE.equals(document.getBoolean("Tracking"));
                        if(!(track == a.trackingEnabled())){
                            a.toggleTracking();
                        }


                    } else {
                        Log.d("Firebase", "No such document");
                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });
        return a;
    }

    /**
     * For use within an
     * @param doc
     * @return
     */
    public Attendee getAttendee(DocumentSnapshot doc){
        Attendee a = new Attendee();
        Log.d("Firebase Succeed", "Retrieve attendee: " + doc.getData());
        a.setUserId(doc.getId());
        a.setName(doc.getString("Name"));
        a.setHomepage(doc.getString("Homepage"));
        a.setPhoneNumber(doc.getString("Phone"));

        //set the tracking status of the attendee
        //the empty constructor has tracking as true by default
        boolean track = Boolean.TRUE.equals(doc.getBoolean("Tracking"));
        if(!(track == a.trackingEnabled())){
            a.toggleTracking();
        }

        return a;
    }
    /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Attendees").document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "DocumentSnapshot data: " + document.getData());


                    } else {
                        Log.d("Firebase", "No such document");
                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });
    */



    public boolean OrganizerExists(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final boolean[] exists = {false};

        DocumentReference docRef = db.collection("Organizers").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "DocumentSnapshot data: " + document.getData());
                        exists[0] = true;

                    } else {
                        Log.d("Firebase", "No such document");
                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });
        return exists[0];
    }
    //use/modify this code if you need to load all the attendees for whatever reason
    //To my understanding, retrieving documents from firebase has to be done in a way that doesn't allow
    //for a return value. So it has to be done on the actual activity and you can't create a method for it

    /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Attendees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase", document.getId() + " => " + document.getData());
                                //MODIFY SOME GLOBAL VARIABLE
                            }
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });
    */

}