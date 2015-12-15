package services;

import services.model.*;
import services.network.ChatNetwork;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by ValentinC on 21/10/2015.
 *
 */
public class ChatController {
	
	private Model model;
    private User localUser;
    private boolean connected = false;

    private static ChatController instance = new ChatController();

    public int getNbrUser(){
        return model.getUserListSize();
    }

    private ChatController(){
    	model = new Model();
    }
    
    public void Connect(String nickname) throws IOException
    {
        //On vérifie que l'on ne soit pas déjà connecté
        if (connected)
            return;

		localUser = new User((nickname + "@" +InetAddress.getLocalHost().getHostName()), InetAddress.getLocalHost());
        System.out.println("[UDP] - Hello connexion");
        ChatNetwork.getInstance().sendHello(localUser.getNickname());
        connected = true;
    }
    
    public static ChatController getInstance()
    {
    	return instance;
    }

    public Model getModel() {
        return instance.model;
    }

    public void sendText(String text, User receiver){
        //Sending the message
        System.out.println("[UDP] - Sending \"" + (text + "\" to user : " + receiver.getNickname()));
        ChatNetwork.getInstance().sendString(text, receiver.getAddr());

        //Updating the model
        receiver.addMessage(new TextMessage(text, localUser));
    }

    public void sendFile(File f, User receiver) {

        int key = ChatNetwork.getInstance().sendFile(f, receiver.getAddr());

        //Updating the model
        FileMessage fileMsg = new FileMessage(localUser, key, FileMessage.TransferType.ToRemoteUser);
        receiver.addMessage(fileMsg);

        model.setFileTransferNeedUpdate(true);
        model.addFileTransferRequest(fileMsg);
        model.notifyObservers();
    }

    public void processPermissionForFileTransfer(int key, InetAddress addr) {
        //Updating the model
        FileMessage fileMsg = new FileMessage(localUser, key, FileMessage.TransferType.FromRemoteUser);

        User receiver = model.findUser(addr);
        receiver.addMessage(fileMsg);

        model.setFileTransferNeedUpdate(true);
        model.addFileTransferRequest(fileMsg);
        model.notifyObservers();
    }
    
    public void processMessage(Message m, InetAddress addr){
        //On vérifie que l'on soit connecté
        if (!connected)
            return;

        //TODO manage localhost later - if one addr == one of localhost addr
        /*try {
            if (addr.equals(InetAddress.getLocalHost()))
                    return;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/


        switch (m.getHeader())
    	{
    		case hello:
                //if(!getLocalUser().getNickname().equals(m.getData())){
    			    model.addUser(new User(m.getData(), addr));
                    System.out.println("[UDP] - Hello received from : " + m.getData());
                //}
                try {
					ChatNetwork.getInstance().sendHelloAck(localUser.getNickname(), addr);
				} catch (IOException e) {
                    System.out.println("[UDP] - ERROR : While sending Hello ack to " + localUser.getNickname());
                    e.printStackTrace();
				}
                model.notifyObservers();
    			break;
    			
    		case helloAck:
                if(!getLocalUser().getNickname().equals(m.getData())){
    			    model.addUser(new User(m.getData(), addr));
                    System.out.println("[UDP] - HelloAck received from : " + m.getData());
                }
                model.notifyObservers();
    			break;
    			
    		case message:
    			System.out.println("[UDP] - New message : "+m.getData());
                User user = model.findUser(addr);

                //User not found
                if (user == null)
                {
                    System.out.println("[ChatController] - New message from an unknown user");
                    user = new User("Unknown @" + addr.getHostName(), addr);
                    model.addUser(user);
                    try {
                        ChatNetwork.getInstance().sendHello(localUser.getNickname());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    model.setUserListNeedUpdate(true);
                    model.notifyObservers();
                }

                TextMessage t = new TextMessage(m.getData(), user);
                user.addMessage(t);

                //if the user's conversation tab is not opened do it
                if(model.getUserListOpenedTab().indexOf(user)==-1){
                    model.setNeedToOpenATab(true);
                    model.setUsertabToOpen(user);
                    model.notifyObservers();
                }
    			break;
    			
			case bye:
                if(!getLocalUser().getNickname().equals(m.getData())) {
                    System.out.println("[UDP] - Goodbye received from : " + addr); //same for info : put nickname !
                    model.remoteUserDisconnect(addr);
                    model.notifyObservers();
                }
				break;
    	}
    }


    public User getLocalUser() {
        return localUser;
    }
}
