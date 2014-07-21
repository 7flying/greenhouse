package com.sevenflying.greenhouseclient.domain;

import android.content.Context;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/** Manages the sensors of the application
 * Created by 7flying on 20/07/2014.
 */
public class SensorManager {

    private static SensorManager manager = null;
    private static final String FILE_NAME = "greenhouse_sensor_manager";
    private Context context;
    private Map<String, Sensor> mapSensors;

    public static SensorManager getInstance(Context context) {
        if(manager == null)
            manager = new SensorManager(context);
        return  manager;
    }

    private SensorManager(Context context) {
        mapSensors = new HashMap<String, Sensor>();
        this.context = context;
        try{
            loadSensors();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Loads the sensors from the file to the map.
     * @throws Exception    */
    private synchronized void loadSensors() throws Exception {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.openFileInput(FILE_NAME)));
            String line = null;
            while( (line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ":");
                Sensor temp = new Sensor();
                List<String> list = new ArrayList<String>(3);
                while(tokenizer.hasMoreTokens()) {
                    list.add(new String(Base64.decode(tokenizer.nextToken().getBytes(),
                            Base64.DEFAULT)));
                }
                if(list.size() == 3) {
                    temp.setPinId(list.get(0));
                    temp.setName(list.get(1));
                    temp.setType(list.get(2).charAt(0));
                    addSensor(temp);
                } else
                    throw new Exception("Sensor couldn't be read");
            }
        }catch (FileNotFoundException e) {
           // No sensors loaded
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Adds a Sensor to the manager. Discarded if repeated.
     * @param s - sensor to add    */
    public synchronized void addSensor(Sensor s) {
        if(!mapSensors.containsKey(s.getPinId() + s.getType().getIdentifier()))
            mapSensors.put(s.getPinId() + s.getType(), s);
    }

    /** Returns all the sensors at the manager.
     * @return list of sensors  */
    public synchronized List<Sensor> getSensors() {
        return  new ArrayList<Sensor>(mapSensors.values());
    }

    /** Returns all the sensors at the manager as formatted strings.
     * @return  map holding a sensor with its formatted representation  */
    public synchronized Map<String, Sensor> getFormattedSensors() {
        Map<String, Sensor> ret = new HashMap<String, Sensor>();
        for(Sensor s : getSensors())
            ret.put(s.getName() + " (" + s.getPinId() + ") " + s.getType().toString(), s);
        return ret;
    }

    /** Saves changes. */
    public synchronized void commit() {
        try{
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            String write = "";
            for(Sensor s : getSensors()){
                write += s.toStoreString() + "\n";
            }
            fos.write(write.getBytes());
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e) {
            // Will create file
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
