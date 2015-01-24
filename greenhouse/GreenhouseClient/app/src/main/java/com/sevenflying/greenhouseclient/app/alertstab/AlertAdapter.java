package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Context;
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
        //this.checkState = new boolean[list.size()];
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
                /*Log.d(Constants.DEBUGTAG, " $ AlertAdapter::onClick: The manager says: enabled?: "
                        + previousManagerValue); */
                Log.d(Constants.DEBUGTAG, " $ AlertAdapter::onClick: The view has: enabled?: "
                        + ((ToggleButton) view).isChecked());
                Log.d(Constants.DEBUGTAG, " $ AlertAdapter::onClick: The list has: enabled?: "
                        + alertList.get(post).isOn());
                alertList.get(post).setOn(!previousManagerValue);
                getItem(post).setOn(!previousManagerValue);

                /*
                boolean temp = ((ToggleButton) view).isChecked();
                //checkState[post] = temp;
                alertList.get(post).setOn(temp);
                getItem(post).setOn(temp);
                // ToggleButton button = (ToggleButton) view;
                DBManager manager = new DBManager(getContext());
                //boolean previous = manager.isEnabled(getItem(post));
                //getItem(post).setOn(!previous);
                manager.setEnabled(getItem(post), temp);
                */
                Toast.makeText(getContext(), "Click on item " + post +
                                " : it has: " + ((ToggleButton) view).isChecked(),
                        Toast.LENGTH_SHORT).show();
                Log.d(Constants.DEBUGTAG, " $ On Alert Adapter get view, toggle button clicked");
            }
        });
       // alertView.getToggle().setChecked(checkState[post]);
        DBManager manager = new DBManager(getContext());
        alertView.setAlert(getItem(post), getItem(post).isOn());
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
