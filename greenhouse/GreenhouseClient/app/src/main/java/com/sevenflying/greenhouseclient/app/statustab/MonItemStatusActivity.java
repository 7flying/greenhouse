package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.app.sensortab.SensorStatusActivity;
import com.sevenflying.greenhouseclient.app.utils.Codes;
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
    private ImageView imageMonitoring;
    private TextView moniName;
    private SensorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_mon_item_status);
        imageMonitoring = (ImageView) findViewById(R.id.image_monitoring);
        moniName = (TextView) findViewById(R.id.tv_moni_name);
        ListView moniAttachedSensors = (ListView) findViewById(R.id.list_attached_sensors);
        sensorList = new ArrayList<Sensor>();
        adapter = new SensorAdapter(this, R.layout.sensor_list_row, sensorList);
        moniAttachedSensors.setAdapter(adapter);
        if(getIntent().hasExtra("moni-item")) {
            extraInput = (MonitoringItem) getIntent().getSerializableExtra("moni-item");
            if(extraInput.getPhotoPath() != null)
                imageMonitoring.setImageBitmap(BitmapFactory.decodeFile(extraInput.getPhotoPath()));
            else
                imageMonitoring.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_leaf_green));
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
                startActivityForResult(intent, Codes.CODE_EDIT_MONI_ITEM);
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
        if(requestCode == Codes.CODE_EDIT_MONI_ITEM) {
            // Callback from MoniItemCreation
            if(resultCode == RESULT_OK) {
                if(data.hasExtra("moni-item-result")) {
                    MonitoringItem itemEdited = (MonitoringItem) data
                            .getSerializableExtra("moni-item-result");
                    sensorList.clear();
                    sensorList.addAll(itemEdited.getAttachedSensors());
                    adapter.notifyDataSetChanged();
                    moniName.setText(itemEdited.getName());
                    MoniItemManager manager = MoniItemManager.getInstance(getApplicationContext());
                    manager.deleteItem(itemEdited.getName());
                    manager.addItem(itemEdited);
                    manager.commit();
                    if(itemEdited.getPhotoPath() != null)
                        imageMonitoring.setImageBitmap(BitmapFactory.
                                decodeFile(itemEdited.getPhotoPath()));
                    else
                        imageMonitoring.setImageDrawable(getResources()
                                .getDrawable(R.drawable.ic_leaf_green));
                    Toast.makeText(getApplicationContext(), R.string.item_edited, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }
}
