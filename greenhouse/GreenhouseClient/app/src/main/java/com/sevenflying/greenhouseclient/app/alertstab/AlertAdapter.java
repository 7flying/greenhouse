package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    }

    public View getView(final int post, View convertView, ViewGroup parent) {
        AlertView alertView = (AlertView) convertView;
        if(alertView == null)
            alertView = AlertView.inflate(parent);
        // Add listener for each toggle
       // final ToggleButton button = alertView.getToggle();
        alertView.getToggle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleButton button = (ToggleButton) view;
                Toast.makeText(getContext(), "Click on item " + post,
                        Toast.LENGTH_SHORT).show();
                getItem(post).setActive(button.isChecked());
                new DBManager(getContext()).setEnabled(getItem(post), button.isChecked());
                Log.d(Constants.DEBUGTAG, " $ On Alert Adapter get view, toggle button clicked");
            }
        });

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
