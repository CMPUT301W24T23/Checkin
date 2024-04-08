package com.example.checkin;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

// Represents the Attendee Perspective of the app
public class AttendeeView extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int PERMISSION_REQUEST_NOTIFICATION = 2;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attenndee_view);

        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_NOTIFICATION);
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_NOTIFICATION);
        }


        db = FirebaseFirestore.getInstance();
        // create homepage and announcements fragments
        AttendeeFragment1 att_frg1 = new AttendeeFragment1();
        Announcements ann_frg1 = new Announcements();
        AttendeeEventOptions options_frag = new AttendeeEventOptions();



        // move to home page fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.atten_view, options_frag)
                .commit();



        // Set bottom navigation bar
        BottomNavigationView bottomnav = findViewById(R.id.bottomnavbar2);
        // URL: https://www.geeksforgeeks.org/how-to-implement-bottom-navigation-with-activities-in-android/
        bottomnav.setSelectedItemId(R.id.home2);
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home2){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.atten_view, options_frag)
                            .commit();
                    return true;
                }
                else if (item.getItemId() == R.id.qrcodes2){
                    startQRScan();

                    return true;
                }
                else if (item.getItemId() == R.id.messages2){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.atten_view, ann_frg1)
                            .addToBackStack(null)
                            .commit();
                    return true;

                }
                else if (item.getItemId() == R.id.profile){
                    UserProfileFragment profileFragment = new UserProfileFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.atten_view, profileFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

     super.onActivityResult(requestCode, resultCode, data);
    IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    String android_id= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    
    // if the intentResult is null then
    // toast a message as "cancelled"
        if (intentResult != null) {
        if (intentResult.getContents() == null) {
            Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        } else {

            // get content (qr code string from scanned qr code)
            String qrCodeContent = intentResult.getContents();
            // retrive event related to scanned qr code, and check in attendee
            getEventDetailsFromFirebase(qrCodeContent, android_id);
            BottomNavigationView bottomNavigationView2 = findViewById(R.id.bottomnavbar2);
            bottomNavigationView2.setSelectedItemId(R.id.home2);

        }
    } else {
        super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * retrieve event to get the scanned qr code from firebase
     * @param qrCodeId
     */
    private void getEventDetailsFromFirebase(String qrCodeId, String attendeeid) {
        Database database = new Database();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventsRef = db.collection("Events");

        // search for event qr code in database
        eventsRef.whereEqualTo("Event Qr Code Id", qrCodeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // retrieve event
                            Event event = database.getEvent(document);

                            Map<String, String> checkInMap = (Map<String, String>) document.get("UserCheckIn");
                            for(String a : checkInMap.keySet()){
                                //Check each user in to the event
                                //db.getAttendee(document);
                                retrieveAttendee(a, true, event);
                                //myevent.userCheckIn();
                            }

                            Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                            for(String a : subbedMap.keySet()){
                                //Check each user in to the event
                                //db.getAttendee(document);
                                retrieveAttendee(a, false, event);
                                //myevent.userCheckIn();
                            }
                            //wait for execution FOR TESTING
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }


                            //get current attendee and upload
                            fetchAttendeeFromFirestore(attendeeid, true, event);

                            
                            // fetch the attendee details
                            DocumentReference attendeeRef = db.collection("Attendees").document(androidId);
                            attendeeRef.get().addOnCompleteListener(attendeeTask -> {
                                if (attendeeTask.isSuccessful()) {
                                    DocumentSnapshot attendeeDocument = attendeeTask.getResult();
                                    if (attendeeDocument.exists()) {
                                        // Convert the document snapshot to an Attendee object
                                        Attendee attendee = database.getAttendee(attendeeDocument);
                                        System.out.println("device id" +attendee.getUserId());

                                        // get both the event and the attendee
                                        if (attendee != null) {
                                            attendee.updateCheckInCount(event);
                                            //event.userCheckIn(attendee);
                                            //System.out.println("Checked In Attendee");
                                            //database.updateEvent(event);
                                            database.updateAttendee(attendee);


                                            FirebaseMessaging.getInstance().subscribeToTopic(event.getEventId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Subscribe", "subscribe to event");
                                                }
                                            });

                                            signedupeventdetail eventfragment = new signedupeventdetail();
                                            Bundle args = new Bundle();
                                            args.putSerializable("event", event);
                                            eventfragment.setArguments(args);

                                            getSupportFragmentManager().setFragmentResult("event",args);
                                            getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.atten_view, eventfragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                            Toast.makeText(this, "Check In Successful!", Toast.LENGTH_LONG).show();

                                        }
                                    } else {
                                        Log.d("Attendee", "No such document");
                                    }
                                } else {
                                    Log.d("Attendee", "get failed with ", attendeeTask.getException());
                                }
                            });
                        }
                    } else {
                        // Error fetching event details
                        Toast.makeText(this, "Error fetching event details from Firebase", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error fetching event details", task.getException());
                    }
                });

        // search for promotion qr code scanned in database
        eventsRef.whereEqualTo("Promotion QR Code Id", qrCodeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve event
                            Event event = database.getEvent(document);

                            // Open the fragment for unique QR code



                            EventDetailAtten promofrag = new EventDetailAtten();
                            Bundle args = new Bundle();
                            args.putSerializable("event", event);
                            promofrag.setArguments(args);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.atten_view, promofrag)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } else {
                        // Error fetching event details
                        Toast.makeText(this, "Error fetching event details from Firebase", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error fetching event details", task.getException());
                    }
                });


    }

    /**
     * Retreives attendee from firebase and checks them in
     * @param attendeeid
     * @param CheckIn
     * @param myevent
     */
    private void fetchAttendeeFromFirestore(String attendeeid, boolean CheckIn, Event myevent) {

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

    /**
     * Retrieves attendee from firebase
     * @param id
     * @param CheckIn
     * @param myevent
     */
    public void retrieveAttendee(String id, boolean CheckIn, Event myevent){
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
    // opens announcements fragment when a notification is clicked on
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("Intent");


        String action = intent.getAction();
        if (action != null && action.equals("OPEN_ANNOUNCEMENTS_FRAGMENT")) {
            // Perform a fragment transaction to open the Announcements fragment
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavbar2);

            Announcements announce_frg = new Announcements();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.atten_view, announce_frg)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.messages2);


        }

    }

    private boolean isEventQRCode(String qrCodeContent) {
        // Return true if it's an event QR code, false otherwise
        Database database = new Database();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventsRef = db.collection("Events");

        eventsRef.whereEqualTo("Event Qr Code Id", qrCodeContent)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // retrieve event
                            Event event = database.getEvent(document);
                            //return true;
                        }
                    }
                   // return false;
                });

        return false; // Default return value if the task is not successful or no event is found
    }

    /**
     * start qr scan when scan qr code option is selected
     */

    //https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
    private void startQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScan();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else if (requestCode == PERMISSION_REQUEST_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Notification permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }


}