package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.AlertType;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Activity for the creation of Alerts.
 * Created by 7flying on 20/07/2014.
 */
public class AlertCreationActivity extends ActionBarActivity {
    private Button buttonCreate;
    private EditText editTextValue;
    private TextView tvSensorUnit;
    private String selectedSensor = null;
    private int selectedAlert = -1;
    private final AlertType [] alertTypes = {   AlertType.GREATER, AlertType.GREATER_EQUAL,
                                                AlertType.EQUAL, AlertType.LESS,
                                                AlertType.LESS_EQUAL
    };  // Do not change the order
    private Map<String, Sensor> formattedSensorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_alert_creation);
        formattedSensorMap = new DBManager(getApplicationContext()).getFormattedSensors();

        // Text View sensor's unit
        tvSensorUnit = (TextView) findViewById(R.id.next_to_edit_alert_value_shows_unit);

        // Sensor list spinner
        final Spinner sensorListSpinner = (Spinner) findViewById(R.id.sensor_list_spinner);

        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>(formattedSensorMap.keySet()));
        sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorListSpinner.setAdapter(sensorAdapter);

        sensorListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AlertCreationActivity.this.selectedSensor = adapterView.getItemAtPosition(i)
                        .toString();
                tvSensorUnit.setText(formattedSensorMap.get(selectedSensor).getType().getUnit());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        // Alert type spinner
        Spinner alertTypeSpinner = (Spinner) findViewById(R.id.alert_type_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.alert_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertTypeSpinner.setAdapter(spinnerAdapter);

        alertTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    AlertCreationActivity.this.selectedAlert = i; // The resource order doesn't change
            }
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        // Edit Text Value
        editTextValue = (EditText) findViewById(R.id.edit_alert_value);

        // Data validator
        editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                Exception exception = null;
                try {
                    Double.valueOf(editable.toString());
                }catch (Exception e) {
                    exception = e;
                }
                buttonCreate.setEnabled((exception == null));
                if (exception == null)
                    buttonCreate.setTextColor(getResources().getColor(R.color.bright_foreground_inverse_material_dark));
                else
                    buttonCreate.setTextColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
        });
        buttonCreate = (Button) findViewById(R.id.button_create_alert);
        buttonCreate.setEnabled(false); // We have to validate data
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alert a = new Alert();
                a.setOn(true);
                a.setSensorType(formattedSensorMap.get(selectedSensor).getType());
                a.setSensorName(formattedSensorMap.get(selectedSensor).getName());
                a.setSensorPinId(formattedSensorMap.get(selectedSensor).getPinId());
                a.setCompareValue(Double.parseDouble(editTextValue.getText().toString()));
                a.setAlertType(alertTypes[selectedAlert]);
                Log.d(Constants.DEBUGTAG, " $ OK on AlertCreationAct. arg:" + a.toString());
                // Return alert to previous activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("alert", a);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

       if (getIntent().hasExtra("alert-to-edit")) {
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(getResources().getString(R.string.title_edit_alert));
            Alert a = (Alert) getIntent().getSerializableExtra("alert-to-edit");
            // the equals of sensor takes pinId + type
            String key = a.getSensorName() + " (" + a.getSensorPinId() + ") " +
                    a.getSensorType().toString();
            List<String> list = new ArrayList<String>(formattedSensorMap.keySet());
            sensorListSpinner.setSelection(list.indexOf(key));
            sensorListSpinner.setEnabled(false);
            alertTypeSpinner.setSelection(a.getAlertType().getIndex());
            alertTypeSpinner.setEnabled(false);
        } else {
            if(getSupportActionBar() != null)
                getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic_cancel, menu);
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
