package com.sevenflying.greenhouseclient.app;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Constants;

/** Manages the results when we ask an activity for a result.
 * Created by 7flying on 25/09/2014.
 */
public class ActivityResultHandler {

    /** Handles the callback from the creation of a new Alert
     * @param context
     * @param data
     * @param activity
     */
    public static void handleCreateNewAlert(Context context, Intent data, Activity activity) {
        DBManager manager = new DBManager(context);
        Alert a = (Alert) data.getSerializableExtra(Extras.EXTRA_ALERT);
        Log.d(Constants.DEBUGTAG, " handleCreateNewAlert: arg " + a.toString());
        // Check if an alert of the same type on the sensor is created
        if (manager.hasAlertsCreatedFrom(a.getSensorPinId(), a.getSensorType(), a.getAlertType())) {
            Log.d(Constants.DEBUGTAG, " handleCreateNewAlert: FAIL message ");
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
            Log.d(Constants.DEBUGTAG, " handleCreateNewAlert: OK message ");
            manager.addAlert(a);
            Toast.makeText(context,context.getResources().getString(R.string.alert_created),
                    Toast.LENGTH_SHORT).show();
        }
    }
    /** Handles the callback from the creation of a new MonitoringItem
     * @param context
     * @param data
     */
    public static void handleCreateNewMoniItem(Context context, Intent data) {
        MonitoringItem item = (MonitoringItem) data.getExtras().getSerializable(Extras.EXTRA_MONI);
        DBManager manager = new DBManager(context);
        manager.addItem(item);
        Toast.makeText(context, context.getString(R.string.item_created), Toast.LENGTH_SHORT)
                .show();
    }

    /** Handles the callback from the edition of a MonitoringItem
     * @param context
     * @param data
     */
    public static void handleEditMoniItem(Context context, Intent data) {
        MonitoringItem itemEdited = (MonitoringItem) data
                .getSerializableExtra(Extras.EXTRA_MONI_RESULT);
        Log.d(Constants.DEBUGTAG, " $ MainAct extraItem callback EDIT_MONI_ITEM: "
                + itemEdited.toString());
        DBManager manager = new DBManager(context);
        manager.deleteItem(itemEdited.getId());
        manager.addItem(itemEdited);

        Toast.makeText(context, R.string.item_edited, Toast.LENGTH_SHORT).show();
    }

    /** Handles the callback from the creation of a new Sensor.
     * @param context
     */
    public static void handleCreateNewSensor(Context context) {
        Toast.makeText(context, context.getString(R.string.sensor_created), Toast.LENGTH_SHORT)
                .show();
    }
    /** Handles the callback from the edition of an Alert
     * @param context
     * @param data
     */
    public static void handleEditAlert(Context context, Intent data) {
        Alert a = (Alert) data.getSerializableExtra(Extras.EXTRA_ALERT);
        Log.d(Constants.DEBUGTAG, " $ Calling edit alert to ->" + a.toString());
        DBManager manager = new DBManager(context);
        manager.updateAlertCompareValue(a);

        Toast.makeText(context, context.getResources().getString(R.string.alert_edited),
                Toast.LENGTH_SHORT).show();
    }

    /** Handles the callback from the edition of a sensor
     * @param context
     * @param data
     */
    public static void handleEditSensor(Context context, Intent data) {
        Sensor s = (Sensor) data.getSerializableExtra(Extras.EXTRA_SENSOR);
        Log.d(Constants.DEBUGTAG, " $ Calling edit sensor to ->" + s.toString());
        DBManager manager = new DBManager(context);
        manager.editSensor(s);
        Toast.makeText(context, context.getResources().getString(R.string.sensor_modified),
                Toast.LENGTH_SHORT).show();
    }

    /** Handles the callback from the creation of an Actuator
     * @param context
     */
    public static void handleCreateNewActuator(Context context) {
        Toast.makeText(context, context.getString(R.string.actuator_created), Toast.LENGTH_SHORT)
                .show();
    }

    /** Handles the callback from the deletion of an Actuator
     * @param context
     */
    public static void handleDeleteActuator(Context context) {
        Toast.makeText(context, context.getString(R.string.actuator_deleted), Toast.LENGTH_SHORT)
                .show();
    }

    /** Handles the callback from the modification of an Actuator
     * @param context
     */
    public static void handleModifyActuator(Context context, Intent data) {
        Actuator a = (Actuator) data.getSerializableExtra(Extras.EXTRA_ACTUATOR);
        DBManager manager = new DBManager(context);
        manager.updateActuator(a);
        Toast.makeText(context, context.getString(R.string.actuator_modified), Toast.LENGTH_SHORT)
                .show();
    }
}