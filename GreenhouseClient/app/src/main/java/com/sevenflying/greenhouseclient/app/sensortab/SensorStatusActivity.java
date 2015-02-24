package com.sevenflying.greenhouseclient.app.sensortab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Constants;
import com.sevenflying.greenhouseclient.net.tasks.HistoricalRecordObtainerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Activity to show further info about a sensor.
 * Created by 7flying on 13/07/2014.
 */
public class SensorStatusActivity extends ActionBarActivity {

    private Sensor currentSensor;
    private LinearLayout layoutProgress;
    private LinearLayout layoutChart;
    private LineChart chart;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        chart = (LineChart) findViewById (R.id.chart);
        if (chart == null)
            Log.e(Constants.DEBUGTAG, " $ SensorStatus chart is null");

        // if enabled, the chart will always start at zero on the y-axis
        chart.setStartAtZero(false);
        // disable the drawing of values into the chart
        chart.setDrawYValues(true);
        chart.setDrawXLabels(true);
        chart.setDrawBorder(false);

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription(getResources().getString(R.string.no_historical_data));


        // enable value highlighting
        chart.setHighlightEnabled(true);

        // enable touch gestures
        chart.setTouchEnabled(false);

        // enable scaling and dragging
        chart.setDragEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        // Set data
        if (getIntent().hasExtra(Extras.EXTRA_SENSOR)) {
            GreenhouseUtils utils = new GreenhouseUtils(this);
            currentSensor = (Sensor) getIntent().getSerializableExtra(Extras.EXTRA_SENSOR);
            imageView.setImageResource(currentSensor.getDrawableId());
            textSensorValue.setText(GreenhouseUtils.suppressZeros(currentSensor.getValue()));
            textSensorUnit.setText(currentSensor.getType().getUnit());
            textSensorUpdatedAt.setText(currentSensor.getUpdatedAt());
            textSensorName.setText(currentSensor.getName());
            textSensorType.setText(utils.getI18nSensorType(currentSensor.getType()));
            textSensorRefresh.setText(Double.toString(currentSensor.getRefreshRate() / 1000d) );
            textSensorPin.setText(currentSensor.getPinId());
            getHistoricalData();
        }
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
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHistoricalData() {
        HistoricalRecordObtainerTask hro = new HistoricalRecordObtainerTask(currentSensor.getPinId(),
                String.valueOf(currentSensor.getType().getIdentifier()),
                chart, layoutProgress, layoutChart, getApplicationContext());
        try {

            List<Map<String, Float>> results = hro.execute().get();
            ArrayList<Entry> yValues = new ArrayList<Entry>();
            ArrayList<String> xValues = new ArrayList<String>();
            // The data has to be ordered in reverse order, from past to present and it's received
            // the other way around
            int i = results.size() - 1;
            for (Map<String, Float> stringFloatMap : results) {
                for (String key : stringFloatMap.keySet()) {
                    xValues.add((String) results.get(i).keySet().toArray()[0]);
                    yValues.add(new Entry(stringFloatMap.get(key), i));
                    i--;
                }
            }
            LineDataSet set = new LineDataSet(yValues, currentSensor.getName());
            set.setColor(Color.rgb(60, 220, 78));
            set.setCircleColor(Color.rgb(60, 220, 78));
            set.setLineWidth(1f);
            set.setCircleSize(5f);
            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set);

            LineData data = new LineData(xValues, dataSets);
            chart.setData(data);
            chart.animateX(1000);

            layoutProgress.setVisibility(View.GONE);
            layoutChart.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(Constants.DEBUGTAG, " $ SensorStatusActivity: couldn't retrieve historical data ");
            e.printStackTrace();
        }
    }
}
