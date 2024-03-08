package com.example.checkin;

// shows signed in list of attendees to an event
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class SignedInList extends Fragment {

    private AttendeeList attendeedatalist;
    private ListView attendeesList;
    private ArrayAdapter<Attendee> AttendeesAdapter;
    Event myevent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signed_in_list, container, false);

        attendeesList = view.findViewById(R.id.signedin_attendees_list);

        ArrayList<Attendee> attendees = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        // if event exists, get checked in list of attendees
        if (myevent !=null) {
            attendeedatalist = myevent.getSubscribers();
        }

        // if attendeeslist is not null set AttendeesAdapter to custom AttendeeArrayAdapter
        if (attendeedatalist!= null) {
            AttendeesAdapter = new AttendeeArrayAdapter(requireContext(), attendeedatalist.getAttendees());
            attendeesList.setAdapter(AttendeesAdapter);
        }




        return view;
    }
}