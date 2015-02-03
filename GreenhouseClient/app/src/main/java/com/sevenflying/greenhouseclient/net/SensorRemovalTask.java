package com.sevenflying.greenhouseclient.net;

import android.content.Context;
import android.os.AsyncTask;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/** Requests a Sensor removal on the server using a background task.
 * Created by 7flying on 23/09/2014.
 */
public class SensorRemovalTask extends AsyncTask<String, Void, Integer> {

    private Communicator comm;
    private String serverIP;
    private int serverPort;

    public SensorRemovalTask(Context context) {
        this.comm = new Communicator(context);
    }

    @Override
    protected void onPreExecute() {
        this.serverPort = comm.getServerPort();
        this.serverIP = comm.getServer();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        // String pinId: 0
        // String type: 1
        if(strings.length != 2)
            return -1;
        Integer ret = -1;
        try {
            InetAddress add = InetAddress.getByName(serverIP);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.DELETE);
            oos.flush();
            String send = strings[0] + ":" + strings[1];
            oos.writeObject(send);
            oos.flush();
            String response = (String) ois.readObject();
            if(response.equals(Constants.OK))
                ret = 0;
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
