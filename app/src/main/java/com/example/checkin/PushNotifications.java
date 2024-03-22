package com.example.checkin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


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





        String CHANNEL_ID = "message";
        CharSequence name;

        Intent intent = new Intent(this, NotificationHandleActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );


        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

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

        super.onMessageReceived(message);

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
