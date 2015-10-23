package services.network.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import services.Message;
import services.network.ChatNetwork;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class SenderUDP {
    private DatagramSocket datagramSocket;
    
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
    	datagramSocket.send(new DatagramPacket(buff, buff.length, addr, ChatNetwork.UDP_PORT));
    }
}
