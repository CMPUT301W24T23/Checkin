// https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
// https://stackoverflow.com/questions/66036757/android-location-gps-track
// https://www.geeksforgeeks.org/how-to-manage-startactivityforresult-on-android/
// https://www.youtube.com/watch?v=OV25x3a55pk
// https://www.youtube.com/watch?v=bLi1qr6h4T4
// https://www.geeksforgeeks.org/how-to-use-activityforresultluncher-as-startactivityforresult-is-deprecated-in-android/
// https://www.youtube.com/watch?v=pHCZpw9JQHk&t=492s
package com.example.checkin;

import android.content.Intent;
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
    private EventList events;
    private Event event;

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

        btnAddPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        return view;
    }
}