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
    private EditText eventDate;
    private EditText eventTime;
    private EditText eventDetails;
    private ImageView ivEventPoster;
    private Button btnAddPoster;
    private Organizer organizer;
    private Bitmap poster;
    private ImageEncoder encoder = new ImageEncoder();

    private boolean posterAdded = false;
    private Button backbutton;
    private Button addeventbutton;
    private Button qrcodebutton;
    private Button btnMap;
    private EventList events;
    private Event event;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
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
        eventDate = view.findViewById(R.id.etEventDate);
        eventTime = view.findViewById(R.id.etEventTime);
        eventDetails = view.findViewById(R.id.etEventdetails);
        ivEventPoster = view.findViewById(R.id.ivEventPoster);
        btnAddPoster = view.findViewById(R.id.btnAddPoster);
        addeventbutton = view.findViewById(R.id.createeventbtn);
        qrcodebutton = view.findViewById(R.id.btnGenerateQR);
        backbutton = view.findViewById(R.id.backbtn);
        btnMap = view.findViewById(R.id.btnMap);

        Database database = new Database();
        btnAddPoster.setOnClickListener(v -> mGetContent.launch("image/*"));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            events = (EventList) bundle.getSerializable("eventslist");
        }

        backbutton.setOnClickListener(view1 -> getActivity().getSupportFragmentManager().popBackStack());

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
                        organizer = database.getOrganizer(document);
                    } else {
                        Log.d("document", "No such document");
                    }
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });

        qrcodebutton.setOnClickListener(view12 -> qrcodebutton.setBackgroundColor(Color.GRAY));

        addeventbutton.setOnClickListener(view13 -> {
            String eventName = eventname.getText().toString().trim();
            String eventDateStr = eventDate.getText().toString().trim();
            String eventTimeStr = eventTime.getText().toString().trim();
            String eventDetailsStr = eventDetails.getText().toString().trim();

            if (eventName.isEmpty()) {
                eventname.setError("Event name is required");
                return;
            }
            if (eventDateStr.isEmpty()) {
                eventDate.setError("Event date is required");
                return;
            }
            if (eventTimeStr.isEmpty()) {
                eventTime.setError("Event time is required");
                return;
            }
            if (eventDetailsStr.isEmpty()) {
                eventDetails.setError("Event details are required");
                return;
            }

            event = new Event(eventName, Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            event.setEventDate(eventDateStr);
            event.setEventTime(eventTimeStr);
            event.setEventDetails(eventDetailsStr);

            if (posterAdded) {
                event.setPoster(encoder.BitmapToBase64(poster));
            } else {
                event.setPoster("");
            }

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
        });

        btnMap.setOnClickListener(view14 -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
