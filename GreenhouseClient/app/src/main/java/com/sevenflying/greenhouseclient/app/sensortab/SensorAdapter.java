package com.sevenflying.greenhouseclient.app.sensortab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.List;


/** Adapter used at the SensorsListActivity
 * Created by 7flying on 28/06/2014.
 */
public class SensorAdapter extends ArrayAdapter<Sensor> {

    public SensorAdapter(Context context, int resource, List<Sensor> list) {
        super(context, resource, list);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SensorView sensorView = (SensorView) convertView;
        if(sensorView == null)
            sensorView = SensorView.inflate(parent);
        sensorView.setSensor(getItem(position));
        return  sensorView;
    }
}
