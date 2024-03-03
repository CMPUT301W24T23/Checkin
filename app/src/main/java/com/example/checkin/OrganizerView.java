package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class OrganizerView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view);

        // URL: https://www.geeksforgeeks.org/how-to-implement-bottom-navigation-with-activities-in-android/
        BottomNavigationView bottomnav = findViewById(R.id.bottomnavbar);
        bottomnav.setSelectedItemId(R.id.home);


        OrganizerFragment1 org_frag1= new OrganizerFragment1();
        CheckedInList_org check_frag1= new CheckedInList_org();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.org_view, org_frag1)
                .commit();
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home){

                    //implement
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, org_frag1)
                            .commit();
                }
                else if (item.getItemId() == R.id.qrcodes){
                    // implement
                }
                else if (item.getItemId() == R.id.messages){
                    //implement
                }
                else if (item.getItemId() == R.id.attendees){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.org_view, check_frag1)
                            .commit();
                   //implement
                }
                return false;
            }
        });

    }
}