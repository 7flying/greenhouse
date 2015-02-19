package com.sevenflying.greenhouseclient.net.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;

import com.sevenflying.greenhouseclient.app.actuatorstab.ActuatorAdapter;
import com.sevenflying.greenhouseclient.app.database.DBManager;
import com.sevenflying.greenhouseclient.domain.Actuator;
import com.sevenflying.greenhouseclient.domain.AlertType;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Communicator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/** Task to obtain the actuators from the server.
 * Created by flying on 19/02/15.
 */
public class ActuatorObtainerTask extends AsyncTask<Void, Actuator, List<Actuator>> {

    private ActuatorAdapter adapter;
    private List<Actuator> currentActuators, dbActuators;
    private LinearLayout layoutCharge, layoutNoConnection;
    private Exception exception;
    private Context context;
    private DBManager manager;
    private Communicator comm;
    private String host;
    private int serverPort;

    public ActuatorObtainerTask(ActuatorAdapter adapter, LinearLayout layoutCharge,
    LinearLayout layoutNoConnection, Context context, List<Actuator> currentActuators)
    {
        this.adapter = adapter;
        this.layoutCharge = layoutCharge;
        this.layoutNoConnection = layoutNoConnection;
        this.exception = null;
        this.currentActuators = currentActuators;
        this.context = context;
        this.comm = new Communicator(context);
    }

    protected void onPreExecute() {
        serverPort = comm.getServerPort();
        host = comm.getServer();
        layoutNoConnection.setVisibility(View.GONE);
        layoutCharge.setVisibility(View.VISIBLE);
        manager= new DBManager(context);
        dbActuators = manager.getAllActuators();
    }

    @Override
    protected List<Actuator> doInBackground(Void... params) {
        List<Actuator> ret = new ArrayList<Actuator>();
        try {
            InetAddress add = InetAddress.getByName(host);
            Socket s = new Socket(add, serverPort);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.GETACTUATORS);
            oos.flush();
            int numActuators = Integer.parseInt((String) ois.readObject());
            while (numActuators > 0) {
                String rawActuator = (String) ois.readObject();
                if (rawActuator != null) {
                    Actuator actuator = new Actuator();
                    StringTokenizer tokenizer = new StringTokenizer(rawActuator, ":");
                    ArrayList<String> temp = new ArrayList<String>();
                    while (tokenizer.hasMoreTokens())
                        temp.add(new String(Base64.decode(tokenizer.nextToken().getBytes(),
                                Base64.DEFAULT)));
                    if (temp.size() == 2 || temp.size() == 6) {
                        actuator.setName(temp.get(0));
                        actuator.setPinId(temp.get(1));
                        if (temp.size() == 6) {
                            Sensor sensor = new Sensor();
                            sensor.setType(temp.get(2).charAt(0));
                            sensor.setPinId(temp.get(3));
                            actuator.setControlSensor(sensor);
                            actuator.setCompareType(AlertType.valueOf(temp.get(4).toUpperCase()));
                            actuator.setCompareValue(Double.parseDouble(temp.get(5)));
                        }
                        ret.add(actuator);
                        oos.writeObject("ACK");
                        oos.flush();
                        numActuators--;
                    } else {
                        oos.writeObject("NACK");
                        oos.flush();
                    }
                } else {
                    oos.writeObject("NACK");
                    oos.flush();
                }
            }
            String errorOrOK = (String) ois.readObject(); // TODO handle as alert?
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
            exception = e;
        }
        return ret;
    }

    @Override
    protected void onPostExecute(List<Actuator> result) {
        for (Actuator a : result) {
            if (dbActuators.contains(a))
                manager.updateActuator(a);
            else
                manager.addActuator(a);
        }
        currentActuators.clear();
        currentActuators.addAll(manager.getAllActuators());
        adapter.notifyDataSetChanged();
        layoutCharge.setVisibility(View.GONE);
    }
}
