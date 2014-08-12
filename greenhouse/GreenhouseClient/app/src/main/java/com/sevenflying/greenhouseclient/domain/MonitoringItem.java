package com.sevenflying.greenhouseclient.domain;


import com.sevenflying.greenhouseclient.app.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** MonitoringItem class.
 * Created by 7flying on 10/08/2014.
 */
public class MonitoringItem implements Serializable {

    private String name;
    private List<Sensor> attachedSensors;
    private int icon, warningIcon;
    private boolean isWarningEnabled;

    public MonitoringItem(String name) {
        this.name = name;
        attachedSensors = new ArrayList<Sensor>();
        icon = R.drawable.ic_leaf_green; // TODO will crash here when we add camera/gallery stuff
        warningIcon = R.drawable.ic_warning_orange;
        isWarningEnabled = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSensor(Sensor s) {
        attachedSensors.add(s);
    }

    public List<Sensor> getAttachedSensors(){
        return attachedSensors;
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

}
