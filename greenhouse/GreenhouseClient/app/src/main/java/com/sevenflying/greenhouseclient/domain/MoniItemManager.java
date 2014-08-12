package com.sevenflying.greenhouseclient.domain;

import android.content.Context;

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

/** Manages the Monitoring Items created by the user
 * Created by 7flying on 12/08/2014.
 */
public class MoniItemManager {

    private static MoniItemManager manager = null;
    private static final String FILE_NAME = "greenhouse_moni_item_manager";
    private Context context;
    private Map<String, MonitoringItem> mapNameItem;

    public static MoniItemManager getInstance(Context context) {
        if(manager == null)
            manager = new MoniItemManager(context);
        return  manager;
    }

    private MoniItemManager(Context context) {
        this.context = context;
        mapNameItem = new HashMap<String, MonitoringItem>();
        loadItems();
    }

    /** Loads the stored monitoring items. */
    private synchronized void loadItems() {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.openFileInput(FILE_NAME)));
            String line = null;
            while( (line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ":");
                List<String> list = new ArrayList<String>();
                while(tokenizer.hasMoreTokens())
                    list.add(tokenizer.nextToken());
                if (list.size() > 0) {
                    MonitoringItem item = new MonitoringItem(list.get(0));
                    item.setWarningEnabled(Boolean.valueOf(list.get(1)));
                    int i = 2;
                    SensorManager sensorManager = SensorManager.getInstance(context);
                    while(i < list.size()) {
                        item.addSensor(sensorManager.getSensorBy(list.get(i), list.get(i + 1)));
                        i += 2;
                    }
                    addItem(item);
                }
            }
        }catch (FileNotFoundException e) {
            // No sensors loaded
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Saves the changes.    */
    public synchronized void commit() {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for(MonitoringItem item : getItems())
                fos.write((item.toStoreString() + "\n").getBytes());
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e) {

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /** Retrieves all the items.
     * @return list with all the monitoring items.      */
    public synchronized List<MonitoringItem> getItems() {
        return  new ArrayList<MonitoringItem>(mapNameItem.values());
    }

    /** Adds a MonitoringItem to the manager. Updated if repeated.
     * @param item - item to add     */
    public void addItem(MonitoringItem item) {
        mapNameItem.put(item.getName(), item);
    }

}
