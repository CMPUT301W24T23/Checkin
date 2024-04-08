package com.example.checkin;


import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_THRESHOLD;
import static com.example.checkin.OnSwipeTouchListener.GestureListener.SWIPE_VELOCITY_THRESHOLD;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

/*
This Java class, named `AdministratorAttendeeList`, is responsible of generating a new fragment displaying
the list of all the attendees/Users of the app.
 */
public class AdministratorAttendeeList extends Fragment {

    private FirebaseFirestore db;
    private ListView listView;
    private ArrayAdapter<String> attendeeAdapter;
    private List<String> attendeeList;

    /**
     * Responsible for generating the fragment displaying the list, buttons for navigating back to
     * the main menu fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_attendeeprofile, container, false);
        listView = view.findViewById(R.id.admin_attendees);
        Button backbtn = view.findViewById(R.id.back_button);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Get the instance of the Firebase.
        db = FirebaseFirestore.getInstance();
        CollectionReference attendeesCollectionRef = db.collection("Attendees");

        // Adapter to convert the type of the attendeeCollectionRef to list.
        attendeesCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                attendeeList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String attendee = documentSnapshot.getString("Name");
                    if (attendee != null) {
                        attendeeList.add(attendee);
                    }
                }
                // Set up the adapter and list view.
                attendeeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.content, attendeeList) {
                    @Override

                    // This getView is responsible for creating the View for each item in the ListView.
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = convertView;
                        if (view == null) {
                            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_list_layout, parent, false);
//                                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                                        // Sets the view to the content layout.
//                                        view = inflater.inflate(R.layout.content, null);
                        }

                        // Setting the text of content layout to the name of the event.
                        TextView textView = view.findViewById(R.id.admin_text_view);
                        textView.setText(attendeeList.get(position));

                        return view;
                    }
                };
                listView.setAdapter(attendeeAdapter);

//                attendeeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, attendeeList);
//                listView.setAdapter(attendeeAdapter);
//                setupSwipeGesture();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });

        // Long click to delete an event
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String attendee = attendeeAdapter.getItem(position);
                // Confirm deletion with the organizer
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Attendee")
                        .setMessage("Are you sure you want to delete this attendee?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteAttendee(attendee))
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        return view;
    }


    /**
     * Function responsible to delete the selected attendee/user from the list.
     * @param attendee : Selected attendee to delete.
     */
    private void deleteAttendee(String attendee) {
        attendeeList.remove(attendee);
        attendeeAdapter.notifyDataSetChanged(); // Refresh the list
        db.collection("Attendees")
               .whereEqualTo("Name", attendee)
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @SuppressLint("RestrictedApi")
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               document.getReference().delete();
                               Log.d(TAG, "Deleted Selected Attendee");
                           }
                       } else {
                           Log.d(TAG, "Error getting documents: ", task.getException());
                       }
                   }
               });
    }
}

