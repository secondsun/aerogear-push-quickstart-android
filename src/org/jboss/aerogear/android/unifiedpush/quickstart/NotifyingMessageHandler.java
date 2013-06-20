package org.jboss.aerogear.android.unifiedpush.quickstart;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;

public class NotifyingMessageHandler implements MessageHandler {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private Context ctx;

    public static final NotifyingMessageHandler instance = new NotifyingMessageHandler();

    private NotifyingMessageHandler() {
    }

    @Override
    public void onMessage(Context context, Bundle message) {
        ctx = context;
        sendNotification(message.getString("alert"));
    }

    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, PushQuickstartActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra("alert", msg);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("AeroGear Push Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onDeleteMessage(Context context, Bundle arg0) {
    }

    @Override
    public void onError() {
    }

}
