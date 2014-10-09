package com.sevenflying.greenhouseclient.app.about;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sevenflying.greenhouseclient.app.R;

/** Shows info about the app
 * Created by 7flying on 18/09/2014.
 */
public class AboutActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ActionBar temp = getActionBar();
        if (temp != null)
            temp.setDisplayHomeAsUpEnabled(true);
    }
}
