package com.example.checkin;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

// Shows list of checked in attendees for an event
public class CheckedInListOrg extends Fragment {
    private AttendeeList attendeedatalist;
    private ListView attendeesList;
    private ArrayAdapter<Attendee> AttendeesAdapter;
    Event myevent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checked_in_list, container, false);

        attendeesList = view.findViewById(R.id.attendees_list);

        ArrayList<Attendee> attendees = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        // if event exists, get checked in list of attendees
        if (myevent !=null) {
            attendeedatalist = myevent.getCheckInList();
        }

        // if attendeeslist is not null set AttendeesAdapter to custom AttendeeArrayAdapter
        if (attendeedatalist!= null) {
            AttendeesAdapter = new AttendeeArrayAdapter(requireContext(), attendeedatalist.getAttendees());
            attendeesList.setAdapter(AttendeesAdapter);
        }




        return view;
    }
}