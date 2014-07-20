package com.sevenflying.greenhouseclient.app.alertstab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.AlertManager;
import com.sevenflying.greenhouseclient.domain.AlertType;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorManager;

import java.util.ArrayList;
import java.util.Map;

/** Activity for the creation of Alerts.
 * Created by 7flying on 20/07/2014.
 */
public class AlertCreationActivity extends FragmentActivity {

    private EditText editTextValue;
    private String selectedSensor = null;
    private int selectedAlert = -1;
    private final AlertType [] alertTypes = {   AlertType.GREATER, AlertType.GREATER_EQUAL,
                                                AlertType.EQUAL, AlertType.LESS,
                                                AlertType.LESS_EQUAL
    };
    private Map<String, Sensor> formatedSensorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_creation);
        formatedSensorMap = SensorManager.getInstance(getApplicationContext()).getFormatedSensors();
        // Sensor list spinner
        Spinner sensorListSpinner = (Spinner) findViewById(R.id.sensor_list_spinner);

        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>(formatedSensorMap.keySet()));
        sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorListSpinner.setAdapter(sensorAdapter);

        sensorListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AlertCreationActivity.this.selectedSensor = adapterView.getItemAtPosition(i)
                        .toString();
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

            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        // Edit Text Value
        editTextValue = (EditText) findViewById(R.id.edit_alert_value);

        // TODO MOVE TO ACTION BAR : button create alert
        Button buttonCreate = (Button) findViewById(R.id.button_create_alert);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alert a = new Alert();
                a.setActive(true);
                a.setSensorType(formatedSensorMap.get(selectedSensor).getType());
                a.setSensorName(formatedSensorMap.get(selectedSensor).getName());
                a.setSensorPinId(formatedSensorMap.get(selectedSensor).getPinId());
                a.setCompareValue(Double.parseDouble(editTextValue.getText().toString()));
                a.setAlertType(alertTypes[selectedAlert]);
                AlertManager.getInstance(getApplicationContext()).addAlert(a);
                AlertManager.getInstance(getApplicationContext()).commit();
            }
        });
    }
}
