package com.sevenflying.greenhouseclient.net;

import android.os.AsyncTask;
import android.text.Layout;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/** Obtains the historical records of a sensor from the server.
 * Created by 7flying on 04/08/2014.
 */
public class HistoricalRecordObtainer extends AsyncTask<Void, Void, Map<String, Float>> {

    // Sensor's pin id
    private String pinId;
    // Sensor's type
    private String senType;
    // Chart to set the values onto
    private LineChart chart;
    private LinearLayout layoutProgress, layoutChart;

    public HistoricalRecordObtainer(String pinId, String senType, LineChart chart,
           LinearLayout layoutProgress, LinearLayout layoutChart)
    {
        this.pinId = pinId;
        this.senType = senType;
        this.chart = chart;
        this.layoutProgress = layoutProgress;
        this.layoutChart = layoutChart;
    }

    @Override
    protected void onPreExecute() {
        layoutChart.setVisibility(View.GONE);
        layoutProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Map<String, Float> doInBackground(Void... voids) {
        Map<String, Float> ret = new HashMap<String, Float>();
        try {
            InetAddress add = InetAddress.getByName(Constants.serverIP);
            Socket s = new Socket(add, Constants.serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.HISTORY);
            oos.flush();
            oos.writeObject(pinId + ":" + senType);
            oos.flush();
            // Read how many pairs of: timedate-value we have to expect
            int numPairs = Integer.parseInt((String) ois.readObject());
            while(numPairs > 0) {
                String data = (String) ois.readObject();
                if(data != null) {
                    StringTokenizer tokenizer = new StringTokenizer(data, ":");
                    String[] temp = new String[2];
                    int tempIndex = 0;
                    while(tokenizer.hasMoreTokens()) {
                        temp[tempIndex] = new String(Base64.decode(tokenizer.nextToken().getBytes(),
                                Base64.DEFAULT));
                        tempIndex++;
                    }
                    if(tempIndex == 2) {
                        // Remove date from timedate
                        temp[0] = temp[0].substring(0, temp[0].indexOf('-') - 1);
                        ret.put(temp[0], Float.valueOf(temp[1]));
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
    protected void onPostExecute(Map<String, Float> stringFloatMap) {
        ArrayList<Entry> chartData = new ArrayList<Entry>();
        int i = 0;
        for(String key : stringFloatMap.keySet()) {
            chartData.add(new Entry( stringFloatMap.get(key), i));
            i++;
        }

        DataSet set = new DataSet(chartData, "Sensor");
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set);

        ChartData data = new ChartData(new ArrayList<String>(stringFloatMap.keySet()), dataSets);
        chart.setData(data);

        layoutProgress.setVisibility(View.GONE);
        layoutChart.setVisibility(View.VISIBLE);
        chart.requestFocus();
    }
}
