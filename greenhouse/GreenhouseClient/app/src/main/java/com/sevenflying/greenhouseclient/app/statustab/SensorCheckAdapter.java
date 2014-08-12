package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.sevenflying.greenhouseclient.domain.Sensor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adapter used at the StatusActivity
 * Created by 7flying on 12/08/2014.
 */
public class SensorCheckAdapter extends ArrayAdapter<Sensor> {

    private Map<String, Boolean> mapSensorChecked;

    public SensorCheckAdapter(Context context, int resource, List<Sensor> list) {
        super(context, resource, list);
        mapSensorChecked = new HashMap<String, Boolean>();
        for(Sensor s : list) {
            mapSensorChecked.put(s.getPinId() + Character.valueOf(s.getType().getIdentifier()),
                    Boolean.valueOf(false));
        }
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        SensorCheckView sensorView = (SensorCheckView) convertView;
        if(sensorView == null)
            sensorView = SensorCheckView.inflate(parent);
        sensorView.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String key = getItem(position).getPinId() +
                        Character.valueOf(getItem(position).getType().getIdentifier());
                mapSensorChecked.put(key, !mapSensorChecked.get(key));
            }
        });
        sensorView.setSensor(getItem(position));
        return  sensorView;
    }

    public boolean isChecked(Sensor s) {
        String key = s.getPinId() + Character.valueOf(s.getType().getIdentifier());
        return mapSensorChecked.containsKey(key) ? mapSensorChecked.get(key) : false;
    }

}
