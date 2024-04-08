package com.example.checkin;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
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

/**
 Represents the Attendee Perspective of the app
 */
public class AttendeeView extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int PERMISSION_REQUEST_NOTIFICATION = 2;
    private FirebaseFirestore db;
    private boolean trackingAllowed = false;
    private final Context globalContext = this;
    private static final int REQUEST_CODE = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //private double curLat = 0, curLon = 0;
    private Location curLocation;

    /**
     * Initializes the activity and sets up its layout and functionality.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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
                    //get attendee and check location permission
                    retrieveAttendee(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

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
                            .addToBackStack(null)
                            .replace(R.id.atten_view, profileFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * Called when the result of an activity launched for result is received.
     * This method is invoked after calling startActivityForResult(Intent, int).
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
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

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        Log.d("Fetching Location", "Fetching Location");
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //updateAttendee();
                    curLocation = location;
                    Log.d("Firebase Retrieve", String.format("Get Location after scan. LAT: %f, LON: %f", location.getLatitude(), location.getLongitude() ));
                }
            }
        });
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
                                retrieveAttendeeForUpload(a, true, event);
                                //myevent.userCheckIn();
                            }

                            Map<String, String> subbedMap = (Map<String, String>) document.get("Subscribers");
                            for(String a : subbedMap.keySet()){
                                //Check each user in to the event
                                //db.getAttendee(document);
                                retrieveAttendeeForUpload(a, false, event);
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



                                            FirebaseMessaging.getInstance().subscribeToTopic(event.getEventId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Subscribe", "subscribe to event!!!!");
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

                        //add location to attendee
                        if(trackingAllowed){
                            attendee.setLat(curLocation.getLatitude());
                            attendee.setLon(curLocation.getLongitude());
                            Log.d("Updating Tracking", String.format("Updating tracking Lat: %f, Lon: %f", curLocation.getLatitude(), curLocation.getLongitude()));
                        }

                        Log.d("Current Tracking", String.format("Current tracking Lat: %f, Lon: %f", attendee.getLat(), attendee.getLon()));

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
    public void retrieveAttendeeForUpload(String id, boolean CheckIn, Event myevent){
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
                            myevent.addToCheckIn(a);
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

    /**
     * opens announcements fragment when a notification is clicked on
     * @param intent The new intent that was started for the activity.
     *
     */
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

    /**
     * Checks if the provided QR code content corresponds to an event.
     * @param qrCodeContent
     * @return
     */
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
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    /**
     * Callback for the result of requesting permissions. This method is invoked when the user responds
     * to the permission request dialog.
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
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

    public void retrieveAttendee(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Attendees").document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "Retrieve attendee: " + document.getData());
                        Database fireBase = new Database();
                        Attendee a = fireBase.getAttendee(document);
                        if(a.trackingEnabled()){
                            Log.d("Tracking Enabled", "Attendee is Retrieved and Tracking Enabled");
                            trackingAllowed = true;
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(globalContext);
                            fetchLastLocation();
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