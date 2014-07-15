package com.sevenflying.greenhouseclient.app.alertstab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sevenflying.greenhouseclient.domain.Alert;

import java.util.List;

/**
 * Created by 7flying on 15/07/2014.
 */
public class AlertAdapter extends ArrayAdapter<Alert> {

    public AlertAdapter(Context context, int resource, List<Alert> list) {
        super(context, resource, list);
    }

    public View getView(int post, View convertView, ViewGroup parent) {
        AlertView alertView = (AlertView) convertView;
        if(alertView == null) {
            alertView = AlertView.inflate(parent);
        }
        alertView.setAlert(getItem(post));
        return  alertView;
    }
}
