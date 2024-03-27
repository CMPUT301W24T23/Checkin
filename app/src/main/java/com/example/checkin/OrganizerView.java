package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

// Organizer perspective of the app
public class OrganizerView extends AppCompatActivity {

    OrganizerFragment1 org_frag1;
    Organizer organizer;

    Fragment open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view);

        // URL: https://www.geeksforgeeks.org/how-to-implement-bottom-navigation-with-activities-in-android/
        BottomNavigationView bottomnav = findViewById(R.id.bottomnavbar);
        bottomnav.setSelectedItemId(R.id.home);

        // create home page and attendees list fragments


        org_frag1 = new OrganizerFragment1();
        AttendeesOptions list_frag = new AttendeesOptions();
        SendNotification sendmssg_frag = new SendNotification();
        MessagesOption messageopt_frag = new MessagesOption();




        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.org_view, org_frag1, "OrganizerFragment1")
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
                            .replace(R.id.org_view, org_frag1, "OrganizerFragment1")
                            .commit();
                    return true;

                }
                else if (item.getItemId() == R.id.qrcodes){
                    // implement when fragment is added
                }
                else if (item.getItemId() == R.id.messages){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, messageopt_frag)
                            .commit();
                    return true;
                }
                else if (item.getItemId() == R.id.attendees){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, org_frag1)
                            .commit();
                    return true;
                }
                return false;
            }
        });

    }
}