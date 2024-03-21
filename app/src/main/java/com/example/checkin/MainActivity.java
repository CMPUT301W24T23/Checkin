package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
/*
`MainActivity` serves as the central hub for the event-check-in app,
offering user-friendly navigation and dynamic ID generation based on roles.
 */
public class MainActivity extends AppCompatActivity {

    Button organizerbutton;
    Button attendeebutton;
    Button adminButton;

    static String AttendId;         //User's Attendee ID
    static String OrgId;            //User's Organizer ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        organizerbutton = findViewById(R.id.organizerbtn);
        attendeebutton = findViewById(R.id.attendeebtn);

        adminButton = findViewById(R.id.adminbtn);
        //String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String aID = preferences.getString("attendeeId", null); //attendee id
        String oID = preferences.getString("organizerId", null);

        //Generate a new ID if none is stored
        if (aID == null){
            generateAttendeeId();
        }
        if (oID == null){
            generateOrganizerId();
        }

        // move to attendee screen when attendee button is clicked
        attendeebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //generate a new attendee ID and upload to database if none is stored locally
                if (aID == null) {
                    Log.d("ID Generated", String.format("Generated ID: %s", AttendId));

                    //create a new Attendee object and upload to fireStore
                    Attendee a = new Attendee(AttendId, "", "", "", "", false);
                    Database db = new Database();
                    db.updateAttendee(a);

                    //save id locally to preferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("attendeeId", AttendId);
                    editor.apply();

                    Log.d("New User", String.format("Created Attendee: %s", AttendId));
                }

                //Loads ID
                AttendId = preferences.getString("attendeeId", null);
                Log.d("Loaded User", String.format("Loaded Attendee: %s", AttendId));

                Intent intent = new Intent(getApplicationContext(), AttendeeView.class);
                startActivity(intent);
            }
        });

        // Move to organizer screen when organizer button is clicked
        organizerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generate a new organizer ID and upload to database if none is stored locally
                if (oID == null) {
                    Log.d("ID Generated", String.format("Generated Organizer ID: %s", OrgId));

                    //create a new Attendee object and upload to firestore
                    Organizer o = new Organizer(OrgId);
                    Database db = new Database();
                    db.updateOrganizer(o);

                    //save id locally to preferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("organizerId", OrgId);
                    editor.apply();

                    Log.d("New Organizer", String.format("Created Organizer: %s", OrgId));
                }

                //Loads id
                OrgId = preferences.getString("organizerId", null);
                Log.d("Loaded Organizer", String.format("Loaded Organizer: %s", OrgId));


                Intent intent = new Intent(getApplicationContext(), OrganizerView.class);
                startActivity(intent);

            }
        });

        // Move to administrator screen when administrator button is clicked.
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AdministratorMainView.class);
                startActivity(intent);
            }
        });


    }

    /**
     * This generates a new Attendee ID for the user
     * This will only be called if none is stored locally in preferences
     */
    public void generateAttendeeId(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Generating ID", "Now generating Attendee id...");
        db.collection("Attendees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int newId = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase", document.getId() + " => " + document.getData());
                                newId = Integer.parseInt(document.getId());
                            }
                            newId += 1;
                            //Set generated user id to 1 higher than last value
                            AttendId = String.valueOf(newId);
                        } else {
                            Log.d("Firebase", "Error getting Attendee documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * This generates a new Organizer ID for the user
     * This will only be called if none is stored locally in preferences
     */
    public void generateOrganizerId(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Generating ID", "Now generating Organizer id...");
        db.collection("Organizers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int newId = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase", document.getId() + " => " + document.getData());
                                newId = Integer.parseInt(document.getId());
                            }
                            newId += 1;
                            //Set generated user id to 1 higher than last value
                            OrgId = String.valueOf(newId);
                        } else {
                            Log.d("Firebase", "Error getting Organizer documents: ", task.getException());
                        }
                    }
                });
    }
}