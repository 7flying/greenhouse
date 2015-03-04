package com.sevenflying.greenhouseclient.net.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/** Requests to update the description of a sensor using a background task. PinID and type
 * Created by 7flying on 24/09/2014.
 */
public class SensorModificationTask extends AsyncTask<String, Void, String> {

    private Communicator comm;
    private String serverIP;
    private int serverPort;

    public SensorModificationTask(Context context) {
        this.comm = Communicator.getInstance(context);
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
            return Constants.INCORRECT_NUMBER_OF_PARAMS;
        String ret = null;
        try {
            InetAddress add = InetAddress.getByName(serverIP);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.UPDATE_SENSOR);
            oos.flush();
            String send = strings[0] + ":" + strings[1] + strings[2] + ":" + strings[3] + ":" +
                    strings[4] + ":" + strings[5];
            oos.writeObject(send);
            oos.flush();
            String response = (String) ois.readObject();
            if (response.equals(Constants.OK))
                ret = Constants.OK;
            s.close();
            oos.close();
            ois.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
