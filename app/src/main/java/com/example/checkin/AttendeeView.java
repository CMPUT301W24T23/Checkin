package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

// Represents the Attendee Perspective of the app
public class AttendeeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attenndee_view);

        //onNewIntent(getIntent());






        boolean openAnnouncements = getIntent().getBooleanExtra("open_announcements_fragment", true);

        if (openAnnouncements) {
            // Replace the fragment container with the Announcements fragment
          //  getSupportFragmentManager().beginTransaction()
                    // .replace(R.id.atten_view, new Announcements())
           // .commit();
        }



        // create homepage and announcements fragments
        AttendeeFragment1 att_frg1 = new AttendeeFragment1();
        Announcements ann_frg1 = new Announcements();
        ScanQrCode scan_frag = new ScanQrCode();


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
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.atten_view, scan_frag)
                            .commit();
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
            System.out.println("Worked");
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

   // @Override
    /*protected void onResume() {
        super.onResume();

        Intent notifyIntent = getIntent();
        String extras = getIntent().getStringExtra("open_announcements_fragment");
        if (extras != null && extras.equals("true")) {
            Announcements ann_frg1 = new Announcements(); // Instantiate Announcements fragment here
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.atten_view, ann_frg1)
                    .commit();
        }*/
   // }

    private void getEventDetailsFromFirebase(String qrCodeId) {
        Database database = new Database();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String androidId = preferences.getString("ID", "");
        CollectionReference eventsRef = db.collection("Events");

        eventsRef.whereEqualTo("Qr Code Id", qrCodeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Event details retrieved successfully
                            Event event = database.getEvent(document);
                            // Proceed with event check-in or any other operations

                            // Now, let's fetch the attendee details


                            DocumentReference attendeeRef = db.collection("Attendees").document(androidId);
                            attendeeRef.get().addOnCompleteListener(attendeeTask -> {
                                if (attendeeTask.isSuccessful()) {
                                    DocumentSnapshot attendeeDocument = attendeeTask.getResult();
                                    if (attendeeDocument.exists()) {
                                        // Convert the document snapshot to an Attendee object
                                        Attendee attendee = database.getAttendee(attendeeDocument);
                                        System.out.println("device id" +attendee.getUserId());

                                        // Now you have both the event and the attendee
                                        // You can proceed with the check-in process
                                        if (attendee != null) {
                                            attendee.CheckIn(event);
                                            event.userCheckIn(attendee);
                                            System.out.println("Checked In Attendee");
                                            database.updateEvent(event);
                                            database.updateAttendee(attendee);

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
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Announcements ann_frg1 = new Announcements();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("open_announcements_fragment")) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.atten_view, ann_frg1)
                        .commit();
            }
        }
    }

    private void startQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }




}
