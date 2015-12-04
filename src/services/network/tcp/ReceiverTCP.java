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

	public long getBytesReceived() {
		return bytesReceived;
	}

	private long bytesReceived=0;

	public boolean ackGiven() {
		return permissionAck;
	}

	private boolean permissionAck=false;
	private boolean fileAccepted;
	private String filePath;
	private String fileName;

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

	public synchronized void acceptFile(String path) {
		permissionAck = true;
		fileAccepted = true;
		filePath = path;
		notify();
	}

	public synchronized void refuseFile() {
		permissionAck = true;
		fileAccepted = false;
		notify();
	}

	public String getFileName()
	{
		return fileName;
	}

	public long getFileLength()
	{
		return fileLength;
	}

    public void run(){
        try {
			DataInputStream input = new DataInputStream( clientSocket.getInputStream());
			DataOutputStream output =new DataOutputStream( clientSocket.getOutputStream());
			
			
			fileName = input.readUTF();
			fileLength = input.readLong();

			
			//Waiting for accept
			synchronized (this)
			{
				while (permissionAck == false)
				{
					wait();
				}
			}


			if (fileAccepted) {
				output.writeBoolean(true);

				file = new File(filePath + fileName);
				file.createNewFile();

				FileOutputStream fos = new FileOutputStream(file);
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
			}
			else {
				output.writeBoolean(false);
			}

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
