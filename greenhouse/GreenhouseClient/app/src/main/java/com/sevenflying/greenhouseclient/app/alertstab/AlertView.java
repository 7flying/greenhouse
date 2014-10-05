package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.net.Constants;

/** AlertView class.
 * Created by 7flying on 15/07/2014.
 */
public class AlertView extends LinearLayout {

    private ToggleButton toggle;
    private TextView textSensorName;
    private TextView textSensorType;
    private TextView textAlertTypeSymbol;
    private TextView textCompareValue;
    private TextView textSensorUnit;

    public static  AlertView inflate(ViewGroup parent) {
       return (AlertView) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.alert_view, parent, false);
    }

    public AlertView(Context context) {
        this(context, null);
    }

    public AlertView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlertView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.alert_list_row, this, true);
        toggle = (ToggleButton) findViewById(R.id.button_on_off);
        textSensorName = (TextView) findViewById(R.id.alert_sensor_name);
        textSensorType = (TextView) findViewById(R.id.alert_sensor_type);
        textAlertTypeSymbol = (TextView) findViewById(R.id.alert_type_symbol);
        textCompareValue = (TextView) findViewById(R.id.alert_compare_value);
        textSensorUnit = (TextView) findViewById(R.id.alert_sensor_unit);
    }

    public void setAlert(Alert alert) {
        Log.d(Constants.DEBUGTAG, " $ On Alert View set alert");
        toggle.setEnabled(true);
        toggle.setChecked(alert.isActive());
        textSensorName.setText(alert.getSensorName());
        textSensorType.setText(alert.getSensorType().toString().toLowerCase());
        textAlertTypeSymbol.setText(alert.getAlertType().getSymbol());
        textCompareValue.setText(GreenhouseUtils.suppressZeros(alert.getCompareValue()));
        textSensorUnit.setText(alert.getSensorType().getUnit());
    }

    public ToggleButton getToggle() {
        return toggle;
    }

    public TextView getTextSensorName() {
        return textSensorName;
    }

    public TextView getTextSensorType() {
        return textSensorType;
    }

    public TextView getTextAlertTypeSymbol() {
        return textAlertTypeSymbol;
    }

    public TextView getTextCompareValue() {
        return textCompareValue;
    }

    public TextView getTextSensorUnit() {
        return textSensorUnit;
    }

}
