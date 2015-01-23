package com.sevenflying.greenhouseclient.domain;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.sevenflying.greenhouseclient.app.MainActivity;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.net.Communicator;

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
        checkAlerts();
    }

    /** Checks if any alert is fired
     */
    private void checkAlerts() {

        List<Alert> alerts = manager.getAlerts();
        int alertCount = alerts.size() - 1;
        for(Alert alert : alerts) {
            if(alert.isActive()) {
                int errors = 0;
                Exception e = null;
                double lastValue = -1;
                do {
                    try {
                        lastValue = Communicator.getLastValue(alert.getSensorPinId(),
                                Character.valueOf(alert.getSensorType().getIdentifier()).toString());
                        e = null;
                    } catch (Exception ex) {
                        e = ex;
                        errors++;
                    }
                } while(e != null && errors < 3);
                if(alert.isFired(lastValue)) {
                    sendNotification(alert, lastValue, alertCount);
                    setWarning(alert.getSensorPinId(),
                            String.valueOf(alert.getSensorType().getIdentifier()));
                }
            }
            alertCount--;
        }
    }

    /**Sends an "alert is fired" notification
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
     * @param sensorPinId - pin id of the sensor that fired the alert
     * @param sensorType - type of the sensor that fired the alert
     */
    private void setWarning(String sensorPinId, String sensorType){
        List<MonitoringItem> listItems = manager.getItems();
        for(MonitoringItem item : listItems) {
            if(!item.isWarningEnabled()) {
                if(item.getSensorByKey(sensorPinId + sensorType) != null)
                    item.setWarningEnabled(true);
            }
        }
    }
}
