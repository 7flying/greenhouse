package com.sevenflying.greenhouseclient.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** AlarmReceiver is waken up every 2 minutes and asks AlertService to check whether the alerts
 *  defined by the user are fired.
 * Created by 7flying on 05/08/2014.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       Intent service = new Intent(context, AlertService.class);
       context.startService(service);
    }
}
