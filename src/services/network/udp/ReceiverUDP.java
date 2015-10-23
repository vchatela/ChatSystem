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
    
    private boolean running;
    
    public ReceiverUDP() throws SocketException
    {
    	datagramSocket = new DatagramSocket();
    	datagramSocket.setBroadcast(true);
    	inData = new byte[DATA_MAX_LENGTH];
    	running = true;
    }

    public void run(){
    	while (running)
    	{
        	try {
    			datagramSocket.receive(new DatagramPacket(inData, DATA_MAX_LENGTH));
    			ByteArrayInputStream baos = new ByteArrayInputStream(inData);
    		    ObjectInputStream oos = new ObjectInputStream(baos);
    			try {
					ChatNetwork.getInstance().processUDPPacket((Message)oos.readObject());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
}
