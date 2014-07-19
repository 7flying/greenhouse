package com.sevenflying.greenhouseclient.app.sensortab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Sensor;

/**
 * Created by 7flying on 28/06/2014.
 */
public class SensorView extends RelativeLayout {

    private TextView sensorName;
    private TextView sensorPin;
    private TextView sensorValue;
    private TextView sensorUnit;
    private ImageView sensorDefaultImage;

    public static SensorView inflate(ViewGroup parent) {
        SensorView sensorView = (SensorView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_view, parent, false);
        return sensorView;
    }

    public SensorView(Context context) {
        this(context, null);
    }

    public SensorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SensorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.sensor_list_row, this, true);
        sensorName = (TextView) findViewById(R.id.text_sensor_name);
        sensorPin = (TextView) findViewById(R.id.text_sensor_pin);
        sensorValue = (TextView) findViewById(R.id.text_sensor_value);
        sensorUnit = (TextView) findViewById(R.id.text_sensor_unit);
        sensorDefaultImage = (ImageView) findViewById(R.id.icon_sensor);
    }

    public void setSensor(Sensor sensor) {
        sensorName.setText(sensor.getName());
        sensorPin.setText(" - " + sensor.getPinId());
        sensorValue.setText(GreenhouseUtils.suppressZeros(sensor.getValue()));
        sensorUnit.setText(sensor.getType().getUnit());
        sensorDefaultImage.setImageResource(sensor.getDrawableId());
    }

    public TextView getSensorName() {
        return sensorName;
    }

    public TextView getSensorPin() {
        return sensorPin;
    }

    public TextView getSensorValue() {
        return sensorValue;
    }

    public TextView getSensorUnit() {
        return sensorUnit;
    }

    public ImageView getSensorDefaultImage() {
        return sensorDefaultImage;
    }
}

