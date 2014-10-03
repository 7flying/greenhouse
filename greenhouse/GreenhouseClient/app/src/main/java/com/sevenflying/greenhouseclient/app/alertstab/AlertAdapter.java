package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;

import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Alert;

import java.io.Serializable;
import java.util.List;

/** Adapter for Alert class.
 * Created by 7flying on 15/07/2014.
 */
public class AlertAdapter extends ArrayAdapter<Alert> implements Serializable {

    public AlertAdapter(Context context, int resource, List<Alert> list) {
        super(context, resource, list);
    }

    public View getView(final int post, View convertView, ViewGroup parent) {
        AlertView alertView = (AlertView) convertView;
        if(alertView == null)
            alertView = AlertView.inflate(parent);
        // Add listener for each toggle
        final ToggleButton button = alertView.getToggle();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getItem(post).setActive(button.isChecked());
                new DBManager(getContext()).setEnabled(getItem(post), button.isChecked());
            }
        });

        alertView.setAlert(getItem(post));
        return  alertView;
    }
}
