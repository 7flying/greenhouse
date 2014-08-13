package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.app.sensortab.SensorStatusActivity;
import com.sevenflying.greenhouseclient.domain.MoniItemManager;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.ArrayList;
import java.util.List;

/** This activity shows the monitoring item info & edit views.
 * Created by 7flying on 11/08/2014.
 */
public class MonItemStatusActivity extends FragmentActivity {

    private List<Sensor> sensorList;
    private MonitoringItem extraInput = null;
    private TextView moniName;
    private SensorAdapter adapter;
    private static final int CODE_EDIT_MONI_ITEM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_item_status);
        moniName = (TextView) findViewById(R.id.tv_moni_name);
        ListView moniAttachedSensors = (ListView) findViewById(R.id.list_attached_sensors);
        sensorList = new ArrayList<Sensor>();
        adapter = new SensorAdapter(this, R.layout.sensor_list_row, sensorList);
        moniAttachedSensors.setAdapter(adapter);
        if(getIntent().hasExtra("moni-item")) {
            extraInput = (MonitoringItem) getIntent().getSerializableExtra("moni-item");
            moniName.setText(extraInput.getName());
            sensorList.addAll(extraInput.getAttachedSensors());
            adapter.notifyDataSetInvalidated();
        }
        ImageButton buttonEdit = (ImageButton) findViewById(R.id.button_edit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MonItemStatusActivity.this,
                        MoniItemCreationActivity.class);
                intent.putExtra("moni-to-edit", extraInput);
                startActivityForResult(intent, CODE_EDIT_MONI_ITEM);
            }
        });

        moniAttachedSensors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Display sensor data on new Activity
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Intent intent = new Intent(MonItemStatusActivity.this, SensorStatusActivity.class);
                intent.putExtra("sensor", sensorList.get(index));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_EDIT_MONI_ITEM) {
            // Callback from MoniItemCreation
            if(resultCode == RESULT_OK) {
                if(data.hasExtra("moni-item")) {
                    MonitoringItem itemEdited = (MonitoringItem) data.getSerializableExtra("moni-item");
                    sensorList = new ArrayList<Sensor>();
                    adapter.notifyDataSetInvalidated();
                    sensorList.addAll(itemEdited.getAttachedSensors());
                    adapter.notifyDataSetChanged();
                    moniName.setText(itemEdited.getName());
                    MoniItemManager manager = MoniItemManager.getInstance(getApplicationContext());
                    manager.deleteItem(itemEdited.getName());
                    manager.addItem(itemEdited);
                    manager.commit();
                    Toast.makeText(getApplicationContext(), R.string.item_edited, Toast.LENGTH_SHORT)
                            .show();
                }

            }
        }
    }
}
