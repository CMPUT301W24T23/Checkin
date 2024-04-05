package com.example.checkin;


import static java.security.AccessController.getContext;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import android.provider.Settings.Secure;
import android.widget.Toast;

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

    //boolean attendExists = false;                 //User exists as Attendee in the database
    //boolean organizerExists;                 //User exists as Attendee in the database
    boolean exists = false;

    private FirebaseFirestore database;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        organizerbutton = findViewById(R.id.organizerbtn);
        attendeebutton = findViewById(R.id.attendeebtn);

        adminButton = findViewById(R.id.adminbtn);

        //String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String android_id = preferences.getString("ID", "");

        Log.d("android id", android_id);

        Database db = new Database();

        String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        System.out.println("ID" + id);



       // if(!(android_id == "")){
            //if ID is stored locally, then user exists already
          //  Log.d("Attendee Exists", String.format("Attendee Exists, ID: %s ", android_id));
          //  exists = true;

       // }

        //String id2 = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);


       // System.out.println(android_id);

       // if (!(exists)){
            //if the uid is not saved then create their attendee and organizer profiles
            //String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
           // Log.d("Not in Database", String.format("Generating Organizer and Attendee, ID: %s ", android_id));
            //create attendee profile
            //Attendee a = new Attendee();
           // a.setUserId(id);
           // db.updateAttendee(a);

            //create organizer profile
           // Organizer o = new Organizer();
           // o.setUserId(id);
          //  db.updateOrganizer(o);


          //  SharedPreferences.Editor editor = preferences.edit();
          //  editor.putString("ID", id);
           // editor.apply();
       // }


        // check if device id, exists in organizer or attendee database collections
        // if yes, do not do anything
        // if not, add them to the database
        database = FirebaseFirestore.getInstance();

        DocumentReference organizerRef = database.collection("Organizers").document(id);
        organizerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Organizer object using Database class method
                        Log.d("document", "Exists");
                        // Proceed with setting up the UI using the retrieved organizer object
                    } else {
                        Organizer o = new Organizer();
                        o.setUserId(id);
                        db.updateOrganizer(o);

                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });


        DocumentReference AttendeeRef = database.collection("Attendees").document(id);
        AttendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Organizer object using Database class method
                        Log.d("document", "Exists");
                        // Proceed with setting up the UI using the retrieved organizer object
                    } else {
                        Attendee a = new Attendee();
                        a.setUserId(id);
                        db.updateAttendee(a);

                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });





        /*
        if(!(attendExists)){
            //if attendee id does not exist in database then add it
            String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
            Attendee a = new Attendee();
            a.setUserId(id);
            db.updateAttendee(a);
            attendExists = true;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ID", id);
            editor.apply();
        }
        if(!(organizerExists)){
            //if organizer id does not exist in database then add it
            String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
            Organizer o = new Organizer();
            o.setUserId(id);
            db.updateOrganizer(o);
            organizerExists = true;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ID", id);
            editor.apply();
        }*/

        //Event e = new Event("RetrieveTest", android_id);
        //Database b = new Database();
        //b.updateEvent(e);



        //getEvent();


        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString("ID", "");
        editor.apply();


        // move to attendee screen when attendee button is clicked
        attendeebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AttendeeView.class);
                startActivity(intent);
            }
        });

        // Move to organizer screen when organizer button is clicked
        organizerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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



}