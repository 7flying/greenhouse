package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Activity to create and modify Actuators
 * Created by flying on 08/02/15.
 */
public class ActuatorCreationActivity extends ActionBarActivity {

    private RadioButton radioYes, radioNo;
    private Map<String, Sensor> formattedSensorMap;
    private Button createButton;
    private boolean []validated = { false, false, false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_actuator_creation);
        formattedSensorMap = new DBManager(getApplicationContext()).getFormattedSensors();
        GreenhouseUtils utils = new GreenhouseUtils(getBaseContext());
        List<String> keys = new ArrayList<String>(formattedSensorMap.keySet());
        for (String key : keys) {
            Sensor temp = formattedSensorMap.remove(key);
            int ind = key.lastIndexOf('-');
            if (ind > 0) {
                String newKey = key.substring(0, ind) + "- " + utils.getI18nSensorType(
                        temp.getType());
                formattedSensorMap.put(newKey, temp);
            }
        }
        EditText etName = (EditText) findViewById(R.id.et_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validated[0] = false;
                if (s.toString() != null && s.length() > 0)
                    validated[0] = true;
                checkSaveButton();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        EditText etPin = (EditText) findViewById(R.id.et_pin_number);
        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validated[1] = false;
                Exception temp = null;
                try {
                    Integer.valueOf(s.toString());
                } catch (Exception e) {
                    temp = e;
                }
                validated[1] = (temp == null);
                checkSaveButton();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        RadioButton radioAnalog = (RadioButton) findViewById(R.id.radio_analog);
        RadioButton radioDigital = (RadioButton) findViewById(R.id.radio_digital);
        final LinearLayout layoutOpSensor = (LinearLayout) findViewById(
                R.id.layout_optional_control_sensor);
        radioYes = (RadioButton) findViewById(R.id.radio_yes);
        radioNo = (RadioButton) findViewById(R.id.radio_no);
        radioYes.setChecked(true);
        radioNo.setChecked(false);
        radioYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutOpSensor.setVisibility(View.VISIBLE);
                } else {
                    layoutOpSensor.setVisibility(View.GONE);
                }
                checkSaveButton();
            }
        });
        EditText etControlValue = (EditText) findViewById(R.id.et_control_value);
        etControlValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validated[2] = false;
                if (radioYes.isChecked()) {
                    validated[2] = (s.toString() != null && s.length() > 0);
                } else {
                    validated[2] = true;
                }
                checkSaveButton();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        final TextView tvControlUnit = (TextView) findViewById(R.id.et_control_unit);
        // Spinner of sensors
        final Spinner controlSensorSpinner = (Spinner) findViewById(R.id.sensor_chooser);
        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>(formattedSensorMap.keySet()));
        sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlSensorSpinner.setAdapter(sensorAdapter);
        controlSensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvControlUnit.setText(formattedSensorMap.get( (String)
                        parent.getItemAtPosition(position)).getType().getUnit());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner of control types
        Spinner controlTypeSpinner = (Spinner) findViewById(R.id.sensort_type_chooser);
        ArrayAdapter<CharSequence> controlSpinnerAdapter = ArrayAdapter.createFromResource(this,
            R.array.alert_type_array, android.R.layout.simple_spinner_item);
        controlSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlTypeSpinner.setAdapter(controlSpinnerAdapter);

        // Button create
        createButton = (Button) findViewById(R.id.button_create_actuator);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void checkSaveButton() {
        if (radioYes.isChecked())
            createButton.setEnabled(validated[0] && validated[1] && validated[2]);
        else
            createButton.setEnabled(validated[0] && validated[1]);
        if (createButton.isEnabled())
            createButton.setTextColor(getResources().getColor(
                    R.color.bright_foreground_inverse_material_dark));
        else
            createButton.setTextColor(getResources().getColor(
                    R.color.switch_thumb_normal_material_dark));
    }
}
