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

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

//represents a class that allows for new events to be created
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
    private EditText eventlocation;
    private EditText attendeeCap;
    private Switch switchVisible;

    private boolean usedQR = false;
    private String retrievedQRCodeID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();



    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        try {
                            poster = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            poster = Bitmap.createScaledBitmap(poster, 1000, 2000, false);
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
        eventlocation = view.findViewById(R.id.etlocation);

        attendeeCap = view.findViewById(R.id.attendeeCap);
        switchVisible = view.findViewById(R.id.switchSignUpLimit);

        attendeeCap.setVisibility(View.GONE); // Initially hide the EditText

        switchVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    attendeeCap.setVisibility(View.VISIBLE);
                } else {
                    attendeeCap.setVisibility(View.GONE);
                }
            }
        });

        Database database = new Database();
        btnAddPoster.setOnClickListener(v -> mGetContent.launch("image/*"));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            events = (EventList) bundle.getSerializable("eventslist");
        }

        backbutton.setOnClickListener(view1 -> getActivity().getSupportFragmentManager().popBackStack());

        String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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

        // choose event qr code to be generated

        // create new event and open list of events
        addeventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create event with event name and ID
                if (eventname.getText().toString().equals("")) {
                    eventname.setError("Event name required");
                    Log.d("Event Name Required", "User did not supply event name");
                    return;
                }
                event = new Event(eventname.getText().toString(), Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                //get details if any

                if (createqr == true) {
                    String qrcodevalue = generateQRCode(event, qrcodeimage);
                    event.setQrcodeid(qrcodevalue);
                }

                //convert image to string and add to event
                if (posterAdded) {
                    event.setPoster(encoder.BitmapToBase64(poster));
                } else {
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


        addeventbutton.setOnClickListener(view13 -> {
            String eventName = eventname.getText().toString().trim();
            String eventDateStr = eventDate.getText().toString().trim();
            String eventTimeStr = eventTime.getText().toString().trim();
            String eventDetailsStr = eventDetails.getText().toString().trim();
            String eventlocationStr = eventlocation.getText().toString().trim();
            String attendeeCapStr = attendeeCap.getText().toString();


            boolean hasError = false;
            //if the event cap switch is checked
            if(switchVisible.isChecked()){
                Log.d("Get String", String.format("%s", attendeeCap.getText().toString()));
                if(attendeeCapStr.isEmpty()){
                    attendeeCap.setError("Required");
                    hasError = true;
                }
            } else{
                //set high cap otherwise
                attendeeCapStr = "999999999";
            }

            if (eventName.isEmpty()) {
                eventname.setError("Required");
                hasError = true;
            }
            if (eventlocationStr.isEmpty()) {
                eventlocation.setError("Required");
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
                Toast.makeText(getContext(), "Mandatory Fields have not been entered. Please also select a QR code option.", Toast.LENGTH_SHORT).show();
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
            event.setLocation(eventlocationStr);
            event.setAttendeeCap(attendeeCapStr);

            String uniquecode = generatepromotionQRCode(event, uniqueqrcodeimage, organizer);
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
                qrCodeOptionSelected = true;
                createqr = true;
                qrcodebutton.setBackgroundColor(Color.GRAY);
            }
        });

        btnUseExistingQR.setOnClickListener(view12 -> {
            qrCodeOptionSelected = true;

            retrieveDeletedCodes(android_id);
            // Your code for using an existing QR code

        });


        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        return view;

    }

    public Bitmap generateQRCodeBitmap(String qrCodeValue) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(qrCodeValue, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * generates new qr code for event
     * @param myevent
     * @param imageCode
     * @return
     */
    public String generateQRCode(Event myevent, ImageView imageCode) {
        String myText = myevent.getEventId();

        // Appending timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        myText += "_" + timestamp;
        myText += "_" + myevent.getEventname();

        // Appending user's ID
        //String userid = "123456"; // Change 123456 to user's ID
        //  myText += "_" + userid;

        // Initializing MultiFormatWriter for QR code

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

    /**
     * Generates new promotion qr code
     * @param myevent
     * @param imageCode
     * @param organizer
     * @return
     */
    public String generatepromotionQRCode(Event myevent, ImageView imageCode, Organizer organizer) {
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

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String amPm;
                if (hourOfDay >= 12) {
                    amPm = "PM";
                    hourOfDay -= 12;
                } else {
                    amPm = "AM";
                }
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
                String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minute, amPm);
                eventTime.setText(time);
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                eventDate.setText(date);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Retrieve the deleted QR Codes that match this organizer ID
     * @param OrgId
     * the current organizer's ID
     */
    public void retrieveDeletedCodes(String OrgId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("DeletedQR");
        Database fireBase = new Database();
        colRef.whereEqualTo("Organizer", OrgId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (usedQR){
                            return;
                        }
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        //retrieve first document
                        usedQR = true;
                        Map<String, String> retrievedQR = fireBase.retrieveDeletedQR(document);
                        retrievedQRCodeID = retrievedQR.get("DeletedQR");
                        deleteQRCode(retrievedQRCodeID);
                        Log.d("Retrieve Deleted QR", String.format("Retrieved deleted QR Code %s", retrievedQRCodeID));

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void deleteQRCode(String QRCodeId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DeletedQR").document(QRCodeId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

}