package com.sevenflying.greenhouseclient.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.sevenflying.greenhouseclient.app.about.AboutActivity;
import com.sevenflying.greenhouseclient.app.actuatorstab.ActuatorCreationActivity;
import com.sevenflying.greenhouseclient.app.alertstab.AlertCreationActivity;
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.app.sensortab.SensorCreationActivity;
import com.sevenflying.greenhouseclient.app.settings.SettingsActivity;
import com.sevenflying.greenhouseclient.app.statustab.MoniItemCreationActivity;
import com.sevenflying.greenhouseclient.app.utils.Codes;
import com.sevenflying.greenhouseclient.app.utils.GreenhouseUtils;
import com.sevenflying.greenhouseclient.domain.AlarmReceiver;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;


public class MainActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsPaAdapter;
    public static AlarmManager alarmManager;
    public static PendingIntent alarmIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabsPaAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(tabsPaAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.md_amber_700));

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        } else {
            // Alarm manager setup
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,
                    0);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    GreenhouseUtils.THIRTY_SECONDS, GreenhouseUtils.THIRTY_SECONDS,
                    alarmIntent);
            Log.d(Constants.DEBUGTAG, " # MainActivity start AlarmManager");
        }

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
                final Communicator comm = Communicator.getInstance(getApplicationContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.what_to_add))
                        .setItems(R.array.items_to_create_array,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int index) {
                                        switch (index) {
                                            case 0: // Alert
                                                if (new DBManager(getApplicationContext())
                                                        .getSensors().size() >0 )
                                                {
                                                    startActivityForResult(new Intent(MainActivity.this,
                                                                    AlertCreationActivity.class),
                                                            Codes.CODE_CREATE_NEW_ALERT);
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog
                                                            .Builder(MainActivity.this);
                                                    builder.setMessage(MainActivity.this
                                                            .getResources()
                                                            .getString(R.string.alert_creation_no));
                                                    builder.setPositiveButton(R.string.ok,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialogInterface,
                                                                        int i) {}});
                                                    builder.show();
                                                }
                                                break;
                                            case 1: // Monitoring item
                                                startActivityForResult(new Intent(MainActivity.this,
                                                        MoniItemCreationActivity.class),
                                                        Codes.CODE_NEW_MONI_ITEM);
                                                break;
                                            case 2: // Sensor
                                                if (comm.testConnection()) {
                                                    startActivityForResult(new Intent(
                                                                MainActivity.this,
                                                                SensorCreationActivity.class),
                                                            Codes.CODE_NEW_SENSOR);
                                                } else
                                                    comm.showNoConnectionDialog(MainActivity.this);
                                                break;
                                            case 3: // Actuator
                                                if (comm.testConnection()) {
                                                    startActivityForResult(new Intent(
                                                                    MainActivity.this,
                                                                    ActuatorCreationActivity.class),
                                                            Codes.CODE_NEW_ACTUATOR);
                                                } else
                                                    comm.showNoConnectionDialog(MainActivity.this);
                                                break;
                                        }
                                    }
                                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.DEBUGTAG, "$ MainActivity::onActivityResult, requestCode:" + requestCode
                + ", resultCode:" + resultCode);
        switch (requestCode) {
            case Codes.CODE_CREATE_NEW_ALERT:
                // Callback from AlertCreationActivity
                if(resultCode == Activity.RESULT_OK) {
                    Log.d(Constants.DEBUGTAG, " MainActivity::OnActivity result: CODE_CREATE_NEW_ALERT OK");
                    ActivityResultHandler.handleCreateNewAlert(getBaseContext(), data, this);
                    tabsPaAdapter.update(2);
                }
                break;
            case Codes.CODE_EDIT_ALERT:
                // Callback from AlertCreationActivity on Edit mode
                if(resultCode == Activity.RESULT_OK) {
                    ActivityResultHandler.handleEditAlert(getBaseContext(), data);
                    tabsPaAdapter.update(2);
                }
                break;
            case Codes.CODE_NEW_SENSOR:
                // Callback from SensorCreationActivity
                if(resultCode == Activity.RESULT_OK) {
                    ActivityResultHandler.handleCreateNewSensor(getBaseContext());
                    tabsPaAdapter.update(1);
                }
                break;
            case Codes.CODE_EDIT_SENSOR:
                // Callback from SensorCreationActivity
                if (resultCode == Activity.RESULT_OK) {
                    ActivityResultHandler.handleEditSensor(getBaseContext(), data);
                    if (tabsPaAdapter !=  null)
                        tabsPaAdapter.update(1);
                    else
                        Log.e(Constants.DEBUGTAG, "$ MainActivity::onActivityResult "
                                + "EDIT_SENSOR, has NULL tabsPagerAdapter");
                }
                break;
            case Codes.CODE_NEW_MONI_ITEM:
                // Callback from MoniItemCreationActivity
                if (resultCode == Activity.RESULT_OK) {
                    ActivityResultHandler.handleCreateNewMoniItem(getBaseContext(), data);
                    tabsPaAdapter.update(0);
                }
                break;
            case Codes.CODE_EDIT_MONI_ITEM:
                if (resultCode == RESULT_OK) {
                    ActivityResultHandler.handleEditMoniItem(getBaseContext(), data);
                    tabsPaAdapter.update(0);
                }
                break;
            case Codes.CODE_NEW_ACTUATOR:
                if (resultCode == RESULT_OK) {
                    ActivityResultHandler.handleCreateNewActuator(getBaseContext());
                    tabsPaAdapter.update(3);
                }
                break;
            case Codes.CODE_EDIT_ACTUATOR:
                if (resultCode == RESULT_OK) {
                    ActivityResultHandler.handleModifyActuator(getBaseContext(), data);
                    tabsPaAdapter.update(3);
                }
                break;
            default:
                break;
        }
    }
}
