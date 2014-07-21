package com.sevenflying.greenhouseclient.app.alertstab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sevenflying.greenhouseclient.app.R;


/** Manages the alert list
 * Created by 7flying on 15/07/2014.
 */
public class AlertListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_alert_list);
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

