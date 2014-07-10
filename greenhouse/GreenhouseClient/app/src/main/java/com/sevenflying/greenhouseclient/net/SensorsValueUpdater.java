package com.sevenflying.greenhouseclient.net;

import android.os.AsyncTask;

import com.sevenflying.greenhouseclient.app.sensortab.SensorAdapter;
import com.sevenflying.greenhouseclient.domain.Sensor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/** Task that requests sensor values.
 * Created by 7flying on 10/07/2014.
 */
public class SensorsValueUpdater extends AsyncTask<Void,Integer, List<Sensor>> {

    private SensorAdapter adapter;
    private final String GETSENSORS = "GETSENSORS"; // TODO
    public SensorsValueUpdater(SensorAdapter adapter) {
        this.adapter = adapter;
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
            oos.writeObject(GETSENSORS);
            oos.flush();

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
                sensor.setRefreshRate(new Long(temp.get(3)).longValue());
                sensor.setValue(new Double(temp.get(4)).doubleValue());
                ret.add(sensor);
            }
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    protected void onPostExecute(List<Sensor> result) {
        ArrayList<Sensor> listToUpdate = new ArrayList<Sensor>();
        for(Sensor s : result)
            listToUpdate.add(s);
        adapter.addAll(listToUpdate);
        adapter.notifyDataSetChanged();
       // super.onPostExecute(result);
    }
}
