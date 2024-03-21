// This code defines a custom ArrayAdapter for displaying a list of attendees in a ListView.
// It inflates a layout (content2.xml) for each item in the list and sets the text of a TextView
// (attendee_name) to the name of the attendee at the corresponding position in the list.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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
        String name = "Name: " + attendee.getName();
        attendeename.setText(name);
        TextView attendee_checkintimes = view.findViewById(R.id.checkin_times);
        Map<String, Long> checkIns = attendee.getCheckIns();

        Collection<Long> values = checkIns.values();
        Iterator<Long> iterator = values.iterator();
        // Skip the first value
        iterator.next();
        if (iterator.hasNext()) {
            // Get the second value
            Long secondValue = iterator.next();
            // Convert the value to String
            String numberoftimes = String.valueOf(secondValue);

            String Check_in = "Check In Times: " + numberoftimes;
            attendee_checkintimes.setText(Check_in);

        }
        return view;
    }
}

