package com.sevenflying.greenhouseclient.net;


import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.domain.Sensor;

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
public class SensorsValueUpdater extends AsyncTask<Void, Sensor, List<Sensor>> {

    private SensorAdapter adapter;
    private List<Sensor> buffer;
    private LinearLayout layoutCharge, layoutNoConnection;
    private Exception exception;

    public SensorsValueUpdater(SensorAdapter adapter, LinearLayout layoutCharge,
           LinearLayout layoutNoConnection)
    {
        this.adapter = adapter;
        this.layoutCharge = layoutCharge;
        this.layoutNoConnection = layoutNoConnection;
        exception = null;
        buffer = new ArrayList<Sensor>();
    }

    protected void onPreExecute() {
        layoutCharge.setVisibility(View.VISIBLE);
        layoutNoConnection.setVisibility(View.GONE);
    }

    @Override
    protected List<Sensor> doInBackground(Void... voids) {
        String serverIP = "192.168.1.12"; // TODO
        int serverPort = 5432; // TODO
        List<Sensor> ret = new ArrayList<Sensor>();
        try {
            InetAddress add = InetAddress.getByName(serverIP);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.GETSENSORS);
            oos.flush();
            int numSensors = Integer.parseInt((String) ois.readObject());
            while(numSensors > 0) {
                String ss = (String) ois.readObject();
                if(ss != null) {
                    Sensor sensor = new Sensor();
                    StringTokenizer tokenizer = new StringTokenizer(ss, ":");
                    ArrayList<String> temp = new ArrayList<String>();
                    while(tokenizer.hasMoreTokens())
                        temp.add(tokenizer.nextToken());
                    sensor.setName(temp.get(0));
                    sensor.setPinId(temp.get(1));
                    sensor.setType(temp.get(2).charAt(0));
                    sensor.setRefreshRate(Long.parseLong(temp.get(3)));
                    sensor.setValue(Double.parseDouble(temp.get(4)));
                    sensor.setUpdatedAt(new SimpleDateFormat("dd/MM HH:mm:ss").format(new GregorianCalendar().getTime()));
                    ret.add(sensor);
                    publishProgress(sensor);
                    oos.writeObject("ACK");
                    oos.flush();
                    numSensors--;
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
        if(exception != null)
            layoutNoConnection.setVisibility(View.VISIBLE);

        for(Sensor s : result) {
            if(!buffer.contains(s)) {
                buffer.add(s);
                adapter.add(s);
                adapter.notifyDataSetChanged();
            }
        }
        layoutCharge.setVisibility(View.GONE);
    }

}
