package com.example.checkin;

// provides options to either send notification or view milestones
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MessagesOption extends Fragment {

    Button sendmessage;

    Button viewmilestonebtn;

    Button backbutton;

    Event myevent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages_option, container, false);
        sendmessage = view.findViewById(R.id.sendmssgbtn);
        viewmilestonebtn = view.findViewById(R.id.milestonebtn);
        backbutton = view.findViewById(R.id.backbtn);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myevent = (Event) bundle.getSerializable("event");
        }

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendNotification sendfrag = new SendNotification();
                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                sendfrag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, sendfrag).addToBackStack(null).commit();

            }
        });

        viewmilestonebtn.setOnClickListener(new View.OnClickListener() {
            DisplayMilestones milestones_frag = new DisplayMilestones();
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, milestones_frag).addToBackStack(null).commit();

            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }
}