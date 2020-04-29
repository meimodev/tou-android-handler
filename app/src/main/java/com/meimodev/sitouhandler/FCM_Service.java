/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.SignIn.SignIn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FCM_Service extends FirebaseMessagingService {
    private static final String TAG = "FCM_Service";
    private static final String NOTIFICATION_CHANNEL_ID = "chanel_FCM";
    public static String FCM_TOKEN = null;

    @Override
    public void onNewToken(@NonNull String s) {
        FCM_TOKEN = s;
        Log.e(TAG, "sendTokenToServer: FCM Token Refreshed = " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        notifyApp(title, message);
        Log.e(TAG, "onMessageReceived: NOTIFY FOREGROUND NOTIFICATION" );

    }

    private void notifyApp(String title, String msg) {

        Intent i = new Intent(this, Dashboard.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0 /*Request Code*/,
                i,
                PendingIntent.FLAG_ONE_SHOT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notifBuilder.setSmallIcon(R.drawable.ic_tou_notification)
                .setContentTitle(title)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(createID(), notifBuilder.build());

    }

    public int createID(){
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "TOU notification", NotificationManager.IMPORTANCE_DEFAULT);
        // Configure the notification channel
        channel.setDescription("TOU notification control");
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        NotificationManager manager = context.getSystemService(NotificationManager.class);

        manager.createNotificationChannel(channel);
        Log.d(TAG, "createNotificationChannel: created =" + NOTIFICATION_CHANNEL_ID);
    }
}
