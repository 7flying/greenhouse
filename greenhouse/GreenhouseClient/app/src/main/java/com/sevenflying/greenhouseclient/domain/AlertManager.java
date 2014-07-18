package com.sevenflying.greenhouseclient.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 7flying on 15/07/2014.
 */
public class AlertManager {

    private static AlertManager manager = null;

    private Map <String, List <Alert>> mapSensorAlerts;

    public static AlertManager getInstance(){
        if(manager == null)
            manager = new AlertManager();
        return manager;
    }

    private AlertManager() {
        mapSensorAlerts = new HashMap<String, List<Alert>>();
        // TODO for testing purposes
        addAlert(new Alert(AlertType.GREATER, 30.7, true, "A02", "DHT-22", SensorType.TEMPERATURE));
        addAlert(new Alert(AlertType.LESS_EQUAL, 20.78, true, "A02", "DHT-22", SensorType.HUMIDITY));
        addAlert(new Alert(AlertType.EQUAL, 200, true, "A07", "THERMISTOR", SensorType.LIGHT));
    }

    /* Checks if any of the alerts related to the sensor are fired.
     *  Returns a list containing the alerts that were fired.*/

    /** Checks if any of the alerts related to the sensor are fired.
     *  Returns a list containing the alerts that were fired.
     * @param pinId - pin id from the Sensor
     * @param value - value to compare
     * @return list of fired alerts. Empty list if no alerts were fired.*/
    public List<Alert> checkAlertsFrom(String pinId, double value) {
        ArrayList<Alert> ret = new ArrayList<Alert>();
        if(mapSensorAlerts.containsKey(pinId)) {
            for(Alert alert : mapSensorAlerts.get(pinId)) {
                if (alert.isFired(value)) {
                    ret.add(alert);
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

    public List<Alert> getAlerts() {
       List<Alert> ret = new ArrayList<Alert>();
       for(String key : mapSensorAlerts.keySet())
            ret.addAll(mapSensorAlerts.get(key));
       return ret;
    }

}

























