package com.sevenflying.greenhouseclient.net;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;

import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorManager;

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
public class SensorsValueUpdater extends AsyncTask<Void, Sensor, List<Sensor>> { // Params, progress, result
    // AsyncTask<Void, Sensor, List<Sensor>> -> means Params, Progress and Result
    private SensorAdapter adapter;
    private List<Sensor> buffer;
    private LinearLayout layoutCharge, layoutNoConnection;
    private Exception exception;
    private Context context;

    public SensorsValueUpdater(SensorAdapter adapter, LinearLayout layoutCharge,
           LinearLayout layoutNoConnection, Context context, List<Sensor> buffer)
    {
        this.adapter = adapter;
        this.layoutCharge = layoutCharge;
        this.layoutNoConnection = layoutNoConnection;
        exception = null;
        this.buffer = buffer;
        this.context = context;
    }

    protected void onPreExecute() {
        layoutNoConnection.setVisibility(View.GONE);
        layoutCharge.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Sensor> doInBackground(Void... voids) {

        List<Sensor> ret = new ArrayList<Sensor>();
        try {
            InetAddress add = InetAddress.getByName(Constants.serverIP);
            Socket s = new Socket(add, Constants.serverPort);
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
                        temp.add(new String(Base64.decode(tokenizer.nextToken().getBytes(), Base64.DEFAULT)));
                    if(temp.size() == 5) {
                        sensor.setName(temp.get(0));
                        sensor.setPinId(temp.get(1));
                        sensor.setType(temp.get(2).charAt(0));
                        sensor.setRefreshRate(Long.parseLong(temp.get(3)));
                        sensor.setValue(Double.parseDouble(temp.get(4)));
                        sensor.setUpdatedAt(new SimpleDateFormat("dd/MM HH:mm:ss").format(new GregorianCalendar().getTime()));
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
        if(exception != null)
            layoutNoConnection.setVisibility(View.VISIBLE);
        //AlertManager alertManager = AlertManager.getInstance(context);
        SensorManager sensorManager = SensorManager.getInstance(context);
        for(Sensor s : result) {
            if(!buffer.contains(s)) {
                buffer.add(s);
                adapter.notifyDataSetChanged();
                sensorManager.addSensor(s);
                //alertManager.checkAlertsFrom(s.getPinId(), s.getType(), s.getValue()); // TODO don't like it here
            }
        }
        layoutCharge.setVisibility(View.GONE);
        sensorManager.commit();
    }

}
