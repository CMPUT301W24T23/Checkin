
package com.example.checkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

 import com.example.checkin.Attendee;

 import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 This code defines a custom ArrayAdapter for displaying a list of attendees in a ListView.
 It inflates a layout (content2.xml) for each item in the list and sets the text of a TextView
 (attendee_name) to the name of the attendee at the corresponding position in the list.
 */
public class AttendeeArrayAdapter extends ArrayAdapter<Attendee> {
    private ArrayList<Attendee> attendees;
    private Context context;

    /**
     * Constructor for the AttendeeArrayAdapter class.
     * @param context
     * @param attendees
     */
    public AttendeeArrayAdapter(Context context, ArrayList<Attendee> attendees) {
        super(context, 0, attendees);
        this.attendees = attendees;
        this.context = context;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     */
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

        Long numberoftimes = attendee.getCheckInValue();
        String Check_in = "Check In Times: " + numberoftimes;
        attendee_checkintimes.setText(Check_in);


        return view;
    }
}

