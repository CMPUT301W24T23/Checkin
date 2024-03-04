package com.example.checkin;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
// Attendee Array Adapter that contains Attendees

public class AttendeeArrayAdapter extends ArrayAdapter<Attendee> {
    private ArrayList<Attendee> attendees;
    private Context context;

    public AttendeeArrayAdapter(Context context, ArrayList<Attendee> attendees) {
        super(context, 0, attendees);
        this.attendees = attendees;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content2,
                    parent, false);
        } else {
            view = convertView;
        }
        Attendee attendee = getItem(position);

        TextView attendeename = view.findViewById(R.id.attendee_name);


        attendeename.setText(attendee.getName());



        return view;
    }
}

