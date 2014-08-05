package com.sevenflying.greenhouseclient.domain;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
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
                    sendNotification(alert, lastValue);
                }
            }
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
        }catch (Exception e) {
            e.printStackTrace();
        }
        return lastValue;
    }

    /**Sends an "alert is fired" notification
     * @param alert - alert that is fired
     * @param lastValue - last value of the sensor that fired the alert
     */
    private void sendNotification(Alert alert, double lastValue) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(getResources().getString(R.string.logic_alert_on_sensor))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getResources().getString(R.string.logic_alert_on_sensor) +
                                        " : " + alert.getSensorName()))
                        .setContentText(alert.getSensorName() + " " + alert.getSensorType().getUnit()
                                + " " + alert.getAlertType().getSymbol() + " " + lastValue);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(0, mBuilder.build());
    }
}
