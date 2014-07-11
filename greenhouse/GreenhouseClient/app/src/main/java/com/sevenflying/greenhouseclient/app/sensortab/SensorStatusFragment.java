package com.sevenflying.greenhouseclient.app.sensortab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevenflying.greenhouseclient.app.R;

/** This fragment shows further sensor info and allows customization.
 * Created by 7flying on 11/07/2014.
 */
public class SensorStatusFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return  inflater.inflate(R.layout.fragment_sensor_status, container, false);
    }
}
