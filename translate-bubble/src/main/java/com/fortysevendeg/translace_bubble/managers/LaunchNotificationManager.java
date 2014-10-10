package com.fortysevendeg.translace_bubble.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.fortysevendeg.translace_bubble.R;
import com.fortysevendeg.translace_bubble.ui.activities.TranslateBubbleActivity;

public class LaunchNotificationManager {

    private final static int NOTIFICATION_ID = 1100;

    private Context context;

    private NotificationManager notifyManager;

    public LaunchNotificationManager(Context context) {
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;
    }

    public void failed() {
        Intent notificationIntent = new Intent(context, TranslateBubbleActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                getUniqueId(), notificationIntent,
                0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String title = context.getString(R.string.failedTitle);
        String message = context.getString(R.string.failedMessage);
        Notification notification = builder.setContentTitle(title)
                .setContentText(message)
                .setTicker(title)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true).build();

        notifyManager.notify(NOTIFICATION_ID, notification);
    }

    public void translating() {
        Intent notificationIntent = new Intent(context, TranslateBubbleActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                getUniqueId(), notificationIntent,
                0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String title = context.getString(R.string.translating);
        Notification notification = builder.setContentTitle(title)
                .setTicker(title)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true).build();

        notifyManager.notify(NOTIFICATION_ID, notification);
    }

    public void launch(String original, String translated) {
        Intent notificationIntent = new Intent(context, TranslateBubbleActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                getUniqueId(), notificationIntent,
                0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String title = context.getString(R.string.translatedTitle, original);
        builder.setContentTitle(title)
                .setContentText(translated)
                .setTicker(title)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);

        Notification notification = new NotificationCompat.BigTextStyle(builder)
                .bigText(translated)
                .build();

        notifyManager.notify(NOTIFICATION_ID, notification);
    }

    public static int getUniqueId() {
        return (int) (System.currentTimeMillis() & 0xfffffff);
    }

}
