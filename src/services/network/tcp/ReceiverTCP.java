package services.network.tcp;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class ReceiverTCP extends Thread {
    private Socket clientSocket;
    private static HashMap<Integer,ReceiverTCP> instances = new HashMap<>();

    private ReceiverTCP() {
        instances.put(instances.size(),this);
    }

    public static ReceiverTCP getInstance(int which){
        return instances.get(which);
    }


    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run(){
        File directory = new File("/"); // TODO
        OutputStream os = null;
        while(true){
            try {
                os = new BufferedOutputStream(new FileOutputStream(new File(directory,clientSocket.getRemoteSocketAddress() + "-" + clientSocket.getPort())));
                os.flush();
            } catch (FileNotFoundException e) {
                //TODO
                e.printStackTrace();
            } catch (IOException e) {
                //TODO
                e.printStackTrace();
            } finally {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
