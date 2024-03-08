// This class is a Fragment used to create new events. It allows the user to input event details,
// including name, details, and upload poster image. The user can also enable/disable
// geo-tracking for the event. The poster image can be selected from the device's gallery.
// https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
// https://stackoverflow.com/questions/66036757/android-location-gps-track
// https://www.geeksforgeeks.org/how-to-manage-startactivityforresult-on-android/
// https://www.youtube.com/watch?v=OV25x3a55pk
// https://www.youtube.com/watch?v=bLi1qr6h4T4
// https://www.geeksforgeeks.org/how-to-use-activityforresultluncher-as-startactivityforresult-is-deprecated-in-android/
// https://www.youtube.com/watch?v=pHCZpw9JQHk&t=492s
package com.example.checkin;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class CreateEventFragment extends Fragment {

    private CheckBox checkBoxGeoTracking;
    private EditText eventname;
    private EditText eventdetails;
    private ImageView ivEventPoster;
    private Button btnAddPoster;

    private Button addeventbutton;

    private Button qrcodebutton;

    private EventList events;

    Event event;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        ivEventPoster.setImageURI(uri);
                        ivEventPoster.setVisibility(View.VISIBLE);
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        checkBoxGeoTracking = view.findViewById(R.id.checkbox_geo_tracking);
        eventname = view.findViewById(R.id.etEventName);
        eventdetails = view.findViewById(R.id.etEventDetails);
        ivEventPoster = view.findViewById(R.id.ivEventPoster);
        btnAddPoster = view.findViewById(R.id.btnAddPoster);
        addeventbutton = view.findViewById(R.id.createeventbtn);
        qrcodebutton = view.findViewById(R.id.btnGenerateQR);

        btnAddPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            events = (EventList) bundle.getSerializable("eventslist");
        }

        // choose event qr code to be generated
        qrcodebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrcodebutton.setBackgroundColor(Color.GRAY);
            }
        });

        // create new event and open list of events
        addeventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event = new Event();
                event.setEventname(eventname.getText().toString());
                event.setEventdetails(eventdetails.getText().toString());
                events.addEvent(event);
                OrganizerFragment1 organizerfrag = new OrganizerFragment1();
                Bundle args = new Bundle();
                args.putSerializable("eventslist", events);
                organizerfrag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, organizerfrag).addToBackStack(null).commit();

            }
        });
        
        
        


        return view;
    }
}
