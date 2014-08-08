package com.sevenflying.greenhouseclient.app.statustab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sevenflying.greenhouseclient.app.R;

/**
 * Created by 7flying on 06/08/2014.
 */
public class StatusActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_status);
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
