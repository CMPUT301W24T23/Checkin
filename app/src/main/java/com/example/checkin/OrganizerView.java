package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

// Organizer perspective of the app
public class OrganizerView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view);

        // URL: https://www.geeksforgeeks.org/how-to-implement-bottom-navigation-with-activities-in-android/
        BottomNavigationView bottomnav = findViewById(R.id.bottomnavbar);
        bottomnav.setSelectedItemId(R.id.home);

        // create home page and attendees list fragments
        OrganizerHomePage org_frag1= new OrganizerHomePage();
        Attendeeslisted list_frag = new Attendeeslisted();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.org_view, org_frag1)
                .addToBackStack(null)
                .commit();
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, org_frag1)
                            .commit();
                    return true;

                }
                else if (item.getItemId() == R.id.qrcodes){
                    // implement when fragment is added
                }
                else if (item.getItemId() == R.id.messages){
                    //implement when fragment is added
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