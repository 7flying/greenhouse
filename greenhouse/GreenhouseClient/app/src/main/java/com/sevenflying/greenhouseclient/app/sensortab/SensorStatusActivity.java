package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.ArrayList;


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
    private LineChart chart;

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
            textSensorValue.setText(GreenhouseUtils.suppressZeros(s.getValue()));
            textSensorUnit.setText( s.getType().getUnit());
            textSensorUpdatedAt.setText(s.getUpdatedAt());
            textSensorName.setText(s.getName());
            textSensorType.setText(s.getType().toString());
            textSensorRefresh.setText( Double.toString(s.getRefreshRate() / 1000d) );
            textSensorPin.setText(s.getPinId());
        }
        chart = (LineChart) findViewById(R.id.chart);
        ColorTemplate ct = new ColorTemplate();
        // ct.addColorsForDataSets(new int[] {
        // R.color.colorful_1
        // }, this);
        ct.addDataSetColors(new int[] {
                R.color.colorful_1
        }, this);


        chart.setColorTemplate(ct);

        // if enabled, the chart will always start at zero on the y-axis
        chart.setStartAtZero(false);

        // disable the drawing of values into the chart
        chart.setDrawYValues(false);

        chart.setLineWidth(1f);
        chart.setCircleSize(4f);

        chart.setDrawBorder(true);
        chart.setBorderStyles(new BarLineChartBase.BorderStyle[] { BarLineChartBase.BorderStyle.BOTTOM });

        // no description text
        chart.setDescription("");
        chart.setYLabelCount(6);

        // enable value highlighting
        chart.setHighlightEnabled(true);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);
        setData(15, 30);
    }
    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        DataSet set1 = new DataSet(yVals, "DataSet 1");

        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        ChartData data = new ChartData(xVals, dataSets);

        // set data
        chart.setData(data);
    }
}
