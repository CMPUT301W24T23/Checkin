package com.example.checkin;
// Fragment that allows you to scan a QR Code
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


//https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
public class ScanQrCode extends Fragment implements View.OnClickListener{

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private Button scanBtn;
    private TextView messageText, messageFormat;
    private ActivityResultLauncher<Intent> qrScanLauncher;

    Attendee attendee;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, proceed with QR scanning
                    startQRScan();
                } else {
                    Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_LONG).show();
                    requireActivity().finish();
                }
            });



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_qr_code, container, false);


        Intent intent = getActivity().getIntent();

        qrScanLauncher =  registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                                result.getResultCode(),
                                result.getData()
                        );
                        if (intentResult != null) {
                            if (intentResult.getContents() == null) {
                                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            } else {
                                System.out.println(intentResult.getContents());
                                System.out.println(intentResult.getFormatName());
                                messageText.setText(intentResult.getContents());
                                messageFormat.setText(intentResult.getFormatName());
                                String qrCodeContent = intentResult.getContents();
                                getEventDetailsFromFirebase(qrCodeContent);



                                // check in attendee using firebase- use event id and attendee id to get
                                // event and attendee from firebase, and update both

                                EventDetailAtten eventfragment = new EventDetailAtten();
                                Bundle args = new Bundle();
                                args.putString("event", intentResult.getContents());
                                eventfragment.setArguments(args);

                                // --- needs to be implemented
                                // Navigate to the EventDetailAtten Frgment
                                // requireActivity().getSupportFragmentManager().beginTransaction()
                                // .replace(R.id.atten_view, eventfragment)
                                // .addToBackStack(null)
                                //  .commit();


                            }


                        }
                    }
                }
        );



                // Add Firebase
                //String eventId = messageText.getText().toString();

                // Get event data from Firebase, need to implement from updated Database code
                // db.getEvent(eventId, new Database.OnEventRetrievedListener() {
                  //  @Override
                  //  public void onEventRetrieved(Event event) {
                        // Use the retrieved event object here
                  //  }
               // });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                //startQRScan();
            }
        }




        return view;





    }

    @Override
    public void onClick(View v) {
        startQRScan();
    }

    private void startQRScan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ScanQrCode.this);
        integrator.setPrompt("Scan a barcode or QR Code");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();

        qrScanLauncher.launch(integrator.createScanIntent());
    }




    private void getEventDetailsFromFirebase(String qrCodeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("Events");

        eventsRef.whereEqualTo("Qr Code Id", qrCodeId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Event details retrieved successfully
                            Event event = document.toObject(Event.class);
                            // Proceed with event check-in or any other operations

                            // Now, let's fetch the attendee details
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            String androidId = preferences.getString("ID", "");

                            DocumentReference attendeeRef = db.collection("Attendees").document(androidId);
                            attendeeRef.get().addOnCompleteListener(attendeeTask -> {
                                if (attendeeTask.isSuccessful()) {
                                    DocumentSnapshot attendeeDocument = attendeeTask.getResult();
                                    if (attendeeDocument.exists()) {
                                        // Convert the document snapshot to an Attendee object
                                        Attendee attendee = attendeeDocument.toObject(Attendee.class);

                                        // Now you have both the event and the attendee
                                        // You can proceed with the check-in process
                                        if (attendee != null) {
                                            attendee.CheckIn(event);
                                            event.userCheckIn(attendee);

                                            EventDetailAtten eventfragment = new EventDetailAtten();
                                            Bundle args = new Bundle();
                                            args.putSerializable("event", event);
                                            eventfragment.setArguments(args);

                                            getParentFragmentManager().setFragmentResult("event",args);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.atten_view, eventfragment).addToBackStack(null).commit();

                                        }
                                    } else {
                                        Log.d("Attendee", "No such document");
                                    }
                                } else {
                                    Log.d("Attendee", "get failed with ", attendeeTask.getException());
                                }
                            });
                        }
                    } else {
                        // Error fetching event details
                        Toast.makeText(requireContext(), "Error fetching event details from Firebase", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error fetching event details", task.getException());
                    }
                });
    }
}