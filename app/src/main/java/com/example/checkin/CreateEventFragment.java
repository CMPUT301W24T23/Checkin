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

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private ImageView qrcodeimage;
    private ImageView uniqueqrcodeimage;
    private boolean posterAdded = false;
    private Button backbutton;
    private Button addeventbutton;
    private Button qrcodebutton;
    private Button btnMap;
    private Button btnUseExistingQR;
    private boolean qrCodeOptionSelected = false;
    private EventList events;
    private Event event;
    boolean createqr;

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
        qrcodeimage = view.findViewById(R.id.qrcodeimage);
        uniqueqrcodeimage = view.findViewById(R.id.uniquecodeimage);
        btnUseExistingQR = view.findViewById(R.id.btnUseExistingQR);

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

        qrcodebutton.setOnClickListener(view12 -> {
            qrCodeOptionSelected = true;
            qrcodebutton.setBackgroundColor(Color.GRAY);
            // Your existing code for generating QR code
        });

        btnUseExistingQR.setOnClickListener(view12 -> {
            qrCodeOptionSelected = true;
            // Your code for using an existing QR code
        });

        addeventbutton.setOnClickListener(view13 -> {
            String eventName = eventname.getText().toString().trim();
            String eventDateStr = eventDate.getText().toString().trim();
            String eventTimeStr = eventTime.getText().toString().trim();
            String eventDetailsStr = eventDetails.getText().toString().trim();

            boolean hasError = false;

            if (eventName.isEmpty()) {
                eventname.setError("Required");
                hasError = true;
            }

            if (eventDateStr.isEmpty()) {
                eventDate.setError("Required");
                hasError = true;
            }

            if (eventTimeStr.isEmpty()) {
                eventTime.setError("Required");
                hasError = true;
            }

            if (eventDetailsStr.isEmpty()) {
                eventDetails.setError("Required");
                hasError = true;
            }

            if (!qrCodeOptionSelected) {
                Toast.makeText(getContext(), "Mandatory Fields have no been entered. Please also select a QR code option.", Toast.LENGTH_SHORT).show();
                hasError = true;
            }

            if (hasError) {
                return;
            }

            // Proceed with event creation
            event = new Event(eventName, Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            event.setEventDate(eventDateStr);
            event.setEventTime(eventTimeStr);
            event.setEventDetails(eventDetailsStr);

            String uniquecode = generatepromotionQRCode(event,uniqueqrcodeimage ,organizer);
            event.setUniquepromoqr(uniquecode);


            if (createqr == true) {
                String qrcodevalue = generateQRCode(event, qrcodeimage);
                event.setQrcodeid(qrcodevalue);
            }



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

        qrcodebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createqr = true;
                qrcodebutton.setBackgroundColor(Color.GRAY);
            }
        });

        return view;
    }

    public String generateQRCode(Event myevent, ImageView imageCode) {
        String myText = myevent.getEventId();

        // Appending timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        myText += "_" + timestamp;

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(myText, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(matrix);
            imageCode.setImageBitmap(mBitmap);

            InputMethodManager manager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(imageCode.getApplicationWindowToken(), 0);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return myText;
    }

    public String generatepromotionQRCode(Event myevent, ImageView imageCode, Organizer organizer){
        String myText = myevent.getEventId();
        myText += "_" + organizer.getUserId();

        // Appending timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        myText += "_" + timestamp;

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(myText, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(matrix);
            imageCode.setImageBitmap(mBitmap);

            InputMethodManager manager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(imageCode.getApplicationWindowToken(), 0);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return myText;
    }
}