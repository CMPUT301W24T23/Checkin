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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class CreateEventFragment extends Fragment {

    private CheckBox checkBoxGeoTracking;
    private EditText eventname;
    private EditText eventdetails;
    private ImageView ivEventPoster;
    private Button btnAddPoster;
    private Organizer organizer;
    private Bitmap poster;          //bitmap of the poster
    private ImageEncoder encoder = new ImageEncoder();      //image encoder for converting image

    private boolean posterAdded = false;
    Button backbutton;

    private Button addeventbutton;

    private Button qrcodebutton;

    private EventList events;

    Event event;

    /**
     * Get the image from the user
     */
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        //ivEventPoster.setImageURI(uri);
                        try {
                            poster = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            ivEventPoster.setImageBitmap(poster);
                            posterAdded = true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
        backbutton = view.findViewById(R.id.backbtn);


        Database database = new Database();
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

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String android_id = preferences.getString("ID", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference organizerRef = db.collection("Organizers").document(android_id);
        organizerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Convert the document snapshot to an Organizer object using Database class method
                        organizer = database.getOrganizer(document);
                        // Proceed with setting up the UI using the retrieved organizer object
                    } else {
                        Log.d("document", "No such document");
                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });



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
                //create event with event name and ID
                if (eventname.getText().toString().equals("")){
                    eventname.setError("Event name required");
                    Log.d("Event Name Required", "User did not supply event name");
                    return;
                }
                event = new Event(eventname.getText().toString(), Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                //get details if any
                event.setEventdetails(eventdetails.getText().toString());
                //convert image to string and add to event
                if (posterAdded){
                    event.setPoster(encoder.BitmapToBase64(poster));
                } else{
                    //empty string if no poster is added
                    event.setPoster("");
                }

                //Add poster to database
                database.updatePoster(event.getPoster(), event.getEventId());

                events.addEvent(event);
                database.updateEvent(event);
                Log.d("Event Creation", String.format("Adding organizer %s event %s to the database", organizer.getUserId(), event.getEventId()));

                organizer.EventCreate(event.getEventId());
                database.updateOrganizer(organizer);

                OrganizerFragment1 organizerfrag = new OrganizerFragment1();
                Bundle args = new Bundle();
                args.putSerializable("organizer", organizer);
                args.putSerializable("eventslist", events);
                organizerfrag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, organizerfrag).addToBackStack(null).commit();

            }
        });

        return view;
    }
}
