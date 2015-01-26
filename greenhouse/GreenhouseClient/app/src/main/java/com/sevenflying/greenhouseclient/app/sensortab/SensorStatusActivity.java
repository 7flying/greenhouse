package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.HistoricalRecordObtainer;

/** Activity to show further info about a sensor.
 * Created by 7flying on 13/07/2014.
 */
public class SensorStatusActivity extends ActionBarActivity {

    private Sensor currentSensor;
    LinearLayout layoutProgress;
    LinearLayout layoutChart;
    LineChart chart;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        setContentView(R.layout.activity_sensor_status);
        // Views
        ImageView imageView = (ImageView) findViewById(R.id.image_sensor);
        TextView textSensorValue = (TextView) findViewById(R.id.text_sensor_value);
        TextView textSensorUnit = (TextView) findViewById(R.id.text_sensor_value_unit);
        TextView textSensorUpdatedAt = (TextView) findViewById(R.id.text_sensor_updated_at);
        TextView textSensorName = (TextView) findViewById(R.id.text_sensor_name);
        TextView textSensorType = (TextView) findViewById(R.id.text_sensor_type);
        TextView textSensorRefresh = (TextView) findViewById(R.id.text_sensor_refresh);
        TextView textSensorPin = (TextView) findViewById(R.id.text_sensor_pin);
        layoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
        layoutChart = (LinearLayout) findViewById(R.id.layout_chart);

        chart = (LineChart) findViewById(R.id.chart);
        // Set data
        if(getIntent().hasExtra("sensor")) {
            currentSensor = (Sensor) getIntent().getSerializableExtra("sensor");
            imageView.setImageResource(currentSensor.getDrawableId());
            textSensorValue.setText(GreenhouseUtils.suppressZeros(currentSensor.getValue()));
            textSensorUnit.setText(currentSensor.getType().getUnit());
            textSensorUpdatedAt.setText(currentSensor.getUpdatedAt());
            textSensorName.setText(currentSensor.getName());
            textSensorType.setText(currentSensor.getType().toString());
            textSensorRefresh.setText(Double.toString(currentSensor.getRefreshRate() / 1000d) );
            textSensorPin.setText(currentSensor.getPinId());
            getHistoricalData();
        }

        // if enabled, the chart will always start at zero on the y-axis
        chart.setStartAtZero(false);
        // disable the drawing of values into the chart
        chart.setDrawYValues(true);
        chart.setDrawXLabels(true);
        chart.setDrawBorder(false);

        // no description text
        chart.setDescription("");

        // enable value highlighting
        chart.setHighlightEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(false);

        // enable scaling and dragging
        chart.setDragEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sensor_fragment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getHistoricalData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHistoricalData() {
        HistoricalRecordObtainer hro = new HistoricalRecordObtainer(currentSensor.getPinId(),
                String.valueOf(currentSensor.getType().getIdentifier()),
                chart, layoutProgress, layoutChart);
        hro.execute();
    }
}
