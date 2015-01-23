package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Alert;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.Serializable;
import java.util.List;

/** Adapter for Alert class.
 * Created by 7flying on 15/07/2014.
 */
public class AlertAdapter extends ArrayAdapter<Alert> implements Serializable {

    private List<Alert> alertList;
    //private boolean[] checkState;

    public AlertAdapter(Context context, int resource, List<Alert> list) {
        super(context, resource, list);
        this.alertList = list;
        //this.checkState = new boolean[list.size()];
    }

    public View getView(final int post, View convertView, ViewGroup parent) {
        AlertView alertView = (AlertView) convertView;
        if(alertView == null)
            alertView = AlertView.inflate(parent);
        // Add listener for each toggle
        alertView.getToggle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.DEBUGTAG, " $[!!] AlertAdapter::onClick Launched: "
                        + getItem(post).getSensorPinId() + ": " + getItem(post).isActive());
                DBManager manager = new DBManager(getContext());
                boolean enabled = manager.isEnabled(getItem(post));
                // Swap value
                enabled = !enabled;
                manager.setEnabled(getItem(post), enabled);
                alertList.get(post).setActive(enabled);
                getItem(post).setActive(enabled);

                /*
                boolean temp = ((ToggleButton) view).isChecked();
                //checkState[post] = temp;
                alertList.get(post).setActive(temp);
                getItem(post).setActive(temp);
                // ToggleButton button = (ToggleButton) view;
                DBManager manager = new DBManager(getContext());
                //boolean previous = manager.isEnabled(getItem(post));
                //getItem(post).setActive(!previous);
                manager.setEnabled(getItem(post), temp);
                */
                Toast.makeText(getContext(), "Click on item " + post +
                                " : it has: " + ((ToggleButton) view).isChecked(),
                        Toast.LENGTH_SHORT).show();
                Log.d(Constants.DEBUGTAG, " $ On Alert Adapter get view, toggle button clicked");
            }
        });
       // alertView.getToggle().setChecked(checkState[post]);
        alertView.setAlert(getItem(post));
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
