package services.network.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import services.Message;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class SenderUDP {
    private DatagramSocket datagramSocket;
    private static final int UDP_PORT = 9738;
    
    public SenderUDP() throws SocketException
    {
		datagramSocket = new DatagramSocket();
    }
    
    
    public void send (Message mess, InetAddress addr) throws IOException
    {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(mess);
		oos.flush();
		byte[] buff= baos.toByteArray();
    	datagramSocket.send(new DatagramPacket(buff, buff.length, addr, UDP_PORT));
    }
}
