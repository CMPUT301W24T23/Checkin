package com.example.checkin;

// recieves notification sent through firebase
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;


public class PushNotifications extends FirebaseMessagingService {

    Database d = new Database();
    Message m = new Message();
    private FirebaseFirestore db;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = message.getNotification().getTitle();
        String body = message.getNotification().getBody();
        DisplayRemoteNotification(title, body);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int attendeeCount = preferences.getInt("attendeeCount", 0);



        super.onMessageReceived(message);

    }

    private void DisplayRemoteNotification(String title, String body){
        String CHANNEL_ID = "message";
        CharSequence name;

        Intent intent = new Intent(this, AttendeeView.class);
        intent.setAction("OPEN_ANNOUNCEMENTS_FRAGMENT");
        intent.putExtra("open fragment", "announcements");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );


        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notifications = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notifications.notify(0, builder.build());


    }

    private void DisplayMileStoneNotification(String title, String body){
        String CHANNEL_ID = "milestone";
        CharSequence name;

        Intent intent = new Intent(this, AttendeeView.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("open_announcements_fragment", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );


        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Milestone", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.img_3)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notifications = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notifications.notify(0, builder.build());


    }

    private boolean isMilestoneNotification(RemoteMessage message) {
        // Check if the message payload contains a specific field that indicates it's a milestone notification
        // For example, you might have a key called "type" in the message data, and its value could be "milestone"
        String messageType = message.getData().get("Type");
        return messageType != null && messageType.equals("Milestone");
    }

    private void checkMilestone(int attendeeCount) {
        ArrayList<Integer> milestones = new ArrayList<>();
        milestones.add(1);
        milestones.add(10);
        milestones.add(50);
        milestones.add(100);

        for (int milestone : milestones) {
            if (attendeeCount == milestone) {
                String milestoneTitle = "Milestone Reached!";
                String milestoneBody = "Attendee count: " + attendeeCount;
                DisplayMileStoneNotification(milestoneTitle, milestoneBody);
                break; // No need to continue checking other milestones
            }
        }
    }









      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // // Pass the intent to PendingIntent to start the
        // next Activity
      //  PendingIntent pendingIntent
               // = PendingIntent.getActivity(
               // this, 0, intent,
              //  PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);


        // Create a Builder object using NotificationCompat
      //  NotificationCompat.Builder builder
              //  = new NotificationCompat
             //   .Builder(getApplicationContext(),
               // channel_id)
             //   .setContentTitle(title)
            //    .setSmallIcon(R.drawable.img_3)
           //     .setContentText(message)
              //  .setAutoCancel(true)
             //   .setOnlyAlertOnce(true)
              //  .setContentIntent(pendingIntent);

       // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


     //   if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

        //    notificationManager.notify(0, builder.build());
      //  } else {
            // Log an error if permission is not granted
         //   Log.e("PushNotifications", "Missing permission to post notifications");
    //    }
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


  //  }





}
