package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Sensor;

/** This fragment shows further sensor info and allows customization.
 * Created by 7flying on 11/07/2014.
 */
public class SensorStatusFragment extends Fragment {

    private TextView tvSensorData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_sensor_status, container, false);
        tvSensorData = (TextView) view.findViewById(R.id.show_data_here);
        Bundle b = this.getArguments();
        Sensor sensor =  (Sensor) b.getSerializable("sensor");
        tvSensorData.setText(sensor.toString());
        return  view;
    }
}
