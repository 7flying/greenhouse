package com.sevenflying.greenhouseclient.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.MoniItemManager;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;

/** Manages the results when we ask an activity for a result.
 * Created by 7flying on 25/09/2014.
 */
public class ActivityResultHandler {

    public static void handleCreateNewAlert(Context context, Intent data, Activity activity) {
        DBManager manager = new DBManager(context);
        Alert a = (Alert) data.getSerializableExtra("alert");
        // Check if an alert of the same type on the sensor is created
        if(manager.hasAlertsCreatedFrom(
                a.getSensorPinId(), a.getSensorType(), a.getAlertType()))
        {
            // Display message telling to the user that he/she cannot create two alerts
            // of the same type on the same sensor
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(context.getResources().getString(
                    R.string.alert_repeated_notification));
            builder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {}
            });
            builder.show();
        } else {
            manager.addAlert(a);
            Toast.makeText(context,context.getResources().getString(R.string.alert_created),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void handleCreateNewMoniItem(Context context, Intent data, Activity activity) {
        MonitoringItem item = (MonitoringItem) data.getExtras().getSerializable("moni-item");
        MoniItemManager moniManager = MoniItemManager.getInstance(
                context);
        moniManager.addItem(item);
        moniManager.commit();
        Toast.makeText(context, context.getString(R.string.item_created), Toast.LENGTH_SHORT)
                .show();
    }
}
