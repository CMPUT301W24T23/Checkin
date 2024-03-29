package com.example.checkin;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

// Represents the Attendee Perspective of the app
public class AttendeeView extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int PERMISSION_REQUEST_NOTIFICATION = 2;

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


        // create homepage and announcements fragments
        AttendeeFragment1 att_frg1 = new AttendeeFragment1();
        Announcements ann_frg1 = new Announcements();



        // move to home page fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.atten_view, att_frg1)
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
                            .replace(R.id.atten_view, att_frg1)
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
    // if the intentResult is null then
    // toast a message as "cancelled"
        if (intentResult != null) {
        if (intentResult.getContents() == null) {
            Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            // if the intentResult is not null we'll set
            // the content and format of scan message
            String qrCodeContent = intentResult.getContents();
            System.out.println("content"+ qrCodeContent);
            getEventDetailsFromFirebase(qrCodeContent);




            // check in attendee using firebase- use event id and attendee id to get
            // event and attendee from firebase, and update both

            EventDetailAtten eventfragment = new EventDetailAtten();
            Bundle args = new Bundle();
            args.putString("event", intentResult.getContents());
            eventfragment.setArguments(args);
        }
    } else {
        super.onActivityResult(requestCode, resultCode, data);
    }
}


    /**
     * retrieve event to get the scanned qr code from firebase
     * @param qrCodeId
     */
    private void getEventDetailsFromFirebase(String qrCodeId) {
        Database database = new Database();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String androidId = preferences.getString("ID", "");
        CollectionReference eventsRef = db.collection("Events");

        eventsRef.whereEqualTo("Event Qr Code Id", qrCodeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // retrieve event
                            Event event = database.getEvent(document);
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
                                            attendee.CheckIn(event);
                                            event.userCheckIn(attendee);
                                            System.out.println("Checked In Attendee");
                                            database.updateEvent(event);
                                            database.updateAttendee(attendee);

                                            FirebaseMessaging.getInstance().subscribeToTopic(event.getEventId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Subscribe", "subscribe to event");
                                                }
                                            });

                                            EventDetailAtten eventfragment = new EventDetailAtten();
                                            Bundle args = new Bundle();
                                            args.putSerializable("event", event);
                                            eventfragment.setArguments(args);

                                            getSupportFragmentManager().setFragmentResult("event",args);
                                            getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.atten_view, eventfragment)
                                                    .addToBackStack(null)
                                                    .commit();

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

        eventsRef.whereEqualTo("Unique QR Code", qrCodeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve event
                            Event event = database.getEvent(document);

                            // Open the fragment for unique QR code
                            PromotionFragment promofrag = new PromotionFragment();
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
