package com.example.checkin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class NotificationHandleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_handle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.atten_view, new Announcements())
                .commit();
    }
}