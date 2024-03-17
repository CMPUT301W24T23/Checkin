package com.example.checkin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SendNotification extends Fragment {


    private RequestQueue mrequest;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    private String URL2 = "https://fcm.googleapis.com/v1/projects/checkin-6a54e/messages:send";
    Button sendbtn;
    EditText titlemessage;
    EditText bodymessage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_notification, container, false);
        sendbtn = view.findViewById(R.id.sendmessagebtn);
        mrequest = Volley.newRequestQueue(getContext());
        titlemessage = view.findViewById(R.id.title_msg);
        bodymessage = view.findViewById(R.id.body_msg);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String token = "fF1-N2eFRhSXdSmUBpe9VI:APA91bHydxfMLCklsISAfOTBvE1WFZ3lrS60Ho5wzs9Ec0PxP0iLyxFEKdoRI_F9tv_8_txBFCteJ7YZjMgKihF5JOFwO4w-sfCXEt-G-sQK4W4IsS96iS44SutO5dNlWTxSgxO_5Svs";
                //Message message = Message.builder().putData("Hello", "today").putData("time","7").setToken(token).build();
                //String response = FirebaseMessaging.getInstance().send(message);

                try {
                    sendNotification();
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        return view;
    }

    private void sendNotification() throws JSONException, IOException {
        // json object

        JSONObject notification = new JSONObject();
        JSONObject notificationbody = new JSONObject();
        notificationbody.put("title", titlemessage.getText().toString());
        notificationbody.put("body", bodymessage.getText().toString());

        JSONObject message = new JSONObject();
        message.put("to", "fF1-N2eFRhSXdSmUBpe9VI:APA91bHydxfMLCklsISAfOTBvE1WFZ3lrS60Ho5wzs9Ec0PxP0iLyxFEKdoRI_F9tv_8_txBFCteJ7YZjMgKihF5JOFwO4w-sfCXEt-G-sQK4W4IsS96iS44SutO5dNlWTxSgxO_5Svs");
        message.put("notification", notificationbody);

        JSONObject mainObject = new JSONObject();
        mainObject.put("message", message);

        JSONObject notificationbody2 = new JSONObject();
        notificationbody2.put("title", titlemessage.getText().toString());
        notificationbody2.put("body", bodymessage.getText().toString());

        JSONObject message2 = new JSONObject();
        message.put("topic", "event");
        message.put("notification", notificationbody2);

        JSONObject mainObject2 = new JSONObject();
        mainObject2.put("message", message2);

        FileInputStream serviceAccountStream = new FileInputStream("admin.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);

        System.out.println(credentials.getAccessToken().getTokenValue());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, mainObject,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("NotificationSuccess", "Error sending notification: ");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NotificationError", "Error sending notification: " + error.toString());

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                header.put("Authorization", "key=AAAAxpawIEE:APA91bGHVJc5ORvHkmdn1i9Q0DzGHqBSyx4cbw9XsH3xKU9QjBMLGef5INCn9uxRa2fQGxyroHEskZqJuuAEzh-CohOdTcpJtIVK87FaEfjiq8Jt3a1qJOU-eZ_946JYzyIDCZhZXUm-");
                return header;
            }
        };

        mrequest.add(request);

    }

}