package com.example.checkin;

import static java.util.Objects.requireNonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class AdministratorEventList extends Fragment {
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_eventlist, container, false);
//        ListView listView = view.findViewById(R.id.admin_events);

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference eventsCollectionRef = db.collection("Events");
//
//        eventsCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                List<String> eventList = new ArrayList<>();
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    String eventName = documentSnapshot.getString("Name");
//                    eventList.add(eventName);
//                }
//
//                ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, eventList);
//                // Use requireNonNull() to ensure listView is not null
//                listView.setAdapter(eventAdapter);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // Handle any errors
//            }
//        });
        return view;
    }
}
