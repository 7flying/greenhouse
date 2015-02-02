package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.app.sensortab.SensorStatusActivity;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.ArrayList;
import java.util.List;

/** This activity shows the monitoring item info & edit views.
 * Created by 7flying on 11/08/2014.
 */
public class MonItemStatusActivity extends ActionBarActivity {

    private List<Sensor> sensorList;
    private MonitoringItem extraInput = null;
    private ImageView imageMonitoring, imageWarning;
    private TextView moniName;
    private SensorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_mon_item_status);
        imageMonitoring = (ImageView) findViewById(R.id.image_monitoring);
        imageWarning = (ImageView) findViewById(R.id.image_warning);
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
                    DBManager manager = new DBManager(getApplicationContext());
                    manager.deleteItem(itemEdited.getName());
                    manager.addItem(itemEdited);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_moni_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(MonItemStatusActivity.this,
                        MoniItemCreationActivity.class);
                intent.putExtra("moni-to-edit", extraInput);
                startActivityForResult(intent, Codes.CODE_EDIT_MONI_ITEM);
                break;
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
