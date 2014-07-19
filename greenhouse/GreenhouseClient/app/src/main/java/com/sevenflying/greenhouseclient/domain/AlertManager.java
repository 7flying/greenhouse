package com.sevenflying.greenhouseclient.domain;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by 7flying on 15/07/2014.
 */
public class AlertManager {

    private static AlertManager manager = null;
    private static final String FILE_NAME = "greenhouse_alert_manager";
    private Map <String, List <Alert>> mapSensorAlerts;
    private Context context;

    public static AlertManager getInstance(Context context){
        if(manager == null)
            manager = new AlertManager(context);
        return manager;
    }

    private AlertManager(Context context) {
        mapSensorAlerts = new HashMap<String, List<Alert>>();
        this.context = context;
        try {
            loadAlerts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Checks if any of the alerts related to the sensor are fired.
     *  Returns a list containing the alerts that were fired.
     * @param pinId - pin id from the Sensor
     * @param value - value to compare
     * @return list of fired alerts. Empty list if no alerts were fired.*/
    public List<Alert> checkAlertsFrom(String pinId, SensorType type, double value) {
        ArrayList<Alert> ret = new ArrayList<Alert>();
        if(mapSensorAlerts.containsKey(pinId)) {
            for(Alert alert : mapSensorAlerts.get(pinId)) {
                if(alert.getSensorType() == type) {
                    if (alert.isFired(value)) {
                        ret.add(alert);
                        // TODO TESTING
                        Toast.makeText(context, "Alert fired", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return ret;
    }

    /** Adds an alert to the Manager. Alerts cannot be repeated.
     * @param a - Alert to add
     */
    public void addAlert(Alert a) {
        if(!mapSensorAlerts.containsKey(a.getSensorPinId())){
            mapSensorAlerts.put(a.getSensorPinId(), new ArrayList<Alert>());
            mapSensorAlerts.get(a.getSensorPinId()).add(a);
        } else {
            // Check if the alert is repeated
            if(!mapSensorAlerts.get(a.getSensorPinId()).contains(a))
                mapSensorAlerts.get(a.getSensorPinId()).add(a);
        }
    }

    /** Returns a list of the stored alerts.
     * @return list containing the alerts
     */
    public List<Alert> getAlerts() {
       List<Alert> ret = new ArrayList<Alert>();
       for(String key : mapSensorAlerts.keySet())
            ret.addAll(mapSensorAlerts.get(key));
       return ret;
    }

    /** Loads the stored alerts.   */
    private void loadAlerts() throws Exception{
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.openFileInput(FILE_NAME)));
            String line = "";
            while( (line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ":");
                Alert temp = new Alert();
                List<String> list = new ArrayList<String>(6);
                while(tokenizer.hasMoreTokens()) {
                    String res = new String(Base64.decode(tokenizer.nextToken().getBytes(),
                            Base64.DEFAULT));
                    list.add(res);
                }
                temp.setAlertType(list.get(0));
                temp.setCompareValue(Double.parseDouble(list.get(1)));
                temp.setActive(list.get(2).equals("1"));
                temp.setSensorPinId(list.get(3));
                temp.setSensorName(list.get(4));
                temp.setSensorType(list.get(5).charAt(0));
                addAlert(temp);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Makes the changes made on the alerts persistent.*/
    public void commit() {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            String toWrite = "";
            for(String key : mapSensorAlerts.keySet()) {
                for(Alert a : mapSensorAlerts.get(key)) {
                    toWrite += a.toStoreString();
                }
            }
            fos.write(toWrite.getBytes());
            fos.flush();
            fos.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}

























