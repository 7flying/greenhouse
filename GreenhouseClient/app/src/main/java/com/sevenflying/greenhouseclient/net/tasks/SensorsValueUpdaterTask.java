package com.sevenflying.greenhouseclient.net.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

/** Task that requests sensor values.
 * Created by 7flying on 10/07/2014.
 */
public class SensorsValueUpdaterTask extends AsyncTask<Void, Sensor, List<Sensor>> { // Params, progress, result
    // AsyncTask<Void, Sensor, List<Sensor>> -> means Params, Progress and Result
    private SensorAdapter adapter;
    private List<Sensor> buffer, dbSensors;
    private LinearLayout layoutCharge, layoutNoConnection;
    private Exception exception;
    private Context context;
    private DBManager manager;
    private Communicator comm;
    private String host;
    private int serverPort;

    public SensorsValueUpdaterTask(SensorAdapter adapter, LinearLayout layoutCharge,
    LinearLayout layoutNoConnection, Context context, List<Sensor> buffer)
    {
        this.adapter = adapter;
        this.layoutCharge = layoutCharge;
        this.layoutNoConnection = layoutNoConnection;
        this.exception = null;
        this.buffer = buffer;
        this.context = context;
        this.comm = Communicator.getInstance(context);
    }

    protected void onPreExecute() {
        serverPort = comm.getServerPort();
        host = comm.getServer();
        layoutNoConnection.setVisibility(View.GONE);
        layoutCharge.setVisibility(View.VISIBLE);
        manager= new DBManager(context);
        dbSensors = manager.getSensors();
    }

    @Override
    protected List<Sensor> doInBackground(Void... voids) {

        List<Sensor> ret = new ArrayList<Sensor>();
        try {
            InetAddress add = InetAddress.getByName(host);
            Socket s = new Socket(add, serverPort);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.GETSENSORS);
            oos.flush();
            int numSensors = Integer.parseInt((String) ois.readObject());
            while (numSensors > 0) {
                String ss = (String) ois.readObject();
                if(ss != null) {
                    Sensor sensor = new Sensor();
                    StringTokenizer tokenizer = new StringTokenizer(ss, ":");
                    ArrayList<String> temp = new ArrayList<String>();
                    while (tokenizer.hasMoreTokens())
                        temp.add(new String(Base64.decode(tokenizer.nextToken().getBytes(),
                                Base64.DEFAULT)));
                    if (temp.size() == 5) {
                        sensor.setName(temp.get(0));
                        sensor.setPinId(temp.get(1));
                        sensor.setType(temp.get(2).charAt(0));
                        sensor.setRefreshRate(Long.parseLong(temp.get(3)));
                        sensor.setValue(Double.parseDouble(temp.get(4)));
                        sensor.setUpdatedAt(new SimpleDateFormat("dd/MM HH:mm:ss").format(
                                new GregorianCalendar().getTime()));
                        ret.add(sensor);
                        oos.writeObject("ACK");
                        oos.flush();
                        numSensors--;
                    } else {
                        oos.writeObject("NACK");
                        oos.flush();
                    }
                } else {
                    oos.writeObject("NACK");
                    oos.flush();
                }
            }
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
            exception = e;
        }
        return ret;
    }

    protected void onPostExecute(List<Sensor> result) {
        if (exception != null)
            layoutNoConnection.setVisibility(View.VISIBLE);

        for (Sensor s : result) {
            Log.d(Constants.DEBUGTAG, " $ SensorsValueUpdater sensor:-> " + s.toString());
            if (!dbSensors.contains(s))
                manager.addSensor(s);
            else
                manager.editSensor(s);
            manager.updateSensor(s, s.getValue(), s.getUpdatedAt());

        }
        buffer.clear();
        buffer.addAll(manager.getSensors());
        adapter.notifyDataSetChanged();
        layoutCharge.setVisibility(View.GONE);
    }

}
