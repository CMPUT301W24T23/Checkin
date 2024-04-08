package com.example.checkin;

// represents milestone recieved by organizer for events
// https://www.geeksforgeeks.org/how-to-push-notification-in-android-using-firebase-cloud-messaging/
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.units.qual.A;

/*
  Represents a milestone received by the organizer for events.
  This class provides functionality to send milestone notifications.
 */
public class MileStone {

    private static final String CHANNEL_ID = "milestone";
    static Database db = new Database();

    /**
     * Sends a milestone notification to the organizer.
     *
     * @param context         The context from which the notification is sent.
     * @param title           The title of the notification.
     * @param body            The body of the notification.
     * @param eventid         The ID of the event associated with the milestone.
     * @param targetIntent    The intent to be launched when the notification is tapped.
     * @param notificationid  The ID of the notification.
     */
    public static void sendMilestoneNotification(Context context, String title, String body, String eventid, Intent targetIntent, int notificationid) {

        Intent intent = new Intent(context, OrganizerView.class);
        intent.setAction("OPEN_MILESTONES_FRAGMENT");
        intent.putExtra("open fragment", "milestones");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                targetIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Milestone", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.img_3)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(notificationid, builder.build());
        }

        Message message = new Message(title, body);
        message.setEventid(eventid);
        message.setType("Milestone");
        db.updateMessage(message);
    }
}