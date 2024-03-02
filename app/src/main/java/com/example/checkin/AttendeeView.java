package com.example.checkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AttendeeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attenndee_view);

        AttendeeFragment1 att_frg1 = new AttendeeFragment1();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.atten_view, att_frg1)
                .commit();

        BottomNavigationView bottomnav = findViewById(R.id.bottomnavbar2);
        bottomnav.setSelectedItemId(R.id.home2);
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home2){

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.atten_view, att_frg1)
                            .commit();
                }
                else if (item.getItemId() == R.id.qrcodes2){
                    //implement when fragment created
                }
                else if (item.getItemId() == R.id.messages2){
                    //implement when fragment created
                }
                else if (item.getItemId() == R.id.profile){
                    //implement when fragment created
                }
                return false;
            }
        });

    }
}