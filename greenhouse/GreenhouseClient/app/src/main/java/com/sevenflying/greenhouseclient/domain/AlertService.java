package com.sevenflying.greenhouseclient.domain;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.sevenflying.greenhouseclient.app.MainActivity;
import com.sevenflying.greenhouseclient.app.R;

/** AlertService checks whether the alerts defined by the user are fired.
 *  If so sends a notification.
 * Created by 7flying on 05/08/2014.
 */
public class AlertService extends IntentService {

    private NotificationManager notificationManager;

    public AlertService() {
        super("AlertService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkAlerts();
    }

    /** Checks if any alert is fired
     */
    private void checkAlerts() { // Todo
        // if alerts will call
        sendNotification();
    }

    /** Sends an "alert is fired" notification
     */
    private void sendNotification() {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.blossom)
                        .setContentTitle("title")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Alert"))
                        .setContentText("Message");

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(0, mBuilder.build());
    }
}
