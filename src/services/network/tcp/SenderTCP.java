package services.network.tcp;

import services.network.ChatNetwork;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class SenderTCP extends Thread{
    //Static attributes
    public static enum State {waiting_for_file, waiting_for_accept, sending_file, file_transferred, file_refused, error};
    private static final int BUFFER_SIZE = 4096;
    private static HashMap<Integer,SenderTCP> instances = new HashMap<>();


    //Attributes
    private Socket clientSocket;
    private long fileLength=0;
    private long bytesSent=0;
    private State state = State.waiting_for_file;
    DataInputStream input;
    DataOutputStream output;
    File file;

    private SenderTCP(int key) {
        System.out.println("New FileSenderTCP, hashcode: " + key);
        instances.put(key,this);
    }

    public static SenderTCP getInstance(int key){
        SenderTCP instance = instances.get(key);
        if (instance == null)
            instance = new SenderTCP(key);

        return instance;
    }

    public void sendFile(File f, InetAddress addr){
        if (state != State.waiting_for_file)
            System.out.println("SenderTCP : File already given");

        else
        {
            file = f;
            fileLength = f.length();
            try {
                System.out.println("Asking permission for sending file: " + f.getName() + ", length="+ fileLength + "bytes");
                clientSocket = new Socket(addr, ChatNetwork.TCP_SERVER_PORT);

                input = new DataInputStream( clientSocket.getInputStream());
                output =new DataOutputStream( clientSocket.getOutputStream());
                output.writeUTF(f.getName());
                output.writeLong(fileLength);

                state=State.waiting_for_accept;
            } catch (IOException e) {
                e.printStackTrace();
                state=State.error;
            }
        }

    }

    public void run(){
        waitFile();
        waitAccept();
        if (state==State.sending_file)
            send();
        close();
    }

    private void waitFile()
    {
        while (state == State.waiting_for_file)
        {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                state = State.error;
            }
        }
    }

    private void waitAccept()
    {
        System.out.println("SenderTCP : Waiting for the remote user to accept the file");
        try {
            if (input.readBoolean()) {
                System.out.println("SenderTCP : File accepted");
                state=State.sending_file;
            }
            else {
                System.out.println("SenderTCP : File refused");
                state=State.file_refused;
            }
        } catch (IOException e) {
            e.printStackTrace();
            state = State.error;
        }
    }

    private void send()
    {
        byte [] byteArray = new byte[4096];
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            int bytesToSend = (int) Math.min(((long)4096), (fileLength - bytesSent));
            while ((bytesToSend)>0)
            {
                System.out.println("SenderTCP : Sending bytes " + bytesSent + " to " + (bytesSent + bytesToSend-1));
                System.out.flush();
                bis.read(byteArray, 0, bytesToSend);
                output.write(byteArray, 0, bytesToSend);
                output.flush();
                bytesSent+=bytesToSend;
                bytesToSend = (int) Math.min(((long)4096), (fileLength - bytesSent));
            }

            state=State.file_transferred;
            System.out.println("SenderTCP : file sent");
            
            bis.close();
            fis.close();

        }
        catch (IOException e) {
        e.printStackTrace();
        state = State.error;
    }


    }

    private void close()
    {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getFileLength() {
        return fileLength;
    }

    public long getBytesSent() {
        return bytesSent;
    }
}
