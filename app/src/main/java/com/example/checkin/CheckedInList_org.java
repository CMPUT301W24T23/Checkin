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

public class CheckedInList_org extends Fragment {


    private ArrayList<Attendee> attendeedatalist;
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
        assert bundle != null;
        myevent = (Event) bundle.getSerializable("event");

        if (myevent !=null) {
            attendeedatalist = myevent.getCheckInList();
        }

        // if attendeeslist is not null set AttendeesAdapter to custom EventArrayAdapter
        if (attendeedatalist!= null) {
            AttendeesAdapter = new ArrayAdapter<Attendee>(getActivity(), R.layout.content2, attendeedatalist) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.content2, null);
                    }

                    TextView textView = view.findViewById(R.id.attendee_name);
                    textView.setText(attendeedatalist.get(position).getName());
                    return view;
                }
            };
            attendeesList.setAdapter(AttendeesAdapter);
        }




        return view;
    }
}