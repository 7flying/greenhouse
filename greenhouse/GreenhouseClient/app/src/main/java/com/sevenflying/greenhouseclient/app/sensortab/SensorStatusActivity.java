package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;


/** Activity to show further info about a sensor.
 * Created by 7flying on 13/07/2014.
 */
public class SensorStatusActivity extends FragmentActivity {

    private ImageView imageView;
    private TextView textSensorValue;
    private TextView textSensorUnit;
    private TextView textSensorUpdatedAt;
    private TextView textSensorName;
    private TextView textSensorType;
    private TextView textSensorRefresh;
    private TextView textSensorPin;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_sensor_status);
        // Views
        imageView = (ImageView) findViewById(R.id.image_sensor);
        textSensorValue = (TextView) findViewById(R.id.text_sensor_value);
        textSensorUnit = (TextView) findViewById(R.id.text_sensor_value_unit);
        textSensorUpdatedAt = (TextView) findViewById(R.id.text_sensor_updated_at);
        textSensorName = (TextView) findViewById(R.id.text_sensor_name);
        textSensorType = (TextView) findViewById(R.id.text_sensor_type);
        textSensorRefresh = (TextView) findViewById(R.id.text_sensor_refresh);
        textSensorPin = (TextView) findViewById(R.id.text_sensor_pin);
        // Set data
        if(getIntent().hasExtra("sensor")) {
            Sensor s = (Sensor) getIntent().getSerializableExtra("sensor");
            imageView.setImageResource(s.getDrawableId());
            textSensorValue.setText(Double.toString(s.getValue()));
            textSensorUnit.setText( s.getType().getUnit());
            textSensorUpdatedAt.setText(s.getUpdatedAt());
            textSensorName.setText(s.getName());
            textSensorType.setText(s.getType().toString());
            textSensorRefresh.setText( Double.toString(s.getRefreshRate() / 1000d) );
            textSensorPin.setText(s.getPinId());
        }
    }
}
