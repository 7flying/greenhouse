package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;

/** Activity to show further info about a sensor.
 * Created by 7flying on 13/07/2014.
 */
public class SensorStatusActivity extends FragmentActivity {

    private TextView tvSensor;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_sensor_status);
        tvSensor = (TextView) findViewById(R.id.show_data_here);
        if(getIntent().hasExtra("sensor")) {
            tvSensor.setText( ((Sensor) getIntent().getSerializableExtra("sensor")).toString() );
        }
    }
}
