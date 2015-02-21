package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Actuator;

/** Holds the actuator info.
 * Created by flying on 08/02/15.
 */
public class ActuatorStatusActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        setContentView(R.layout.activity_actuator_status);
        TextView tvActuatorName = (TextView) findViewById(R.id.text_actuator_name);
        TextView tvPin = (TextView) findViewById(R.id.text_actuator_pin);

        if (getIntent().hasExtra("actuator")) {
            Actuator temp = (Actuator) getIntent().getSerializableExtra("actuator");
            tvActuatorName.setText(temp.getName());
            tvPin.setText(temp.getPinId());
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
