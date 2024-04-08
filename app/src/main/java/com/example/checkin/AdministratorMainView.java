package com.example.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/*
This class sets the background layout for the rest of the administrator's fragments.
 */
public class AdministratorMainView extends AppCompatActivity {
    @Override

    /**
     * Deploys the frame layout fragment for the administrator.
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_layout);

        // Making a new instance of the administrator view.
        AdministratorView admin_menu = new AdministratorView();

        // Deploying the fragment on the frame with id adminFrame.
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminFrame, admin_menu)
                .commit();

    }
}
