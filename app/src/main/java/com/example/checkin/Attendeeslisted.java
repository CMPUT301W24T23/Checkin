package com.example.checkin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Attendeeslisted extends Fragment {

    Button checkedinlistbtn;

    Event myevent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendeeslisted, container, false);
        checkedinlistbtn = view.findViewById(R.id.checkedinbtn);

        Bundle bundle = this.getArguments();

        if (bundle != null){
            myevent = (Event) bundle.getSerializable("event");
        }
        checkedinlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckedInList_org check_frag = new CheckedInList_org();
                Bundle args = new Bundle();
                args.putSerializable("event", myevent);
                check_frag.setArguments(args);
                getParentFragmentManager().setFragmentResult("event",args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.org_view, check_frag).commit();

            }
        });


        return view;
    }
}