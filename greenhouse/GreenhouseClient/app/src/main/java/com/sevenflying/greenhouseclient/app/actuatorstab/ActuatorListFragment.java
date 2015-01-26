package com.sevenflying.greenhouseclient.app.actuatorstab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.Updateable;
import com.sevenflying.greenhouseclient.domain.Actuator;

import java.util.ArrayList;
import java.util.List;

/** Shows the actuator list.
 * Created by flying on 26/01/15.
 */
public class ActuatorListFragment extends Fragment implements Updateable {

    private List<Actuator> actuators;
    private ActuatorAdapter actuatorAdapter;
    private ListView actuatorList;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null)
            return null;
        else {
            View view = inflater.inflate(R.layout.fragment_actuator_list, container, false);
            actuatorList = (ListView) view.findViewById(R.id.list_actuators);
            actuators = new ArrayList<Actuator>();
            for(int i = 0 ; i < 3; i++){
                Actuator a = new Actuator("Water pump " + i, "D0" + i);
                actuators.add(a);
            }
            actuatorAdapter = new ActuatorAdapter(getActivity(),
                    R.layout.actuator_row, actuators);
            actuatorList.setAdapter(actuatorAdapter);
            return view;
        }
    }

    @Override
    public void update() {
        // actuatorList = manager.getActuators();
        actuatorAdapter = new ActuatorAdapter(getActivity(), R.layout.actuator_row, actuators);
        actuatorList.setAdapter(actuatorAdapter);
        actuatorAdapter.notifyDataSetChanged();
    }
}
