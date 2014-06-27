package com.sevenflying.greenhouseclient.app.sensortab;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;

import java.util.List;

/**
 * Created by user on 27/06/2014.
 */
public class SensorsArrayAdapter extends ArrayAdapter<Sensor> {

    private Context context;

    public SensorsArrayAdapter(Context context, int textViewResourceId, List<Sensor> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textSensorName = null;
        TextView textSensorValue = null;
        TextView textSensorUnit = null;
        Sensor sensor = getItem(position);

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.sensor_list_row, null);
            imageView = (ImageView) convertView.findViewById(R.id.icon_sensor); // ??
            textSensorName = (TextView) convertView.findViewById(R.id.text_sensor_name);
            textSensorValue = (TextView) convertView.findViewById(R.id.text_sensor_value);
            textSensorUnit = (TextView) convertView.findViewById(R.id.text_sensor_unit);
            convertView.setTag(sensor);
        }

        textSensorName.setText(sensor.getName());
        textSensorValue.setText(sensor.getValue());
        textSensorUnit.setText(sensor.getUnit());
        return convertView;
    }
}
