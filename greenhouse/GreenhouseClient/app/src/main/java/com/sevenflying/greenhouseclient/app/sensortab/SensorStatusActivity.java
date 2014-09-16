package com.sevenflying.greenhouseclient.app.sensortab;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.HistoricalRecordObtainer;


/** Activity to show further info about a sensor.
 * Created by 7flying on 13/07/2014.
 */
public class SensorStatusActivity extends FragmentActivity {

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ActionBar tempBar = getActionBar();
        if( tempBar != null) {
           tempBar.setDisplayHomeAsUpEnabled(true);
           tempBar.setDisplayShowTitleEnabled(true);
        }

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
        LinearLayout layoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
        LinearLayout layoutChart = (LinearLayout) findViewById(R.id.layout_chart);

        LineChart chart = (LineChart) findViewById(R.id.chart);
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
            HistoricalRecordObtainer hro = new HistoricalRecordObtainer(s.getPinId(),
                    String.valueOf(s.getType().getIdentifier()), chart, layoutProgress, layoutChart);
            hro.execute();
        }

        ColorTemplate ct = new ColorTemplate();
        ct.addDataSetColors(new int[] {
                R.color.joyful_2
        }, this);
        chart.setColorTemplate(ct);

        // if enabled, the chart will always start at zero on the y-axis
        chart.setStartAtZero(false);
        // disable the drawing of values into the chart
        chart.setDrawYValues(true);
        chart.setDrawXLabels(true);
        chart.setLineWidth(2f);
        chart.setCircleSize(4f);
        chart.setDrawBorder(false);
        chart.setBorderStyles(new BarLineChartBase.BorderStyle[] { BarLineChartBase.BorderStyle.BOTTOM });

        // no description text
        chart.setDescription("");
        chart.setYLabelCount(6);

        // enable value highlighting
        chart.setHighlightEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(false);

        // enable scaling and dragging
        chart.setDragEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
    }

}
