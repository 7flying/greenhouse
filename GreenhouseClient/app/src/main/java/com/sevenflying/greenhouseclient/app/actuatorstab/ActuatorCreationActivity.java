package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.domain.AlertType;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;

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
    private int selectedType = -1;
    private boolean []validated = { false, false, false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_actuator_creation);
        formattedSensorMap = new DBManager(getApplicationContext()).getFormattedSensors();
        GreenhouseUtils utils = new GreenhouseUtils(getBaseContext());
        final LinearLayout layoutOpSensor = (LinearLayout) findViewById(
                R.id.layout_optional_control_sensor);
        if (formattedSensorMap.keySet().size() > 0) {
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
        } else {
            // Disable option of control sensor
            LinearLayout lay = (LinearLayout) findViewById(R.id.layout_radio_control_sensor);
            lay.setVisibility(View.GONE);
            // layout where the sensor is chosen
            layoutOpSensor.setVisibility(View.GONE);
        }

        final EditText etName = (EditText) findViewById(R.id.et_name);
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
        final EditText etPin = (EditText) findViewById(R.id.et_pin_number);
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
        final RadioButton radioAnalog = (RadioButton) findViewById(R.id.radio_analog);
        RadioButton radioDigital = (RadioButton) findViewById(R.id.radio_digital);

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
        final EditText etControlValue = (EditText) findViewById(R.id.et_control_value);
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
        List<String> formatedSensorNames = new ArrayList<String>(formattedSensorMap.keySet());
        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, formatedSensorNames);
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
        final Spinner controlTypeSpinner = (Spinner) findViewById(R.id.sensort_type_chooser);
        ArrayAdapter<CharSequence> controlSpinnerAdapter = ArrayAdapter.createFromResource(this,
            R.array.alert_type_array, android.R.layout.simple_spinner_item);
        controlSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlTypeSpinner.setAdapter(controlSpinnerAdapter);
        controlTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ActuatorCreationActivity.this.selectedType = position;
            }
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        // Button create
        createButton = (Button) findViewById(R.id.button_create_actuator);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Communicator comm = Communicator.getInstance(getBaseContext());
                if (comm.testConnection()) {
                    String response = null;
                    String pin = (radioAnalog.isChecked() ? "A" : "D") + etPin.getText().toString();
                    String name = etName.getText().toString().trim();
                    Actuator temp = new Actuator(name, pin);
                    if (formattedSensorMap.keySet().size() > 0 && radioYes.isChecked()) {
                        Log.d(Constants.DEBUGTAG, "$ Actuator Edit/Modify with control fields");
                        Sensor control = formattedSensorMap.get(
                                (String) controlSensorSpinner.getSelectedItem());
                        AlertType controlType = AlertType.alertTypes[selectedType];
                        double compareValue = Double.parseDouble(etControlValue.getText().toString());
                        temp.setControlSensor(control);
                        temp.setCompareType(AlertType.valueOf(controlType.toString()));
                        temp.setCompareValue(compareValue);
                        // Creation if pin enabled
                        if (etPin.isEnabled()) {
                            response = comm.createActuator(name, pin,
                                    control.getType().toString().toUpperCase(),
                                    control.getPinId(), controlType.toString(), compareValue);
                        } else {
                            // Modification
                            response = comm.modifyActuator(name, pin,
                                    control.getType().toString().toUpperCase(),
                                    control.getPinId(), controlType.toString(), compareValue);
                        }
                    } else {
                        // simple fields
                        Log.d(Constants.DEBUGTAG, "$ Actuator Edit/Modify simple fields");
                        // Creation
                        if (etPin.isEnabled()) {
                            response = comm.createActuator(name, pin);
                        } else {
                            // Modification
                            response = comm.modifyActuator(name, pin);
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            ActuatorCreationActivity.this);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }});
                    switch (response) {
                        case Constants.OK:
                            // Send an ok to previous activity
                            Intent returnIntent = new Intent();
                            if (!etPin.isEnabled())
                                returnIntent.putExtra(Extras.EXTRA_ACTUATOR, temp);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                            break;
                        case Constants.INCORRECT_NUMBER_OF_PARAMS:
                            builder.setMessage(ActuatorCreationActivity.this.getResources()
                                    .getString(R.string.error_incorrect_params));
                            builder.show();
                            break;
                        case Constants.INTERNAL_SERVER_ERROR:
                            if (etPin.isEnabled())
                                builder.setMessage(ActuatorCreationActivity.this.getResources()
                                        .getString(R.string.actuator_error_create));
                            else
                                builder.setMessage(ActuatorCreationActivity.this.getResources()
                                        .getString(R.string.actuator_error_modify));
                            break;
                    }
                } else
                    comm.showNoConnectionDialog(ActuatorCreationActivity.this);
            }
        });
        if (getIntent().hasExtra(Extras.EXTRA_ACTUATOR_EDIT)) {
            // Edit actuator
            getSupportActionBar().setTitle(getResources().getString(R.string.title_edit_actuator));
            Actuator extraActuator = (Actuator) getIntent()
                    .getSerializableExtra(Extras.EXTRA_ACTUATOR_EDIT);
            etName.setText(extraActuator.getName());
            radioAnalog.setChecked(extraActuator.getPinId().charAt(0) == 'A');
            radioAnalog.setEnabled(radioAnalog.isChecked());
            radioDigital.setChecked(!radioAnalog.isChecked());
            radioDigital.setEnabled(radioDigital.isChecked());
            etPin.setText(extraActuator.getPinId().substring(1));
            etPin.setEnabled(false);
            radioYes.setChecked(extraActuator.hasControlSensor());
            radioNo.setChecked(!radioYes.isChecked());
            if (extraActuator.hasControlSensor()) {
                etControlValue.setText(Double.toString(extraActuator.getCompareValue()));
                controlTypeSpinner.setSelection(extraActuator.getCompareType().getIndex());
                String sensorFormat = new GreenhouseUtils(getApplicationContext())
                        .getFormattedSensor(extraActuator.getControlSensor());
                Log.d(Constants.DEBUGTAG, " $ ActuatorCreation  sensorFormat: " + sensorFormat);
                boolean found = false;
                int index = 0;
                while (!found && index < formatedSensorNames.size()) {
                   Log.d(Constants.DEBUGTAG, sensorFormat + " - " + formatedSensorNames.get(index));
                   if (sensorFormat.equals(formatedSensorNames.get(index)))
                        found = true;
                    else index++;
                }
                if (found)
                    controlSensorSpinner.setSelection(index);
            }
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

    private void checkSaveButton() {
        if (radioYes.isChecked() && formattedSensorMap.keySet().size() > 0)
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
