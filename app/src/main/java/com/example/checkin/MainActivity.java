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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.provider.Settings.Secure;

import java.util.ArrayList;
import java.util.HashMap;
/*
`MainActivity` serves as the central hub for the event-check-in app,
offering user-friendly navigation and dynamic ID generation based on roles.
 */
public class MainActivity extends AppCompatActivity {

    Button organizerbutton;
    Button attendeebutton;
    boolean exists = false;
    Organizer o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        organizerbutton = findViewById(R.id.organizerbtn);
        attendeebutton = findViewById(R.id.attendeebtn);
        //String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String android_id = preferences.getString("ID", "");


        Database db = new Database();


        if(!(android_id == "")){
            //if ID is stored locally, then user exists already
            Log.d("Attendee Exists", String.format("Attendee Exists, ID: %s ", android_id));
            exists = true;

        }
        String id2 = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);


        if (!(exists)){
            //if the uid is not saved then create their attendee and organizer profiles
            String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

            //create attendee profile
            Attendee a = new Attendee();
            a.setUserId(id);
            db.updateAttendee(a);

            //create organizer profile
            o = new Organizer();
            o.setUserId(id);
            db.updateOrganizer(o);


            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ID", id);
            editor.apply();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ID", "");
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
                Bundle args = new Bundle();
                args.putSerializable("organizer", o);
                startActivity(intent);
            }
        });


    }



}