package services.network.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import services.Message;
import services.network.ChatNetwork;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class ReceiverUDP extends Thread {
	private static final int DATA_MAX_LENGTH = 1500*8;
	
    private DatagramSocket datagramSocket;
    private byte[] inData;
    
    public ReceiverUDP() throws SocketException
    {
    	datagramSocket = new DatagramSocket(ChatNetwork.UDP_PORT);
    	datagramSocket.setBroadcast(true);
    	inData = new byte[DATA_MAX_LENGTH];
    	this.start();
    }

    public void run(){
    	while (true)
    	{
        	try {
        		DatagramPacket p = new DatagramPacket(inData, DATA_MAX_LENGTH);
    			datagramSocket.receive(p);
    			ChatNetwork.getInstance().processUDPPacket(p);
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
}
