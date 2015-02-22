package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.domain.BootReceiver;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.Serializable;
import java.util.List;

/** Adapter for Alert class.
 * Created by 7flying on 15/07/2014.
 */
public class AlertAdapter extends ArrayAdapter<Alert> implements Serializable {

    private List<Alert> alertList;

    public AlertAdapter(Context context, int resource, List<Alert> list) {
        super(context, resource, list);
        this.alertList = list;
    }

    public View getView(final int post, View convertView, ViewGroup parent) {
        AlertView alertView = (AlertView) convertView;
        if (alertView == null)
            alertView = AlertView.inflate(parent);
        // Add listener for each toggle
        alertView.getToggle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.DEBUGTAG, " $[!!] AlertAdapter::onClick Launched: "
                        + getItem(post).getSensorPinId() + ": " + getItem(post).isOn());
                DBManager manager = new DBManager(getContext());
                boolean previousManagerValue = getItem(post).isOn();
                manager.setEnabled(getItem(post), !previousManagerValue);
                Log.d(Constants.DEBUGTAG, " $ AlertAdapter::onClick: The view has: enabled?: "
                        + ((ToggleButton) view).isChecked());
                Log.d(Constants.DEBUGTAG, " $ AlertAdapter::onClick: The list has: enabled?: "
                        + alertList.get(post).isOn());
                alertList.get(post).setOn(!previousManagerValue);
                getItem(post).setOn(!previousManagerValue);
                Log.d(Constants.DEBUGTAG, " $ On Alert Adapter get view, toggle button clicked");

                if (alertList.get(post).isOn()) {
                    // Enable the notification when the device boots
                    ComponentName receiver = new ComponentName(getContext(), BootReceiver.class);
                    PackageManager pm = getContext().getPackageManager();

                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                }
            }
        });

        DBManager manager = new DBManager(getContext());
        try {
            Log.d(Constants.DEBUGTAG, " $ alertAdapter::getView, item at post: " + post  + " -> " + getItem(post));
            boolean isOn = manager.isEnabled(getItem(post));
            alertView.setAlert(getItem(post), isOn);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return  alertView;
    }

    @Override
    public int getViewTypeCount() {
        return alertList == null ? 0 : alertList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
