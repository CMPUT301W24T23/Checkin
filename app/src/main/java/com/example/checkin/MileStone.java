package com.example.checkin;

// represents milestone recieved by organizer for events
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.units.qual.A;

public class MileStone {

    private static final String CHANNEL_ID = "milestone";
    static Database db = new Database();

    public static void sendMilestoneNotification(Context context, String title, String body, String eventid, Intent targetIntent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                targetIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Milestone", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(0, builder.build());
        }

        Message message = new Message(title, body);
        Alert alert = new Alert(title,body);
        message.setEventid(eventid);
        message.setType("Milestone");
        db.updateMessage(message);
    }
}