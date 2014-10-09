package com.sevenflying.greenhouseclient.app;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.about.AboutActivity;
import com.sevenflying.greenhouseclient.app.alertstab.AlertCreationActivity;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.app.sensortab.SensorCreationActivity;
import com.sevenflying.greenhouseclient.app.settings.SettingsActivity;
import com.sevenflying.greenhouseclient.app.statustab.MoniItemCreationActivity;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.domain.AlarmReceiver;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.net.Constants;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsPaAdapter;
    private ActionBar actionBar;
    private final int[] tabNames = {R.string.tab_status, R.string.tab_sensors, R.string.tab_alerts};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        tabsPaAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPaAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for(int name : tabNames)
            actionBar.addTab(actionBar.newTab().setText(name).setTabListener(this));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {}

            @Override
            public void onPageSelected(int i) {
                actionBar.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        // Alarm manager setup
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0, myIntent,0 );
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000,30000,
                pendingIntent);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // Listener to tab change event
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_add_generic:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.what_to_add))
                        .setItems(R.array.items_to_create_array,
                                new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index) {
                                switch (index) {
                                    case 0: // Alert
                                        if(new DBManager(getApplicationContext()).getSensors()
                                                .size() >0 )
                                        {
                                            startActivityForResult(new Intent(MainActivity.this,
                                                            AlertCreationActivity.class),
                                                    Codes.CODE_CREATE_NEW_ALERT);
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                                   MainActivity.this);
                                            builder.setMessage(MainActivity.this
                                                    .getResources()
                                                    .getString(R.string.alert_creation_no));
                                            builder.
                                                    setPositiveButton(R.string.ok,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialogInterface,
                                                                                    int i) {
                                                                }
                                                            });
                                            builder.show();
                                        }
                                        break;
                                    case 1: // Monitoring item
                                        startActivityForResult(new Intent(MainActivity.this,
                                                MoniItemCreationActivity.class)
                                                , Codes.CODE_NEW_MONI_ITEM);
                                        break;
                                    case 2: // Sensor
                                        startActivityForResult(new Intent(MainActivity.this,
                                                SensorCreationActivity.class), Codes.CODE_NEW_SENSOR);
                                        break;
                                }
                            }
                        });
                builder.show();
                return true;
            case R.id.action_add_item:
                startActivityForResult(new Intent(MainActivity.this,
                        MoniItemCreationActivity.class), Codes.CODE_NEW_MONI_ITEM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==  Codes.CODE_CREATE_NEW_ALERT) {
            // Callback from AlertCreationActivity
            if(resultCode == Activity.RESULT_OK) {
                Log.d(Constants.DEBUGTAG, " OnActivity result: CODE_CREATE_NEW_ALERT OK");
                ActivityResultHandler.handleCreateNewAlert(getApplicationContext(), data, this);
                tabsPaAdapter.update(2);
            }
        } else {
            if(requestCode == Codes.CODE_NEW_MONI_ITEM) {
                // Callback from MoniItemCreationActivity
                if(resultCode == Activity.RESULT_OK) {
                    ActivityResultHandler.handleCreateNewMoniItem(getApplicationContext(), data);
                    tabsPaAdapter.update(0);
                }

            } else {
                if(requestCode == Codes.CODE_NEW_SENSOR) {
                    // Callback from SensorCreationActivity
                    if(resultCode == Activity.RESULT_OK) {
                        ActivityResultHandler.handleCreateNewSensor(getApplicationContext());
                        tabsPaAdapter.update(1);
                    }
                } else {
                    if(requestCode == Codes.CODE_EDIT_ALERT) {
                        // Callback from AlertCreationActivity on Edit mode
                        if(resultCode == Activity.RESULT_OK) {
                            Log.d(Constants.DEBUGTAG, " $ Callback of alert edit on main");
                            ActivityResultHandler.handleEditAlert(MainActivity.this, data);
                            tabsPaAdapter.update(2);
                        }
                    }
                }
            }
        }
    }
}
