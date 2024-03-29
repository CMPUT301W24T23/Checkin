package com.example.checkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {
    private ArrayList<Message> messages;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_message,
                    parent, false);
        } else {
            view = convertView;
        }
        Message message = getItem(position);
        TextView messagename = view.findViewById(R.id.message_name);
        messagename.setText(message.getTitle());
        TextView messagebody = view.findViewById(R.id.message_body);
        messagebody.setText(message.getBody());
        return view;
    }
}
