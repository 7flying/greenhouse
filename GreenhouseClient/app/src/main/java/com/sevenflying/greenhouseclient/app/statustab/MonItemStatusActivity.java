package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.app.sensortab.SensorStatusActivity;
import com.sevenflying.greenhouseclient.app.utils.AsyncResourceDrawable;
import com.sevenflying.greenhouseclient.app.utils.BitmapFileWorkerTask;
import com.sevenflying.greenhouseclient.app.utils.BitmapResourceWorkerTask;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.app.utils.ImageLoader;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Constants;

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
    private static ImageLoader imageLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_mon_item_status);
        if (imageLoader == null)
            imageLoader = new ImageLoader(getApplicationContext());

        imageMonitoring = (ImageView) findViewById(R.id.image_monitoring);
        imageWarning = (ImageView) findViewById(R.id.image_warning);
        moniName = (TextView) findViewById(R.id.tv_moni_name);
        ListView moniAttachedSensors = (ListView) findViewById(R.id.list_attached_sensors);
        sensorList = new ArrayList<Sensor>();
        adapter = new SensorAdapter(this, R.layout.sensor_list_row, sensorList);
        moniAttachedSensors.setAdapter(adapter);
        if (getIntent().hasExtra(Extras.EXTRA_MONI)) {
            extraInput = (MonitoringItem) getIntent().getSerializableExtra(Extras.EXTRA_MONI);
            Log.d(Constants.DEBUGTAG, " $ MonItemStatus extraItem onCreate: "
                    + extraInput.toString());
            if (extraInput.getPhotoPath() != null) {
                imageLoader.loadBitmapFile(extraInput.getPhotoPath(), imageMonitoring,
                        R.drawable.ic_leaf_green);
            } else {
                    imageLoader.loadBitmapResource(R.drawable.ic_leaf_green, imageMonitoring);
            }
            moniName.setText(extraInput.getName());
            sensorList.addAll(extraInput.getAttachedSensors());
            adapter.notifyDataSetInvalidated();
        }
        moniAttachedSensors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Display sensor data on new Activity
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Intent intent = new Intent(MonItemStatusActivity.this, SensorStatusActivity.class);
                intent.putExtra(Extras.EXTRA_SENSOR, sensorList.get(index));
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_basic_cancel, menu);
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
