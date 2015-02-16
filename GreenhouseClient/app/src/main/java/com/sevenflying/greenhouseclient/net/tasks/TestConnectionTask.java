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

/** Tests the connection to the server.
 * Created by flying on 16/02/15.
 */
public class TestConnectionTask extends AsyncTask<Void, Void, Boolean> {

    private Communicator comm;
    private String host;
    private int serverPort;

    public TestConnectionTask(Context context) {
        this.comm = new Communicator(context);
    }

    @Override
    protected void onPreExecute() {
        this.serverPort = comm.getServerPort();
        this.host = comm.getServer();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean ret = true;
        try {
            InetAddress add = InetAddress.getByName(host);
            Socket s = new Socket(add, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(Commands.TEST_CONNECTION);
            oos.flush();
            String response = (String) ois.readObject();
            ret = response.equals(Constants.OK);
            s.close();
            oos.close();
            ois.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }
}
