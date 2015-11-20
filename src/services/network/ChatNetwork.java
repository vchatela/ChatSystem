package services.network;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

import services.Controller;
import services.Message;
import services.network.tcp.ReceiverTCP;
import services.network.tcp.SenderTCP;
import services.network.tcp.ServerTCP;
import services.network.udp.ReceiverUDP;
import services.network.udp.SenderUDP;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class ChatNetwork {
	public static final int UDP_PORT = 9738;
    public static final int TCP_SERVER_PORT = 9739;
    private static ChatNetwork instanceChatNetwork = new ChatNetwork();
    private SenderUDP senderUDP;
    private ReceiverUDP receiverUDP;
    private ServerTCP serverTCP;

    private int hashcodeTCP =0;
    
    
    private ChatNetwork(){
    	try {
			senderUDP = new SenderUDP();
			receiverUDP = new ReceiverUDP();
            serverTCP = new ServerTCP();
            serverTCP.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("Error : " + e.toString());
		}

    }

    public int sendFile(File f, InetAddress addr) {
        SenderTCP sender = SenderTCP.getInstance(hashcodeTCP);
        sender.start();
        sender.sendFile(f, addr);

        hashcodeTCP++;
        return hashcodeTCP-1;
    }

    public void newTCPConnection (Socket socket)
    {
        ReceiverTCP receiver = ReceiverTCP.getInstance(hashcodeTCP);
        receiver.start();
        Controller.getInstance().processPermissionForFileTransfer(hashcodeTCP, socket.getInetAddress());

        hashcodeTCP++;
    }


    public static ChatNetwork getInstance(){
        return instanceChatNetwork;
    }
    
    public void sendString(String s, InetAddress addr)
    {
    	Message m = Message.createMessage(s);
    	try {
			senderUDP.send(m, addr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void sendHello(String nickname) throws IOException
    {
    	Message m = Message.createHello(nickname);
		for(InetAddress addr : getBroadList())
		{
			senderUDP.send(m, addr);
		}
    }
    
    public void sendHelloAck(String nickname, InetAddress addr) throws IOException
    {
    	Message m = Message.createHelloAck(nickname);
		senderUDP.send(m, addr);

    }

    public void sendBye() throws IOException
    {
        Message m = Message.createBye();
        for(InetAddress addr : getBroadList())
        {
            senderUDP.send(m, addr);
        }
    }
   
    public void processUDPPacket(DatagramPacket p)
    {
	    try {
	    	ByteArrayInputStream baos = new ByteArrayInputStream(p.getData());
		    ObjectInputStream oos = new ObjectInputStream(baos);
			Controller.getInstance().processMessage((Message)oos.readObject(), p.getAddress());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public ArrayList<InetAddress> getBroadList() throws SocketException{
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();    
        ArrayList<InetAddress> broadcastList = new ArrayList<InetAddress>();
        // récupération de toutes les adresses de broadcast
        while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback())
                continue;    
            for (InterfaceAddress interfaceAddress :networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null)
                    continue;
                broadcastList.add(broadcast);
            }
        }
        return broadcastList;
    }


}
