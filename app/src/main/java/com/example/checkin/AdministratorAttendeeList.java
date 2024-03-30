package com.example.checkin;


import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_THRESHOLD;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_VELOCITY_THRESHOLD;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class AdministratorAttendeeList extends Fragment {

    private FirebaseFirestore db;
    private ListView listView;
    private ArrayAdapter<String> attendeeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_attendeeprofile, container, false);
        listView = view.findViewById(R.id.admin_attendees);

        // Get the instance of the Firebase.
        db = FirebaseFirestore.getInstance();
        CollectionReference attendeesCollectionRef = db.collection("Attendees");

        // Adapter to convert the type of the attendeeCollectionRef to list.
        attendeesCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> attendeeList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String attendee = documentSnapshot.getString("Name");
                    if (attendee != null) {
                        attendeeList.add(attendee);
                    }
                }

                attendeeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, attendeeList);
                listView.setAdapter(attendeeAdapter);
//                setupSwipeGesture();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSwipeGesture();
    }

   // CHATGPT 3.5
    @SuppressLint("ClickableViewAccessibility")
    private void setupSwipeGesture() {
        listView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float deltaX = endX - startX;

                        // Determine if it's a swipe
                        if (Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(deltaX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (deltaX < 0) {
                                // Left swipe
                                int position = listView.pointToPosition((int) event.getX(), (int) event.getY());
                                if (position != ListView.INVALID_POSITION) {
                                    String attendee = attendeeAdapter.getItem(position);
                                    // Remove the attendee from the adapter
                                    attendeeAdapter.remove(attendee);
                                    // Notify adapter about the removal
                                    attendeeAdapter.notifyDataSetChanged();

                                    // Update Firebase Firestore
                                    db.collection("Attendees")
                                            .whereEqualTo("Name", attendee)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            // Fade out animation
                                                            listView.animate()
                                                                    .alpha(0.0f)
                                                                    .setDuration(500) // Adjust duration as needed
                                                                    .withEndAction(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            // Remove the document after the animation completes
                                                                            document.getReference().delete();
                                                                        }
                                                                    })
                                                                    .start();
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }

}
