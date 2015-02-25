package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.tasks.ActuatorLaunchTask;

import java.io.Serializable;
import java.util.List;

/** Adapter for Actuator.
 * Created by 7flying on 11/08/2014.
 */
public class ActuatorAdapter extends ArrayAdapter<Actuator> implements Serializable {

    private List<Actuator> actuatorList;

    public ActuatorAdapter(Context context, int resource, List<Actuator> objects) {
        super(context, resource, objects);
        this.actuatorList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ActuatorView actuatorView = (ActuatorView) convertView;
        if(actuatorView == null)
            actuatorView = ActuatorView.inflate(parent);
        actuatorView.getLaunchButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Communicator comm = new Communicator(getContext());
                comm.launchActuator(actuatorList.get(position).getPinId());
            }
        });
        actuatorView.setActuator(getItem(position));
        return actuatorView;
    }
}
