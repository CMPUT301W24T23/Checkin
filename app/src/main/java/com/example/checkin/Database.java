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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    public Database(){
    }
    //TODO: Profile Picture storing
    //      QR Code Storing

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
        DocumentReference picRef = attendeeRef.document("ProfilePic");


        //Upload check in counts
        Map<String, Long> checkins = a.getCheckIns();
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
     * For use within a snapshot listener to return an attendee
     * @param doc
     * @return
     */
    public Attendee getAttendee(DocumentSnapshot doc){
        Attendee a = new Attendee();
        a.setUserId(doc.getId());
        a.setName(doc.getString("Name"));
        a.setHomepage(doc.getString("Homepage"));
        a.setPhoneNumber(doc.getString("Phone"));
        a.setEmail(doc.getString("Email"));
        a.setProfilePicture(doc.getString("ProfilePic"));

        //set the tracking status of the attendee
        //the empty constructor has tracking as true by default
        boolean track = Boolean.TRUE.equals(doc.getBoolean("Tracking"));
        if(!(track == a.trackingEnabled())){
            a.toggleTracking();
        }

        //get checkins
        Map<String, Object> data = doc.getData();
        Map<String, Long> CheckIns = (Map<String, Long>)data.get("Checkins");
        a.setCheckInHist(CheckIns);

        return a;
    }
    //template snapshot listener for retrieving an attendee
    /*
        String id = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
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

    /**
     * For use within a snapshot listener for firebase, retrieve an organizer
     * @param doc
     * @return
     */
    public Organizer getOrganizer(DocumentSnapshot doc){
        Organizer o = new Organizer();
        //load user info
        o.setUserId(doc.getId());
        o.setAdmin(doc.getBoolean("Admin"));
        boolean track = Boolean.TRUE.equals(doc.getBoolean("Tracking"));
        if(!(track == o.trackingEnabled())){
            o.toggleTracking();
        }
        Map<String, Object> data = doc.getData();

        //Retrieve Events
        //data.get("Events");
        Map<String, String> events = (Map<String, String>)data.get("Events");
        assert events != null;
        for(String key: events.keySet()){
            o.EventCreate(key);
            Log.d("Retrieved Organizer Events", String.format("Organizer %s event %s retrieved", o.getUserId(), key));
        }

        //Retrieve QR Codes
        //data.get("QRCodes");
        Map<String, String> QRCodes = (Map<String, String>)data.get("QRCodes");
        assert QRCodes != null;
        for(String key: QRCodes.keySet()){
            o.QRCreate(key);
            Log.d("Retrieved Organizer QRCodes", String.format("Organizer %s QRCode %s retrieved", o.getUserId(), key));
        }

        Log.d("Retrieved Organizer", String.format("Organizer ID: %s ", o.getUserId()));
        return o;
    }

    //template snapshot listener function content for retrieving an organizer
    /*
        String id = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Organizers").document(id);

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

    public Event getEvent(DocumentSnapshot doc){
        Organizer o = new Organizer();
        Event e = new Event(doc.getId());
        //load user info

        e.setEventname(doc.getString("Name"));
        e.setEventdetails(doc.getString("Details"));
        e.setPoster(doc.getString("Poster"));
        e.setCreator(doc.getString("Creator"));

        Map<String, Object> data = doc.getData();

        //Retrieve Subscribers
        Map<String, String> Subs = (Map<String, String>)data.get("Subscribers");
        for(String subber: Subs.keySet()){
            DocumentReference docRef = db.collection("Attendees").document(subber);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firebase Succeed", "DocumentSnapshot data: " + document.getData());
                            Attendee a = getAttendee(document);
                            e.userSubs(a);
                        } else {
                            Log.d("Firebase", String.format("No such document: %s", subber));
                        }
                    } else {
                        Log.d("Firebase get failed", "get failed with ", task.getException());
                    }
                }
            });
        }

        //Retrieve Checked In users
        Map<String, String> Users = (Map<String, String>)data.get("UserCheckIn");
        for(String user: Subs.keySet()){
            DocumentReference docRef = db.collection("Attendees").document(user);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firebase Succeed", "DocumentSnapshot data: " + document.getData());
                            Attendee a = getAttendee(document);
                            e.userCheckIn(a);
                        } else {
                            Log.d("Firebase", String.format("No such document: %s", user));
                        }
                    } else {
                        Log.d("Firebase get failed", "get failed with ", task.getException());
                    }
                }
            });
        }
        Log.d("Retrieved Event", String.format("Event ID: %s ", e.getEventId()));
        return e;
    }

        /*
    public void getOrg(){
        String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        FirebaseFirestore fireb = FirebaseFirestore.getInstance();
        DocumentReference docRef = fireb.collection("Organizers").document(id);
        Database db = new Database();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "DocumentSnapshot data: " + document.getData());
                        Organizer o = db.getOrganizer(document);
                        for(String QR: o.getQRCodes()){
                            Log.d("QR CODE", String.format("CODE: %s", QR));
                        }

                    } else {
                        Log.d("Firebase", String.format("No such document: %s", id));
                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });}
        */
    /*
    public void getEvent(){
        FirebaseFirestore fireb = FirebaseFirestore.getInstance();
        DocumentReference docRef = fireb.collection("Events").document("983");
        Database db = new Database();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "DocumentSnapshot data: " + document.getData());
                        Event e = db.getEvent(document);
                        for(Attendee a: e.getSubscribers().getAttendees()){
                            Log.d("Successful Event Retrieve", String.format("Retrieved user %s", a.getUserId()));
                        }

                    } else {
                        Log.d("Firebase", String.format("No such document: %s", "983"));
                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });}
        */


    //use/modify this code if you need to load all the attendees for whatever reason
    //Firebase data pulls must be done asynchronously through listeners
    //https://firebase.google.com/docs/firestore/query-data/get-data#java_2

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