package services.network.tcp;

import services.network.ChatNetwork;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class ServerTCP extends Thread {

    private ServerSocket serverSocket;

    public ServerTCP() {
        try {
            serverSocket = new ServerSocket(ChatNetwork.TCP_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Socket socketClient = null;
        while(true)
        {
            try {
                socketClient = serverSocket.accept();
                System.out.println("New TCP connexion: "+socketClient.getInetAddress());
                ChatNetwork.getInstance().newTCPConnection(socketClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
