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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

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



        Bundle bundle = this.getArguments();
        assert bundle != null;
        myevent = (Event) bundle.getSerializable("event");
        String eventid = myevent.getEventId();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        checkinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAttendee(new OnSuccessListener<Attendee>() {
                    @Override
                    public void onSuccess(Attendee attendee) {
                        attendee.CheckIn(myevent);
                        myevent.userCheckIn(attendee);


                        FirebaseMessaging.getInstance().subscribeToTopic(eventid).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Subscribe", "subscribe to event");
                            }
                        });

                        Database database = new Database();
                        database.updateEvent(myevent);
                        database.updateAttendee(attendee);

                    }
                });
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAttendee(new OnSuccessListener<Attendee>() {
                    @Override
                    public void onSuccess(Attendee attendee) {
                        attendee.setName("testname");
                        attendee.EventSub(myevent);
                        myevent.userSubs(attendee);

                        Database database = new Database();
                        database.updateEvent(myevent);
                        database.updateAttendee(attendee);

                        Toast.makeText(getContext(), "You Have Signed Up!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



        // get event object from previous fragment



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
        eventdetails.setText(myevent.getEventdetails());

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

    private void fetchAttendee(OnSuccessListener<Attendee> onSuccessListener) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Database database = new Database();
        DocumentReference attendeeRef = db.collection("Attendees").document(android_id);
        attendeeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Attendee object using Database class method
                        Attendee attendee = database.getAttendee(document);
                        onSuccessListener.onSuccess(attendee);
                    } else {
                        Log.d("document", "No such document");
                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });
    }
}