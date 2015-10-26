package services.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

import services.Controller;
import services.Message;
import services.network.udp.ReceiverUDP;
import services.network.udp.SenderUDP;
import sun.net.InetAddressCachePolicy;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class ChatNetwork {
	public static final int UDP_PORT = 9738;
    private static ChatNetwork instanceChatNetwork = new ChatNetwork();
    private SenderUDP senderUDP;
    private ReceiverUDP receiverUDP;
    
    
    private ChatNetwork(){
    	try {
			senderUDP = new SenderUDP();
			receiverUDP = new ReceiverUDP();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("Error : " + e.toString());
		}

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
