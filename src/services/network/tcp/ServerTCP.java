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

    }

    public void run() {
        Socket socketClient = null;
        try {
            socketClient = serverSocket.accept();
            System.out.println("Connexion avec : "+socketClient.getInetAddress());
            ChatNetwork.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
