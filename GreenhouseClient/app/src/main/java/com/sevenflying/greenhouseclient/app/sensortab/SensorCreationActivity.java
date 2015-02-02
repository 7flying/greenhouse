package com.sevenflying.greenhouseclient.app.sensortab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorType;
import com.sevenflying.greenhouseclient.net.Communicator;

/** This activity is used to create sensors.
 * Created by 7flying on 19/09/2014.
 */
public class SensorCreationActivity extends ActionBarActivity {

    private EditText etName, etPin, etRefreshRate;
    private RadioButton radioAnalog, radioDigital, radioYes, radioNo;
    private Button buttonCreate;
    private boolean [] validated = { false, false, false };
    private int spinnerSelectedType = 0;
    private SensorType [] sensorTypeArray = { SensorType.HUMIDITY, SensorType.LIGHT,
                                              SensorType.TEMPERATURE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_sensor_creation);

        // Name
        etName = (EditText) findViewById(R.id.et_sensor_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
               validated[0] = false;
               if(editable.toString() != null)
                   if(editable.length() > 0)
                       validated[0] = true;
               setButton(buttonCreate);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3){}

        });
        // Analog/digital
        radioAnalog = (RadioButton) findViewById(R.id.radio_analog);
        radioDigital = (RadioButton) findViewById(R.id.radio_digital);
        // Pin number
        etPin = (EditText) findViewById(R.id.et_pin_number);
        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                validated[1] = false;
                Exception temp = null;
                try {
                    Integer.valueOf(editable.toString());
                }catch (Exception e){
                    temp = e;
                }
                validated[1] = (temp == null);
                setButton(buttonCreate);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3){}
        });
        // Type
        Spinner sensorTypeSpinner = (Spinner) findViewById(R.id.sensor_type_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sensor_type, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorTypeSpinner.setAdapter(spinnerAdapter);
        sensorTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                SensorCreationActivity.this.spinnerSelectedType = index;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        // Refresh rate
        etRefreshRate = (EditText) findViewById(R.id.et_refresh_rate);
        etRefreshRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                validated[2] = false;
                Exception temp = null;
                try {
                    Double.valueOf(editable.toString());
                }catch (Exception e){
                    temp = e;
                }
                validated[2] = (temp == null);
                setButton(buttonCreate);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3){}
        });
        // Ensure refresh
        radioYes = (RadioButton) findViewById(R.id.radio_yes);
        radioNo = (RadioButton) findViewById(R.id.radio_no);
        // Button ok
        buttonCreate = (Button) findViewById(R.id.button_create_sensor);
        setButton(buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String analogDig = radioAnalog.isChecked() ? "A" : "D";
                String type = sensorTypeArray[spinnerSelectedType].toString();
                boolean ensureRefresh = radioYes.isChecked();
                int result = 0;
                if (getIntent().hasExtra("sensor-to-edit")) {
                    // Handle edit sensor
                } else {
                    try {
                        result = Communicator.createSensor(
                                etName.getText().toString(),
                                analogDig,
                                etPin.getText().toString(),
                                type,
                                etRefreshRate.getText().toString(),
                                ensureRefresh
                        );
                    }catch (Exception e) {
                        e.printStackTrace();
                        result = -1;
                    }
                    if(result == 0) {
                        Sensor temp = new Sensor();
                        temp.setName(etName.getText().toString());
                        temp.setPinId(analogDig + etPin.getText().toString());
                        temp.setType(SensorType.valueOf(type.toUpperCase()));
                        temp.setRefreshRate(Long.valueOf(etRefreshRate.getText().toString()));

                        // Return sensor to previous activity
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("sensor", temp);
                        setResult(RESULT_OK, returnIntent);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.sensor_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });
        if(getIntent().hasExtra("sensor-to-edit"))
            getSupportActionBar().setTitle(getResources().getString(R.string.title_sensor_edition));

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

    private boolean validated() {
        return  validated[0] && validated[1] && validated[2];
    }

    private void setButton(Button b) {
        if (validated()){
            buttonCreate.setEnabled(true);
            buttonCreate.setTextColor(getResources().getColor(
                    R.color.bright_foreground_inverse_material_dark));
        } else {
            buttonCreate.setEnabled(false);
            buttonCreate.setTextColor(getResources().getColor(
                    R.color.switch_thumb_normal_material_dark));
        }
    }
}
