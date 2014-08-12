package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorManager;

import java.util.ArrayList;
import java.util.List;

/** This activity is in charge of the creation of MonitoringItems.
 * Created by 7flying on 11/08/2014.
 */
public class MoniItemCreationActivity extends FragmentActivity {

    private EditText etName;
    private ImageView imagePreview;
    private Button buttonCreate;
    private List<Sensor> sensorList;
    private SensorCheckAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_item_creation);
        buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setEnabled(false);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonitoringItem monitoringItem = new MonitoringItem(etName.getText().toString());
               /*  Drawables are not serializable
                    monitoringItem.setIcon(imagePreview.getDrawable() == null ?
                        getResources().getDrawable(R.drawable.ic_leaf_green) :
                        imagePreview.getDrawable());
                */
                // TODO set icon from image
                monitoringItem.setIcon(R.drawable.ic_leaf_green);
                for(int i = 0; i < adapter.getCount(); i++) {
                    if(adapter.isChecked(adapter.getItem(i))) {
                        monitoringItem.addSensor(adapter.getItem(i));
                    }
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("moni-item", monitoringItem);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        etName = (EditText) findViewById(R.id.et_moni_name);
        etName.addTextChangedListener(new TextWatcher() {
            // Check data
            public void afterTextChanged(Editable editable) {
                  buttonCreate.setEnabled(etName.getText().length() > 0);
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
        });
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        ListView listViewSensos = (ListView) findViewById(R.id.list_check_sensors);
        sensorList =  SensorManager.getInstance(getApplicationContext())
                .getSensors();
        adapter = new SensorCheckAdapter(getApplicationContext(), R.layout.sensor_check_row,
                sensorList);
        listViewSensos.setAdapter(adapter);
    }
}
