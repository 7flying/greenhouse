package com.sevenflying.greenhouseclient.domain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.sevenflying.greenhouseclient.app.MainActivity;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.net.Constants;

/** Receives a boot event.
 * Created by flying on 22/02/15.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (MainActivity.alarmManager != null) {
                MainActivity.alarmManager.cancel(MainActivity.alarmIntent);
            } else {
                // Alarm manager setup
                Intent myIntent = new Intent(context, AlarmReceiver.class);
                MainActivity.alarmIntent = PendingIntent.getBroadcast(context, 0, myIntent,
                        0);
                MainActivity.alarmManager = (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);
                MainActivity.alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        GreenhouseUtils.THIRTY_SECONDS, GreenhouseUtils.THIRTY_SECONDS,
                        MainActivity.alarmIntent);
                Log.d(Constants.DEBUGTAG, " # BootReceiver start AlarmManager");
            }
        }
    }
}
