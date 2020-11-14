package com.example.musicplayer.util;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {
    /**
     * The notification manager instance we are using.
     */
    private NotificationManager mNotificationManager;
    /**
     * The name of the channel to use for these notifications.
     */
    private String mChannelId;

    /**
     * Creates and returns a new NotificationHelper.
     *
     * @param context the context to use.
     * @param channelId the ID of the notification channel to create.
     * @param name the user visible name of the channel.
     */
    public NotificationHelper(Context context, String channelId, String name) {
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mChannelId = channelId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(channel);
        }
    }
    /**
     * Returns a new Notification.Builder.
     *
     * @param context the context to use
     */
    public Notification.Builder getNewBuilder(Context context) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, mChannelId);
        } else {
            builder = new Notification.Builder(context);
        }
        return builder;
    }

    /**
     * Retruns a new Notification.
     *
     * @param context the context to use.
     */
    public Notification getNewNotification(Context context) {
        return getNewBuilder(context).getNotification(); // build() is API16.
    }

    /**
     * Post a notification to be shown in the status bar.
     *
     * @param id the id of this notification.
     * @param notification the notification to display.
     */
    public void notify(int id, Notification notification) {
        mNotificationManager.notify(id, notification);
    }

    /**
     * Cancels a notification.
     *
     * @param id the id to cancel
     */
    public void cancel(int id) {
        mNotificationManager.cancel(id);
    }
}
