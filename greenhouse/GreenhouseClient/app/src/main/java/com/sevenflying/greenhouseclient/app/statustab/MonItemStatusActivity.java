package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.app.sensortab.SensorStatusActivity;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.ArrayList;
import java.util.List;

/** This activity shows the monitoring item info & edit views.
 * Created by 7flying on 11/08/2014.
 */
public class MonItemStatusActivity extends FragmentActivity {

    private List<Sensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_item_status);
        TextView moniName = (TextView) findViewById(R.id.tv_moni_name);
        ListView moniAttachedSensors = (ListView) findViewById(R.id.list_attached_sensors);
        sensorList = new ArrayList<Sensor>();
        SensorAdapter adapter = new SensorAdapter(this, R.layout.sensor_list_row, sensorList);
        moniAttachedSensors.setAdapter(adapter);

        if(getIntent().hasExtra("moni-item")) {
            MonitoringItem item = (MonitoringItem) getIntent().getSerializableExtra("moni-item");
            moniName.setText(item.getName());
            sensorList.addAll(item.getAttachedSensors());
            adapter.notifyDataSetInvalidated();
        }

        moniAttachedSensors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Display sensor data on new Activity
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Intent intent = new Intent(MonItemStatusActivity.this, SensorStatusActivity.class);
                intent.putExtra("sensor", sensorList.get(index));
                startActivity(intent);
            }
        });
    }
}
