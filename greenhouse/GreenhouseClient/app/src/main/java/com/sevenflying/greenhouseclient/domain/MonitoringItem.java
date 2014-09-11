package com.sevenflying.greenhouseclient.domain;


import com.sevenflying.greenhouseclient.app.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** MonitoringItem class.
 * Created by 7flying on 10/08/2014.
 */
public class MonitoringItem implements Serializable {

    private String name;
    private Map<String, Sensor> attachedSensors; // key: pinId + type
    private int icon, warningIcon;
    private String photoPath;
    private boolean isWarningEnabled;

    public MonitoringItem(String name) {
        this.name = name;
        attachedSensors = new HashMap<String, Sensor>();
        icon = R.drawable.ic_leaf_green;
        warningIcon = R.drawable.ic_warning_orange;
        isWarningEnabled = false;
        photoPath = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSensor(Sensor s) {
        if(s != null)
            attachedSensors.put(s.getPinId() + s.getType().getIdentifier(), s);
    }

    public List<Sensor> getAttachedSensors(){
        return new ArrayList<Sensor>(attachedSensors.values());
    }

    public boolean hasSensorAttached(String key) {
        return attachedSensors.containsKey(key);
    }

    public Sensor getSensorByKey(String key) {
        return attachedSensors.get(key);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int resource) {
        icon = resource;
    }

    public boolean isWarningEnabled() {
        return isWarningEnabled;
    }

    public void setWarningEnabled(boolean warningEnabled) {
        isWarningEnabled = warningEnabled;
    }

    public int getWarningIcon() {
        return warningIcon;
    }

    public String getPhotoPath() { return photoPath; }

    public void setPhotoPath(String path) { this.photoPath = path; }

    public String toStoreString() {
        String ret = "";
        ret += name;
        ret += ":";
        ret += String.valueOf(isWarningEnabled);
        ret += ":";
        ret += photoPath;
        for(String key : attachedSensors.keySet()) {
            ret += ":";
            // Pin
            ret += key.substring(0, key.length() - 1);
            ret += ":";
            // Type
            ret += key.charAt(key.length() - 1);
        }
        return  ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoringItem that = (MonitoringItem) o;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return true;
    }

}
