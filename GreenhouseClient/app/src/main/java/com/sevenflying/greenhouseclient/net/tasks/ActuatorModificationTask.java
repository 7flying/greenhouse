package com.sevenflying.greenhouseclient.net.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sevenflying.greenhouseclient.net.Commands;
import com.sevenflying.greenhouseclient.net.Communicator;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/** Requests an actuator modification using a background task.
 * Created by flying on 10/02/15.
 */
public class ActuatorModificationTask extends AsyncTask<String, Void, String> {

    private Communicator comm;
    private String host;
    private int serverPort;

    public ActuatorModificationTask(Context context) {
        this.comm = new Communicator(context);
    }

    @Override
    protected void onPreExecute() {
        this.serverPort = comm.getServerPort();
        this.host = comm.getServer();
    }
    @Override
    protected String doInBackground(String... params) {
        // 0- name (encoded)
        // 1- id
        // 2- (optional) sensor type
        // 3- (optional) sensor id
        // 4- (optional) compare type
        // 5- (optional) compare value
        String ret = null;
        if (params.length == 2 || params.length == 6) {
            try {
                InetAddress add = InetAddress.getByName(host);
                Socket s = new Socket(add, serverPort);
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                oos.writeObject(Commands.UPDATE_ACTUATOR);
                oos.flush();
                String send = params[0] + ":" + params[1];
                if (params.length == 6)
                    send += params[2] + ":" + params[3] + ":" + params[4] + ":" + params[5];
                oos.writeObject(send);
                oos.flush();
                String response = (String) ois.readObject();
                ret = response.equals(Constants.OK) ? Constants.OK : response;
                s.close();
                oos.close();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
