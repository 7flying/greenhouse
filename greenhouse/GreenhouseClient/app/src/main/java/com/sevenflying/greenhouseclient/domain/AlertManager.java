package com.sevenflying.greenhouseclient.domain;

import android.content.Context;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/** Manages the application's alerts
 * Created by 7flying on 15/07/2014.
 */
public class AlertManager {

    private static AlertManager manager = null;
    private static final String FILE_NAME = "greenhouse_alert_manager";
    private Map <String, List <Alert>> mapSensorAlerts; // key: concatenation of pinId+sensorType
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
    public synchronized List<Alert> checkAlertsFrom(String pinId, SensorType type, double value) {
        ArrayList<Alert> ret = new ArrayList<Alert>();
        if(mapSensorAlerts.containsKey(pinId + type.getIdentifier())) {
            for(Alert alert : mapSensorAlerts.get(pinId + type.getIdentifier())) {
                if (alert.isFired(value)) {
                    ret.add(alert);
                }
            }
        }
        return ret;
    }

    /** Checks whether the manager has alerts created concerning a sensor
     * @param pinId - pin id from the Sensor to check
     * @param type - type of the Sensor to check
     * @return true if it has
     */
    public synchronized boolean hasAlertsCreatedFrom(String pinId, SensorType type) {
        return mapSensorAlerts.containsKey(pinId + type.getIdentifier());
    }

    /** Adds an alert to the Manager. Alerts cannot be repeated.
     * @param a - Alert to add
     */
    public synchronized void addAlert(Alert a) {
        if(!mapSensorAlerts.containsKey(a.getSensorPinId() + a.getSensorType().getIdentifier())){
            mapSensorAlerts.put(a.getSensorPinId() + a.getSensorType().getIdentifier(),
                    new ArrayList<Alert>());
            mapSensorAlerts.get(a.getSensorPinId() + a.getSensorType().getIdentifier()).add(a);
        } else {
            // Check if the alert is repeated
            if(!mapSensorAlerts.get(a.getSensorPinId() + a.getSensorType().getIdentifier()).contains(a))
                mapSensorAlerts.get(a.getSensorPinId() + a.getSensorType().getIdentifier()).add(a);
        }
    }

    /** Removes and alert from the Manager.
     * @param a - Alert to remove
     */
    public synchronized  void removeAlert(Alert a) {
        if(mapSensorAlerts.containsKey(a.getSensorPinId() + a.getSensorType().getIdentifier())) {
            mapSensorAlerts.get(a.getSensorPinId() + a.getSensorType().getIdentifier()).remove(a);
        }
    }

    /** Returns a list of the stored alerts.
     * @return list containing the alerts
     */
    public synchronized List<Alert> getAlerts() {
       List<Alert> ret = new ArrayList<Alert>();
       for(String key : mapSensorAlerts.keySet())
            ret.addAll(mapSensorAlerts.get(key));
       return ret;
    }

    /** Loads the stored alerts.   */
    private synchronized void loadAlerts() throws Exception {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.openFileInput(FILE_NAME)));
            String line = null;
            while( (line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ":");
                Alert temp = new Alert();
                List<String> list = new ArrayList<String>(6);
                while(tokenizer.hasMoreTokens()) {
                    list.add(new String(Base64.decode(tokenizer.nextToken().getBytes(),
                            Base64.DEFAULT)));
                }
                if(list.size() == 6) {
                    temp.setAlertType(list.get(0));
                    temp.setCompareValue(Double.parseDouble(list.get(1)));
                    temp.setActive(list.get(2).equals("1"));
                    temp.setSensorPinId(list.get(3));
                    temp.setSensorName(list.get(4));
                    temp.setSensorType(list.get(5).charAt(0));
                    addAlert(temp);
                } else
                    throw new Exception("Alert couldn't be read");
            }
        }catch (FileNotFoundException e) {
            // No alerts loaded
        }catch (IOException e ){
            e.printStackTrace();
        }
    }

    /** Makes the changes made on the alerts persistent.*/
    public synchronized void commit() {
        try{
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            String toWrite = "";
            for (String key : mapSensorAlerts.keySet()) {
                for (Alert a : mapSensorAlerts.get(key)) {
                    toWrite += a.toStoreString() + "\n";
                }
            }
            fos.write(toWrite.getBytes());
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e) {
            // Create if it doesn't exist
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
