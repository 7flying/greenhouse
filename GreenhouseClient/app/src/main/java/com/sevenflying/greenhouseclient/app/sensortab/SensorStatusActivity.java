package com.sevenflying.greenhouseclient.app.sensortab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;
import com.sevenflying.greenhouseclient.net.tasks.GetPowerSavingStatusTask;
import com.sevenflying.greenhouseclient.net.tasks.HistoricalRecordObtainerTask;
import com.sevenflying.greenhouseclient.net.tasks.SetPowerSavingTask;

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
    private TextView textSensorUpdatedAt, textSensorValue, textPowerSavingOnOff;
    private TableRow tableRow;
    private Button buttonChangePowerMode;
    private int modeOn = -1;
    private Communicator communicator;
    private static DBManager manager = null;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (manager == null)
            manager = new DBManager(getApplicationContext());
        setContentView(R.layout.activity_sensor_status);
        communicator = Communicator.getInstance(getBaseContext());
        // Views
        ImageView imageView = (ImageView) findViewById(R.id.image_sensor);
        textSensorValue = (TextView) findViewById(R.id.text_sensor_value);
        TextView textSensorUnit = (TextView) findViewById(R.id.text_sensor_value_unit);
        textSensorUpdatedAt = (TextView) findViewById(R.id.text_sensor_updated_at);
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

        tableRow = (TableRow) findViewById(R.id.row_change_power_save);
        tableRow.setVisibility(View.GONE);
        textPowerSavingOnOff = (TextView) findViewById(R.id.text_power_saving_on_off);
        buttonChangePowerMode = (Button) findViewById(R.id.button_power_save);
        buttonChangePowerMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPowerSavingTask task = new SetPowerSavingTask(getBaseContext());
                String ret = null;
                try{
                    ret = task.execute(currentSensor.getPinId(), String.valueOf(
                            currentSensor.getType().getIdentifier()),
                            modeOn == 1 ? "false" : "true").get();
                } catch (Exception e) {
                    Log.e(Constants.DEBUGTAG, " $ SensorStatusActivity: couldn't change " +
                            "pow-save mode");
                }
                if (ret != null && ret.equals(Constants.OK)) {
                    if (modeOn == 1) {
                        modeOn = 0;
                        Toast.makeText(getBaseContext(), getResources()
                                        .getString(R.string.sensor_power_save_off),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), getResources()
                                    .getString(R.string.sensor_power_save_on),
                            Toast.LENGTH_SHORT).show();
                        modeOn = 1;
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources()
                           .getString(R.string.sensor_power_save_error),
                            Toast.LENGTH_SHORT).show();
                    modeOn = -1;
                }
                updatePowerSavingStatus();
            }
        });
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
            textSensorRefresh.setText(GreenhouseUtils.suppressZeros(currentSensor
                    .getRefreshRate() / 1000d));
            textSensorPin.setText(currentSensor.getPinId());
        }
        // Read the cached values
        getHistoricalData(true);
        getPowerSavingModeStatus();
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
                getHistoricalData(false); // disable cache
                getPowerSavingModeStatus();
                break;
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHistoricalData(boolean cache) {
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        ArrayList<String> xValues = new ArrayList<String>();
        layoutChart.setVisibility(View.GONE);
        layoutProgress.setVisibility(View.VISIBLE);
        boolean connection = communicator.testConnection(),
                hasCache = manager.doesSensorHaveCache(currentSensor);
        if ((!hasCache || !cache) && connection) {
            HistoricalRecordObtainerTask hro = new HistoricalRecordObtainerTask(currentSensor.getPinId(),
                    String.valueOf(currentSensor.getType().getIdentifier()),
                    chart, layoutProgress, layoutChart, getApplicationContext());
            try {
                List<Map<String, Float>> results = hro.execute().get();
                // The data has to be ordered in reverse order, from past to present and it's received
                // the other way around
                int i = results.size() - 1;
                for (Map<String, Float> stringFloatMap : results) {
                    for (String key : stringFloatMap.keySet()) {
                        if (i == results.size() - 1) {
                            // update the latest value on the view
                            textSensorValue.setText(GreenhouseUtils.suppressZeros(
                                    stringFloatMap.get(key)));
                            textSensorUpdatedAt.setText(key);
                        }
                        // Remove date from timedate, just left hour
                        xValues.add(((String) results.get(i).keySet().toArray()[0])
                                .substring(0, ((String)results.get(i).keySet().toArray()[0])
                                        .indexOf('-') -1));
                        yValues.add(new Entry(stringFloatMap.get(key), i));
                        i--;
                    }
                }
            } catch (Exception e) {
                Log.e(Constants.DEBUGTAG, " $ SensorStatusActivity: couldn't retrieve historical data ");
            }
        } else {
            if (hasCache) {
                // Update the graph using the cached data
                List<Map<String, String>> values = manager.getLastCachedValues(currentSensor);
                boolean first = true;
                int i = values.size() - 1;
                for (Map<String, String> tuple : values) {
                    Log.e(Constants.DEBUGTAG, " $ cached tuple: " + tuple.toString());
                    if (first) {
                        first = false;
                        textSensorValue.setText(tuple.get(DBManager.SensorHistory.SH_VALUE));
                        textSensorUpdatedAt.setText(tuple.get(DBManager.SensorHistory.SH_TIME) + " - "
                                + tuple.get(DBManager.SensorHistory.SH_DATE));
                    }
                    xValues.add(tuple.get(DBManager.SensorHistory.SH_TIME));
                    yValues.add(new Entry(Float.parseFloat(tuple.get(DBManager
                            .SensorHistory.SH_VALUE)), i));
                    i--;
                }
            }
        }
        if (yValues.size() > 0 && xValues.size() > 0) {
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

        }
        layoutProgress.setVisibility(View.GONE);
        layoutChart.setVisibility(View.VISIBLE);
    }

    private void getPowerSavingModeStatus() {
        if (communicator.testConnection()) {
            GetPowerSavingStatusTask task = new GetPowerSavingStatusTask(getBaseContext());
            String mode = null;
            try {
                mode = task.execute(currentSensor.getPinId(), String.valueOf(
                        currentSensor.getType().getIdentifier())).get();
            } catch (Exception e) {
                Log.e(Constants.DEBUGTAG, " $ SensorStatusActivity: couldn't retrieve Pow-save mode" +
                        " status");
            }
            if (mode != null) {
                if (mode.toLowerCase().equals("true"))
                    modeOn = 1;
                else if (mode.toLowerCase().equals("false"))
                    modeOn = 0;
            } else {
                modeOn = -1;
                Toast.makeText(getBaseContext(), getResources()
                                .getString(R.string.error_retrieving_power_save_mode),
                        Toast.LENGTH_SHORT).show();
            }
        }
        updatePowerSavingStatus();
    }

    private void updatePowerSavingStatus() {
        if (modeOn == -1) {
            textPowerSavingOnOff.setText(getResources().getString(R.string.defaults_no_data));
            tableRow.setVisibility(View.GONE);
        } else {
            textPowerSavingOnOff.setText(modeOn == 1
                    ? getResources().getString(R.string.defaults_active)
                    : getResources().getString(R.string.defaults_disabled));
            buttonChangePowerMode.setText(modeOn == 1
                    ? getResources().getString(R.string.disable_power_saving)
                    : getResources().getString(R.string.enable_power_saving));
            tableRow.setVisibility(View.VISIBLE);
        }

    }
}
