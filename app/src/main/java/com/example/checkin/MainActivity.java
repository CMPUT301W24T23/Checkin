package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        a1.setUserId("123");
        Attendee a2 = new Attendee();
        a2.setUserId("456");
        Attendee a3 = new Attendee();
        a3.setUserId("789");
        //AttendeeList aList = new AttendeeList();
        //aList.addAttendee(a1);
        //aList.addAttendee(a2);
        //aList.addAttendee(a3);

        Event e = new Event();
        e.setEventname("epic party");
        //Organizer o = new Organizer();
        //o.EventCreate(e);
        e.userSubs(a2);
        e.userSubs(a1);

        //e.userCheckIn(a1);
        e.userCheckIn(a2);
        e.userCheckIn(a3);




        //Database db1 = new Database();
        //db.storeAttendees(aList);

        /*AttendeeList aList2 = db.loadAttendees();
        for (Attendee a: aList2.getAttendees()){
            a.toggleTracking();
            Log.d("LoadAttendee", String.format("Attendee(%s, %s) loaded successfully", a.getUserId(),
                    a.getName()));
        }*/
        //db1.updateAttendees(aList2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Attendees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        AttendeeList aList = new AttendeeList();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase", document.getId() + " => " + document.getData());
                                String id = document.getId();
                                String name = (String) document.getData().get("Name");
                                String home = (String) document.getData().get("Homepage");
                                String mail = (String) document.getData().get("Email");
                                String phone = (String) document.getData().get("Phone");
                                Boolean tracking = (Boolean) document.getData().get("Tracking");
                                Attendee a = new Attendee(id, name, home, mail, phone, tracking);
                                a.toggleTracking();
                                aList.addAttendee(a);
                            }
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                        Database fire = new Database();
                        fire.updateAttendees(aList);

                    }
                });

        //==========================================================================================
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


    }
}