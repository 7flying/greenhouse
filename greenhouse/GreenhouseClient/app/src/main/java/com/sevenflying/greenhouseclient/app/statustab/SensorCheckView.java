package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;

/** SensorCheckView class. Used to show list with check-buttons
 * Created by 7flying on 12/08/2014.
 */
public class SensorCheckView extends RelativeLayout {

    private TextView sensorName;
    private TextView sensorPin;
    private ImageView sensorDefaultImage;
    private CheckBox checkBox;

    public static SensorCheckView inflate(ViewGroup parent) {
        return (SensorCheckView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_check_view, parent, false);
    }

    public SensorCheckView(Context context) {
        super(context, null);
    }

    public SensorCheckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SensorCheckView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.sensor_check_row, this, true);
        sensorName = (TextView) findViewById(R.id.text_sensor_name);
        sensorPin = (TextView) findViewById(R.id.text_sensor_pin);
        sensorDefaultImage = (ImageView) findViewById(R.id.icon_sensor);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
    }

    public void setSensor(Sensor sensor) {
        sensorName.setText(sensor.getName());
        sensorPin.setText(" - " + sensor.getPinId());
        sensorDefaultImage.setImageResource(sensor.getDrawableId());
        checkBox.setEnabled(true);
        checkBox.setSelected(false);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
