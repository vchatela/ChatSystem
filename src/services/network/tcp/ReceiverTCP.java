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
    private static final int BUFFER_SIZE = 4096;
    
    File file;
    private long fileLength=0;
    private long bytesReceived=0;

    private ReceiverTCP(int key) {
        instances.put(key,this);
    }

    public static ReceiverTCP getInstance(int key){
        ReceiverTCP instance = instances.get(key);
        if (instance == null)
            instance = new ReceiverTCP(key);

        return instance;
    }


    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run(){
        try {
			DataInputStream input = new DataInputStream( clientSocket.getInputStream());
			DataOutputStream output =new DataOutputStream( clientSocket.getOutputStream());
			
			
			String s = "/home/jacques/Documents/" + input.readUTF();
			fileLength = input.readLong();
			
	    	file = new File(s);
			file.createNewFile();
			
			//Accept
			output.writeBoolean(true);
			
			FileOutputStream fos = new FileOutputStream(s);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			
			byte[] bytes = new byte[BUFFER_SIZE];
			
			int count;
		    while (bytesReceived < fileLength) {
		    	count = input.read(bytes);
			    System.out.flush();
		        bos.write(bytes, 0, count);
		        bos.flush();
		        
		        bytesReceived += count;
		    }
		    System.out.flush();
			
			input.close();
			output.close();
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	
    	
    	//File transfer is terminated, closing connection
    	try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
