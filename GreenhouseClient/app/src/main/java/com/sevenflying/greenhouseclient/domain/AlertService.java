package com.sevenflying.greenhouseclient.domain;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sevenflying.greenhouseclient.app.MainActivity;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;

import java.util.List;


/** AlertService checks whether the alerts defined by the user are fired.
 *  If so sends a notification and toggles a warning.
 * Created by 7flying on 05/08/2014.
 */
public class AlertService extends IntentService {

    private DBManager manager;

    public AlertService() {
        super("AlertService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        manager = new DBManager(getApplicationContext());
        Log.d(Constants.DEBUGTAG, "# ~ AlertService started");
        checkAlerts();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /** Checks if any alert is fired
     */
    private void checkAlerts() {
        Log.d(Constants.DEBUGTAG, "(AlertService.checkAlerts()) - launched");
        List<Alert> alerts = manager.getAlerts();
        Log.d(Constants.DEBUGTAG, "(AlertService.checkAlerts()) - there are " + alerts.size() + " alerts");
        int alertCount = alerts.size() - 1;
        for (Alert alert : alerts) {
            Log.d(Constants.DEBUGTAG, "(AlertService.checkAlerts() - alert: " + alert.toString());
            if(alert.isOn()) {
                // Get the latest value from the server
                Log.d(Constants.DEBUGTAG, "(AlertService.checkAlerts()) - alert is active");
                int errors = 0;
                Exception e = null;
                double lastValue = -1;
                do {
                    try {
                        Communicator comm = Communicator.getInstance(getApplicationContext());
                        lastValue = comm.getLastValue(alert.getSensorPinId(),
                                Character.valueOf(alert.getSensorType().getIdentifier()).toString());
                        Log.d(Constants.DEBUGTAG,"(AlertService.checkAlerts() - alert's sensor " +
                            "last value: " + lastValue);
                        e = null;
                    } catch (Exception ex) {
                        e = ex;
                        errors++;
                    }
                } while(e != null && errors < 3);
                boolean setWarning = alert.isFired(lastValue);
                // update the warning: set active or remove
                setWarning(setWarning, alert.getSensorPinId(),
                        String.valueOf(alert.getSensorType().getIdentifier()));
                if (setWarning) {
                    Log.d(Constants.DEBUGTAG, "(AlertService.checkAlerts() - alert is fired");
                    sendNotification(alert, lastValue, alertCount);
                }
            } else {
                // If the alert has been switched off remove warnings (if present)
                setWarning(false, alert.getSensorPinId(),
                        String.valueOf(alert.getSensorType().getIdentifier()));
            }
            alertCount--;
        }
    }

    /** Sends an "alert is fired" notification
     * @param alert - alert that is fired
     * @param lastValue - last value of the sensor that fired the alert
     * @param notifyId - notification id, different values trigger different notifications, the same
     *                 value updates the notification.
     */
    private void sendNotification(Alert alert, double lastValue, int notifyId) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        // Create notification
        String contentText = alert.getSensorName() + " " + alert.getSensorType().toString()
                + " " + alert.getAlertType().getSymbol() + " " + alert.getCompareValue()
                + " " + alert.getSensorType().getUnit();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                Sensor.getDrawableIdClearFromType(alert.getSensorType())))
                        .setSmallIcon(R.drawable.ic_leaf)
                        .setContentTitle(getResources().getString(R.string.logic_alert_on_sensor))
                        .setContentText(contentText)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle(alert.getSensorName()
                                        + " " + alert.getSensorType().toString() + ": " + lastValue
                                        + "" + alert.getSensorType().getUnit())
                                .setSummaryText(contentText)
                             );
        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(notifyId, mBuilder.build());
    }

    /** Marks all the monitoring items with an alert
     * @param active -  set the warning active or not
     * @param sensorPinId - pin id of the sensor that fired the alert
     * @param sensorType - type of the sensor that fired the alert
     */
    private void setWarning(boolean active, String sensorPinId, String sensorType){
        List<MonitoringItem> listItems = manager.getItems();
        for (MonitoringItem item : listItems) {
            if (item.getSensorByKey(sensorPinId + sensorType) != null)
                manager.setWarning(active, item);
        }
    }
}
