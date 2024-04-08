package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/*
  Activity representing the Organizer perspective of the app.
  Responsible for managing fragments and handling navigation through bottom navigation.
 */
public class OrganizerView extends AppCompatActivity {

    OrganizerFragment1 org_frag1;



    private static final int PERMISSION_REQUEST_NOTIFICATION = 3;

    /**
     * Called when the activity is starting.
     * Initializes the layout, sets up bottom navigation, and requests notification permission if not granted.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view);

        // URL: https://www.geeksforgeeks.org/how-to-implement-bottom-navigation-with-activities-in-android/
        BottomNavigationView bottomnav = findViewById(R.id.bottomnavbar);
        bottomnav.setSelectedItemId(R.id.home);

        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_NOTIFICATION);
        }

        // create home page and attendees list fragments


        org_frag1 = new OrganizerFragment1();
        AttendeesOptions list_frag = new AttendeesOptions();

        ChooseEvent choose_eventfrag = new ChooseEvent();



        SendNotification sendmssg_frag = new SendNotification();
        MessagesOption messageopt_frag = new MessagesOption();



        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.org_view, org_frag1, "organizer_fragment_tag")
                .commit();

        // set navbar
        // // URL: https://www.geeksforgeeks.org/how-to-implement-bottom-navigation-with-activities-in-android/
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home) {
                    Bundle args = new Bundle();
                    org_frag1.setArguments(args);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, org_frag1, "organizer_fragment_tag")
                            .commit();
                    return true;
                }

                 else if (item.getItemId() == R.id.messages) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, messageopt_frag)
                            .addToBackStack(null)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.attendees) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, choose_eventfrag, "organizer_attendees_tag")
                            .commit();
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * Called when a new intent is delivered to this activity.
     * Handles the action of opening the milestones fragment.
     *
     * @param intent The new intent that was started for the activity.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (action != null && action.equals("OPEN_MILESTONES_FRAGMENT")) {
            // Perform a fragment transaction to open the display milestones fragment
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavbar);

            DisplayMilestones dispmile_frag = new DisplayMilestones();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.org_view, dispmile_frag)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.messages);


        }
    }

    /**
     * Callback for the result from requesting permissions.
     * Handles the result of the permission request for notifications.
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Notification permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}