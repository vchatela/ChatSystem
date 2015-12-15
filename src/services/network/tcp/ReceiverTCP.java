	package services.network.tcp;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class ReceiverTCP extends Thread {
	public enum State {waiting_for_accept, receiving_file, file_transferred, file_refused, error}

	//Atributes
		//Multiton
		private Socket clientSocket;
		private static HashMap<Integer,ReceiverTCP> instances = new HashMap<>();
    private static final int BUFFER_SIZE = 4096;
	private File file;
    private long fileLength=0;
	private volatile long bytesReceived=0;
	private boolean fileAccepted;
	private String filePath;
	private String fileName;
	private State state;

	//Methods

	//Multiton
    public static ReceiverTCP getInstance(int key){
        ReceiverTCP instance = instances.get(key);
        if (instance == null)
            instance = new ReceiverTCP(key);

        return instance;
    }

	private ReceiverTCP(int key) {
		instances.put(key,this);
		state=State.waiting_for_accept;
	}

	//Getters
	public State getFileState() {
		return state;
	}

	public long getBytesReceived() {
		return bytesReceived;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public long getFileLength() {
		return fileLength;
	}

	//Setters
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

	//Other methods
	public synchronized void acceptFile(String path) {
		fileAccepted = true;
		filePath = path;
		state=State.receiving_file;
		notify();
	}

	public synchronized void refuseFile() {
		fileAccepted = false;
		state = State.file_refused;
		notify();
	}

	//Thread
    public void run(){
        try {
			DataInputStream input = new DataInputStream( clientSocket.getInputStream());
			DataOutputStream output =new DataOutputStream( clientSocket.getOutputStream());

			fileName = input.readUTF();
			fileLength = input.readLong();

			//Waiting for accept
			synchronized (this)
			{
				while (state == State.waiting_for_accept)
				{
					wait();
				}
			}


			if (fileAccepted) {
				output.writeBoolean(true);

				file = new File(filePath +File.separator+ fileName);
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
				bos.close();

				state = State.file_transferred;

			}
			else {
				output.writeBoolean(false);
				input.close();
				output.close();
			}

			} catch (Exception e1) {
			System.out.println("[TCP] - Connexion error, file transfer cancelled");
			state = State.error;
			}

    	//File transfer is terminated, closing connection
    	try {
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("[TCP] - Connexion error");
		}
	}
}
