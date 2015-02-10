package com.sevenflying.greenhouseclient.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


/** Requests a Sensor creation on the server using a background task.
 * Created by 7flying on 23/09/2014.
 */
public class SensorCreationTask  extends AsyncTask <String, Void, String> {

    private Communicator comm;
    private String serverIP;
    private int serverPort;

    public SensorCreationTask(Context context) {
        this.comm = new Communicator(context);
    }

    @Override
    protected void onPreExecute() {
        this.serverPort = comm.getServerPort();
        this.serverIP = comm.getServer();
    }

    @Override
    protected String doInBackground(String... strings) {
        // String name : 0
        // String analogDig : 1
        // String pin: 2
        // String type: 3
        // String refreshRate: 4
        // String isRefreshEnsured: 5
        if(strings.length != 6)
            return null;
        String ret = null;
        try {
            InetAddress add = InetAddress.getByName(serverIP);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.NEW_SENSOR);
            oos.flush();
            Log.d("SENSOR CREATION TASK", "New Sent");
            String send = strings[0] + ":" + strings[1] + strings[2] + ":" + strings[3] + ":" +
                    strings[4] + ":" + strings[5];
            oos.writeObject(send);
            oos.flush();

            String response = (String) ois.readObject();
            if(response.equals(Constants.OK))
                ret = Constants.OK;
            else
                ret = response;
            Log.d("SENSOR CREATION TASK", "Response received");
            s.close();
            oos.close();
            ois.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
