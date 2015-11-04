package services;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import services.GUI.ChatGUI;
import services.Model.User;
import services.network.ChatNetwork;

/**
 * Created by ValentinC on 21/10/2015.
 *
 */
public class Controller {
	
	private Model model;
    private User localUser;
    private boolean connected = false;

    private static Controller instance = new Controller();

    public int getNbrUser(){
        return model.getUserListSize();
    }

    private Controller(){
    	model = new Model();
    }
    
    public void Connect(String nickname) throws IOException
    {
        //On vérifie que l'on ne soit pas déjà connecté
        if (connected)
            return;

		localUser = new User((nickname + " @" +InetAddress.getLocalHost().getHostName()), InetAddress.getLocalHost());
        ChatNetwork.getInstance().sendHello(localUser.getNickname());
        connected = true;
    }
    
    public static Controller getInstance()
    {
    	return instance;
    }

    public Model getModel() {
        return instance.model;
    }

    public void sendText(String text, User receiver){
        //Sending the message
        System.out.println("Sending \"" + (text + "\" to user : " + receiver.getNickname()));
        ChatNetwork.getInstance().sendString(text, receiver.getAddr());

        //Updating the model
        int index = model.getUserList().indexOf(receiver);
        Vector<Model.Msg> conversation = model.getConversations().elementAt(index);
        conversation.add (new Model.TextMsg(text, localUser));
        model.setConversationNeedUpdate(true);
    }

    public void sendFile(File f, User receiver) {

        int key = ChatNetwork.getInstance().sendFile(f, receiver.getAddr());

        //Updating the model
        int index = model.getUserList().indexOf(receiver);
        Vector<Model.Msg> conversation = model.getConversations().elementAt(index);
        conversation.add (new Model.FileMsg(f, localUser, key));
        model.setConversationNeedUpdate(true);
    }

    public void processPermissionForFileTransfer(int key, InetAddress addr) {
        //Updating the model
        int index = model.getUserList().indexOf(model.findUser(addr));
        Vector<Model.Msg> conversation = model.getConversations().elementAt(index);
        conversation.add (new Model.FileMsg(null, localUser, key));
        model.setConversationNeedUpdate(true);


    }
    
    public void processMessage(Message m, InetAddress addr){
        //On vérifie que l'on soit connecté
        if (!connected)
            return;

        //TODO manage localhost later - if one addr == one of localhost addr
        // if (addr == InetAddress.getLocalhost()
        //        return;
        //
    	switch (m.getHeader())
    	{
    		case hello:
    			model.addUser(new User(m.getData(), addr));
                try {
					ChatNetwork.getInstance().sendHelloAck(localUser.getNickname(), addr);
                    System.out.println("Hello received from : " + m.getData());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                //TODO open new "Comunica System"
    			break;
    			
    		case helloAck:
    			model.addUser(new User(m.getData(), addr));
                System.out.println("HelloAck received from : " + m.getData());
    			break;
    			
    		case message:
    			System.out.println("New message : "+m.getData());
                Model.User user = model.findUser(addr);

                int index = model.getUserList().indexOf(user);

                Vector<Model.Msg> conversation = model.getConversations().elementAt(index);

                Model.TextMsg t = new Model.TextMsg(m.getData(), user);
                conversation.add(t);
                model.setConversationNeedUpdate(true);
    			break;
    			
			case bye:
                //TODO be carreful : add/get info from a hashmap containing the addr AND the username
                System.out.println("Goodbye received from : " + addr); //same for info : put nickname !
                model.remoteUserDisconnect(addr);
				break;
    			
    	}
    }


    public User getLocalUser() {
        return localUser;
    }
    
}
