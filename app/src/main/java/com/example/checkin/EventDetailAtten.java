package com.example.checkin;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// Event details page for Attendee
public class EventDetailAtten extends Fragment {
    Event myevent;
    TextView eventnametxt;
    TextView eventdetails;
    Button backbutton;
    Button eventmessagesbtn;

    Button checkinbutton;
    Button signupbutton;
    Button posterbutton;
    Attendee attendee;


    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        eventnametxt =  view.findViewById(R.id.eventname_text);
        eventdetails = view.findViewById(R.id.eventinfo);
        backbutton = view.findViewById(R.id.backbtn);
        eventmessagesbtn = view.findViewById(R.id.eventmessg);
        checkinbutton  = view.findViewById(R.id.checkinbtn);
        signupbutton =  view.findViewById(R.id.signupbtn);
        posterbutton = view.findViewById(R.id.eventposterbtn);


        db = FirebaseFirestore.getInstance();
        Database database = new Database();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");
        DocumentReference organizerRef = db.collection("Attendees").document(android_id);
        organizerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Organizer object using Database class method
                        attendee = database.getAttendee(document);
                        // Proceed with setting up the UI using the retrieved organizer object
                    } else {
                        Log.d("document", "No such document");
                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });

        checkinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendee.CheckIn(myevent);
                myevent.userCheckIn(attendee);
                database.updateEvent(myevent);
                database.updateAttendee(attendee);


            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendee.setName("testname");
                attendee.EventSub(myevent);
                myevent.userSubs(attendee);
                database.updateEvent(myevent);
                database.updateAttendee(attendee);

            }
        });



        // get event object from previous fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        myevent = (Event) bundle.getSerializable("event");


        // move to announcements page when see announcements button is clicked
        eventmessagesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Announcements announce_frag1= new Announcements();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, announce_frag1).commit();

            }
        });

        // move back to previous fragment when clicked
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        eventnametxt.setText(myevent.getEventname());
        eventdetails.setText(myevent.getEventDetails());

        //Display no poster available if the event does not have a poster
        if (myevent.getPoster().equals("")){
            //no poster for this event
            //posterbutton.setError(String.format("%s has no poster.", myevent.getEventname()));
            posterbutton.setText("No Poster Available");
        }

        //move to poster fragment
        posterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myevent.getPoster().equals("")){
                    //no poster for this event
                    //posterbutton.setError(String.format("%s has no poster.", myevent.getEventname()));
                    return;
                }

                //Create fragment
                EventPosterFrag posterShareFrag = new EventPosterFrag();

                UserImage poster = new UserImage();
                poster.setImageB64(myevent.getPoster());
                poster.setID(myevent.getEventId());

                Bundle args = new Bundle();
                args.putSerializable("Poster", poster);

                posterShareFrag.setArguments(args);
                //ShareCode code_frag = new ShareCode();
                //Bundle args = new Bundle();
                //args.putSerializable("event", myevent);
                //code_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("Poster",args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, posterShareFrag).addToBackStack(null).commit();



                //getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}