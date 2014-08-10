package com.sevenflying.greenhouseclient.domain;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;

import com.sevenflying.greenhouseclient.app.MainActivity;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;


/** AlertService checks whether the alerts defined by the user are fired.
 *  If so sends a notification.
 * Created by 7flying on 05/08/2014.
 */
public class AlertService extends IntentService {

    public AlertService() {
        super("AlertService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkAlerts();
    }

    /** Checks if any alert is fired
     */
    private void checkAlerts() {
        AlertManager manager = AlertManager.getInstance(getApplicationContext());
        List<Alert> alerts = manager.getAlerts();
        int alertCount = alerts.size() - 1;
        for(Alert alert : alerts) {
            if(alert.isActive()) {
                int errors = 0;
                Exception e = null;
                double lastValue = -1;
                do {
                    try {
                        lastValue = getLastValue(alert.getSensorPinId(), Character.valueOf(alert.getSensorType().getIdentifier()).toString());
                        e = null;
                    } catch (Exception ex) {
                        e = ex;
                        errors++;
                    }
                } while(e != null && errors < 3);
                if(alert.isFired(lastValue)) {
                    sendNotification(alert, lastValue, alertCount);
                }
            }
            alertCount--;
        }
    }

    /** Gets the sensor's last value from the server.
     * @param sensorPinId - sensor's pin id
     * @param sensorType - sensor's type
     * @return sensor's last value
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private double getLastValue(String sensorPinId, String sensorType)
        throws ClassNotFoundException, IOException
    {
        double lastValue = -3;
        try {
            InetAddress add = InetAddress.getByName(Constants.serverIP);
            Socket s = new Socket(add, Constants.serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.CHECK);
            oos.flush();
            oos.writeObject(sensorPinId + ":" + sensorType);
            oos.flush();
            lastValue = Double.valueOf(new String(Base64.decode( ((String) ois.readObject()).getBytes(), Base64.DEFAULT)));
            s.close();
            oos.close();
            ois.close();
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return lastValue;
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
}
