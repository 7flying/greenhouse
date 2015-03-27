package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.sensortab.SensorStatusActivity;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.app.utils.ImageLoader;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.net.Communicator;

/** Holds the actuator info.
 * Created by flying on 08/02/15.
 */
public class ActuatorStatusActivity extends ActionBarActivity {

    private static ImageLoader loader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (loader == null)
            loader = new ImageLoader(getApplicationContext());

        setContentView(R.layout.activity_actuator_status);
        TextView tvActuatorName = (TextView) findViewById(R.id.text_actuator_name);
        TextView tvPin = (TextView) findViewById(R.id.text_actuator_pin);
        LinearLayout layoutOptionalControlSensor = (LinearLayout) findViewById(R.id
                .layout_optional_control_sensor);
        ImageView controlSensorIm = (ImageView) findViewById(R.id.image_control_sensor);
        TextView controlSensorName = (TextView) findViewById(R.id.text_name_control_sensor);
        TextView controlSensorPin = (TextView) findViewById(R.id.text_pin_sensor);
        TextView controlType = (TextView) findViewById(R.id.text_control_type);
        TextView controlValue = (TextView) findViewById(R.id.text_control_value);
        TextView controlUnit = (TextView) findViewById(R.id.control_unit);
        RelativeLayout sensorLayout = (RelativeLayout) findViewById(R.id
                .layout_optional_control_sensor_data);

        if (getIntent().hasExtra(Extras.EXTRA_ACTUATOR)) {
            layoutOptionalControlSensor.setVisibility(View.VISIBLE);
            final Actuator temp = (Actuator) getIntent().getSerializableExtra(Extras.EXTRA_ACTUATOR);
            tvActuatorName.setText(temp.getName());
            tvPin.setText(temp.getPinId());
            if (temp.hasControlSensor()) {
                layoutOptionalControlSensor.setVisibility(View.VISIBLE);
                loader.loadBitmapResource(temp.getControlSensor().getDrawableId(), controlSensorIm);
                controlSensorName.setText(temp.getControlSensor().getName());
                controlSensorPin.setText(temp.getControlSensor().getPinId());
                controlType.setText(temp.getCompareType().getSymbol());
                controlValue.setText(Double.toString(temp.getCompareValue()));
                controlUnit.setText(temp.getControlSensor().getType().getUnit());
                sensorLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ActuatorStatusActivity.this,
                                SensorStatusActivity.class);
                        intent.putExtra(Extras.EXTRA_SENSOR, temp.getControlSensor());
                        startActivity(intent);
                    }
                });
            } else
                layoutOptionalControlSensor.setVisibility(View.GONE);
            Button buttonLaunch = (Button) findViewById(R.id.button_launch);
            buttonLaunch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Communicator comm = Communicator.getInstance(getBaseContext());
                    comm.launchActuator(temp.getPinId());
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
