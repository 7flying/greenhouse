package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sevenflying.greenhouseclient.app.R;

/**
 * Created by 7flying on 28/06/2014.
 */
public class SensorsListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sensors_list);
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
