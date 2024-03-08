package com.example.checkin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/*
Announcements page that shows messages received by event organizers - will implement
sending notifications in next part of project, has mock data for now
*/
public class Announcements extends Fragment {
    ListView announcements;
    private ArrayList<String> announcelist;
    private ArrayAdapter<String> Announcements_Adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        announcements = view.findViewById(R.id.announcements_list);
        announcelist = new ArrayList<>();

        // Add example announcements
        announcelist.add("First Message");
        announcelist.add("Second Message");


        // if list of announcements is not null, then add messages to Announcements
        // Represented as strings now, will create announcement objects in next part
        if (announcelist != null) {
            Announcements_Adapter = new ArrayAdapter<String>(getActivity(), R.layout.content, announcelist) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.content, null);
                    }
                    TextView textView = view.findViewById(R.id.event_text);
                    textView.setText(announcelist.get(position));
                    return view;
                }
            };
            announcements.setAdapter(Announcements_Adapter);
        }
        return view;
    }
}