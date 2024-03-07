package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

// Represents the Attendee Perspective of the app
public class AttendeeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attenndee_view);

        // create homepage and announcements fragments
        AttendeeFragment1 att_frg1 = new AttendeeFragment1();
        Announcements ann_frg1 = new Announcements();
        // move to home page fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.atten_view, att_frg1)
                .commit();

        // Set bottom navigation bar
        BottomNavigationView bottomnav = findViewById(R.id.bottomnavbar2);
        // URL: https://www.geeksforgeeks.org/how-to-implement-bottom-navigation-with-activities-in-android/
        bottomnav.setSelectedItemId(R.id.home2);
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home2){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.atten_view, att_frg1)
                            .commit();
                    return true;
                }
                else if (item.getItemId() == R.id.qrcodes2){
                    //implement when fragment created
                }
                else if (item.getItemId() == R.id.messages2){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.atten_view, ann_frg1)
                            .commit();
                    return true;

                }
                else if (item.getItemId() == R.id.profile){
                    //implement when fragment created
                }
                return false;
            }
        });

    }
}