package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


/** Holds the actuator info & edit
 * Created by flying on 08/02/15.
 */
public class ActuatorStatusActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }
}
