package com.sevenflying.greenhouseclient.domain;

import com.sevenflying.greenhouseclient.app.R;

import java.io.Serializable;

/** Actuator class.
 * Created by 7flying on 11/08/2014.
 */
public class Actuator implements Serializable {

    private String name;
    private String pinId;
    private Alert controlAlert;
    private int icon;

    public  Actuator(String name, String pinId) {
        this.name = name;
        this.pinId = pinId;
        this.controlAlert = null;
        icon = R.drawable.ic_launch_orange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public int getIcon() {
        return icon;
    }

    /** Checks whether the Actuator has a control alert or not.
     * @return boolean     */
    public boolean hasControlAlert() {
        return !(controlAlert == null);
    }

    public void launch() {
        // TODO get last value from alert sensor and if alert is not fired launch
        // TODO otherwise show warning
    }
}
