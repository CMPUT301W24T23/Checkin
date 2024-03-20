package com.example.checkin;

import android.content.Intent;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScanQrCode extends Fragment implements View.OnClickListener{

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private Button scanBtn;
    private TextView messageText, messageFormat;

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

        scanBtn = view.findViewById(R.id.scanBtn);
        messageText = view.findViewById(R.id.textContent);
        messageFormat = view.findViewById(R.id.textFormat);

        Intent intent = getActivity().getIntent();

        // Retrieve the object from the Intent extras
        Attendee attendee = (Attendee) intent.getSerializableExtra("attendee");
        Database db = new Database();

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScan();

                // Add Firebase
                //String eventId = messageText.getText().toString();

                // Get event data from Firebase, need to implement from updated Database code
                // db.getEvent(eventId, new Database.OnEventRetrievedListener() {
                  //  @Override
                  //  public void onEventRetrieved(Event event) {
                        // Use the retrieved event object here
                  //  }
               // });
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                startQRScan();
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        startQRScan();
    }

    private void startQRScan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Scan a barcode or QR Code");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }


    private ActivityResultLauncher<Intent> qrScanLauncher = registerForActivityResult(
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
                            messageText.setText(intentResult.getContents());
                            messageFormat.setText(intentResult.getFormatName());
                        }
                    }
                }
            }
    );
}