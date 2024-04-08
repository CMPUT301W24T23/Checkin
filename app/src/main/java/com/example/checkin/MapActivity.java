// This file handles the functionality for displaying a map with the user's current location marked.
// The user can also return to the previous screen using a back button. This activity utilizes the Google Maps
// API and requires location permissions to function properly.
// https://stackoverflow.com/questions/18386750/map-activity-in-android
// https://www.geeksforgeeks.org/google-maps-in-android/
// https://www.youtube.com/watch?v=JzxjNNCYt_o
// https://www.youtube.com/watch?v=WouAQmqJI_I
// https://www.youtube.com/watch?v=BO1utHYhsms
// https://developers.google.com/maps/documentation/android-sdk/start

package com.example.checkin;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private Location currentLocation;
    private Button btnBack;
    private Attendee currentUser;

    private AttendeeList trackedAttendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // get event object from previous fragment
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            trackedAttendees = (AttendeeList) bundle.getSerializable("attendeeList");
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });








    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    //updateAttendee();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        for(Attendee a : trackedAttendees.getAttendees()){
            if((a.getLat() != 0d) || (a.getLon() != 0d)){
                Log.d("Bundled Attendees", String.format("Bundled Attendee: %s", a.getUserId()));
            /*
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(a.getLat(),a.getLon())));
                    //.title("Hello world"));

             */
                LatLng latLng = new LatLng(a.getLat(), a.getLon());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
                mMap.addMarker(markerOptions);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    /**
     * Retrieve and update the current attendee's location to firebase
     * Retrieves the current attendee, sets their new location, and then reuploads to the database
     */
    public void updateAttendee(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Database firedb = new Database();
        String id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference docRef = db.collection("Attendees").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firebase Succeed", "(Map Activity) Retrieve attendee: " + document.getData());
                        Database fireBase = new Database();
                        currentUser = fireBase.getAttendee(document);

                        //set new location
                        //currentUser.setLoc(currentLocation);
                        currentUser.setLat(currentLocation.getLatitude());
                        currentUser.setLon(currentLocation.getLongitude());

                        //update in firebase
                        firedb.updateAttendee(currentUser);

                    } else {
                        Log.d("Firebase", "No such document");

                    }
                } else {
                    Log.d("Firebase get failed", "get failed with ", task.getException());
                }
            }
        });
    }
}
