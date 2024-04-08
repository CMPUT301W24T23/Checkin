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

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState This fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
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

        // button to send notifcation to attendees for a certain event
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectEventMessages selectmssg_frag= new SelectEventMessages();
                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                selectmssg_frag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, selectmssg_frag).addToBackStack(null).commit();

            }
        });

        //button that allows organizers to view past milestones
        viewmilestonebtn.setOnClickListener(new View.OnClickListener() {
            DisplayMilestones milestones_frag = new DisplayMilestones();
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, milestones_frag).addToBackStack(null).commit();

            }
        });

        // goes back to previous fragment
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }
}