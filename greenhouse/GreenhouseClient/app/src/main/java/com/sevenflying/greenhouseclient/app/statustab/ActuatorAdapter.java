package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sevenflying.greenhouseclient.domain.Actuator;

import java.util.List;

/** Adapter for Actuator.
 * Created by 7flying on 11/08/2014.
 */
public class ActuatorAdapter extends ArrayAdapter<Actuator> {

    public ActuatorAdapter(Context context, int resource, List<Actuator> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ActuatorView actuatorView = (ActuatorView) convertView;
        if(actuatorView == null)
            actuatorView = ActuatorView.inflate(parent);
        actuatorView.setActuator(getItem(position));
        return actuatorView;
    }
}
