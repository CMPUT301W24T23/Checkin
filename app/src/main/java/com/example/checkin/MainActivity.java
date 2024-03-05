package com.example.checkin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button organizerbutton;
    Button attendeebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        organizerbutton = findViewById(R.id.organizerbtn);
        attendeebutton = findViewById(R.id.attendeebtn);

        //REMOVE BEFORE PUSH=========================================================================

        Attendee a1 = new Attendee();
        Attendee a2 = new Attendee();
        Attendee a3 = new Attendee();
        AttendeeList aList = new AttendeeList();
        aList.addAttendee(a1);
        aList.addAttendee(a2);
        aList.addAttendee(a3);

        Database db = new Database();
        db.storeAttendees2(aList);
/*
        AttendeeList aList = db.loadAttendees();
        for (Attendee a: aList.getAttendees()){
            Log.d("LoadAttendee", String.format("Attendee(%d, %s) loaded successfully", a.getUserId(),
                    a.getName()));
        }*/

        //==========================================================================================
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


    }
}