package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sevenflying.greenhouseclient.app.R;

/** Activity to link ActuatorListFragment & fragment_actuator_list.xml
 * Created by flying on 26/01/15.
 */
public class ActuatorActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_actuator_list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
