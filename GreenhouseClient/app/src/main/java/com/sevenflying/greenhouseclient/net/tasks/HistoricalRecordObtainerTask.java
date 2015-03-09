package com.sevenflying.greenhouseclient.net.tasks;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/** Obtains the historical records of a sensor from the server.
 * Created by 7flying on 04/08/2014.
 */
public class HistoricalRecordObtainerTask extends AsyncTask<Void, Void, List<Map<String, Float>>> {

    // Sensor's pin id
    private String pinId;
    // Sensor's type
    private String senType;
    // Chart to set the values onto
    private LineChart chart;
    private LinearLayout layoutProgress, layoutChart;
    private Communicator comm;
    private String serverIP;
    private int serverPort;
    private DBManager manager;

    public HistoricalRecordObtainerTask(String pinId, String senType, LineChart chart,
    LinearLayout layoutProgress, LinearLayout layoutChart, Context context)
    {
        this.pinId = pinId;
        this.senType = senType;
        this.chart = chart;
        this.layoutProgress = layoutProgress;
        this.layoutChart = layoutChart;
        this.comm = Communicator.getInstance(context);
        this.manager = new DBManager(context);
    }

    @Override
    protected void onPreExecute() {
        this.serverIP = comm.getServer();
        this.serverPort = comm.getServerPort();
        layoutChart.setVisibility(View.GONE);
        layoutProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Map<String, Float>> doInBackground(Void... voids) {
        List<Map<String, Float>> ret = new ArrayList<Map<String, Float>>();
        try {
            InetAddress add = InetAddress.getByName(serverIP);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.HISTORY);
            oos.flush();
            oos.writeObject(pinId + ":" + senType);
            oos.flush();
            // Read how many pairs of: timedate-value we have to expect
            int numPairs = Integer.parseInt((String) ois.readObject());
            while (numPairs > 0) {
                String data = (String) ois.readObject();
                if (data != null) {
                    StringTokenizer tokenizer = new StringTokenizer(data, ":");
                    String[] temp = new String[2];
                    int tempIndex = 0;
                    while (tokenizer.hasMoreTokens()) {
                        temp[tempIndex] = new String(Base64.decode(tokenizer.nextToken().getBytes(),
                                Base64.DEFAULT));
                        tempIndex++;
                    }
                    if (tempIndex == 2) {
                        Map<String, Float> map = new HashMap<String, Float>();
                        map.put(temp[0], Float.valueOf(temp[1]));
                        ret.add(map);
                        oos.writeObject("ACK");
                        oos.flush();
                        numPairs--;
                    } else {
                        oos.writeObject("NACK");
                        oos.flush();
                    }
                }
            }
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onPostExecute(List<Map<String, Float>> stringFloatMapList) {
        manager.cacheData(stringFloatMapList, pinId, senType);
    }
}
