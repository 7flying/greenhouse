package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sevenflying.greenhouseclient.domain.MonitoringItem;

import java.io.Serializable;
import java.util.List;

/** Adapter for MonitoringItem class.
 * Created by 7flying on 10/08/2014.
 */
public class MonitoringItemAdapter extends ArrayAdapter<MonitoringItem> implements Serializable {

    public MonitoringItemAdapter(Context context, int resource, List<MonitoringItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonitoringItemView monitoringItemView = (MonitoringItemView) convertView;
        if(monitoringItemView == null)
            monitoringItemView = MonitoringItemView.inflate(parent);
        monitoringItemView.setMonitoringItem(getItem(position));
        return monitoringItemView;
    }

}
