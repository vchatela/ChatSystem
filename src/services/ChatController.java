package services;

import services.Model.User;
import services.network.ChatNetwork;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

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
        if(!getLocalUser().getNickname().equals(receiver.getNickname())) {
            System.out.println("[UDP] - Sending \"" + (text + "\" to user : " + receiver.getNickname()));
            ChatNetwork.getInstance().sendString(text, receiver.getAddr());
        }

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

        Model.FileMsg fileMsg = new Model.FileMsg(localUser, key, Model.FileMsg.TransferType.ToRemoteUser);
        conversation.add (fileMsg);
        model.setConversationNeedUpdate(true);

        model.setFileTransferNeedUpdate(true);
        model.addFileTransferRequest(fileMsg);
    }

    public void processPermissionForFileTransfer(int key, InetAddress addr) {
        //Updating the model
        int index = model.getUserList().indexOf(model.findUser(addr));
        Vector<Model.Msg> conversation = model.getConversations().elementAt(index);
        Model.FileMsg fileMsg = new Model.FileMsg(localUser, key, Model.FileMsg.TransferType.FromRemoteUser);
        conversation.add (fileMsg);
        model.setConversationNeedUpdate(true);

        model.setFileTransferNeedUpdate(true);
        model.addFileTransferRequest(fileMsg);
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
                if(!getLocalUser().getNickname().equals(m.getData())){
    			    model.addUser(new User(m.getData(), addr));
                    System.out.println("[UDP] - Hello received from : " + m.getData());
                }
                try {
					ChatNetwork.getInstance().sendHelloAck(localUser.getNickname(), addr);
				} catch (IOException e) {
                    System.out.println("[UDP] - ERROR : While sending Hello ack to " + localUser.getNickname());
                    e.printStackTrace();
				}
                // TODO If the user was previously open (tab), allow again to send message to him
    			break;
    			
    		case helloAck:
                if(!getLocalUser().getNickname().equals(m.getData())){
    			    model.addUser(new User(m.getData(), addr));
                    System.out.println("[UDP] - HelloAck received from : " + m.getData());
                }
    			break;
    			
    		case message:
    			System.out.println("[UDP] - New message : "+m.getData());
                Model.User user = model.findUser(addr);

                int index = model.getUserList().indexOf(user);

                Vector<Model.Msg> conversation = model.getConversations().elementAt(index);

                Model.TextMsg t = new Model.TextMsg(m.getData(), user);
                conversation.add(t);
                //TODO : notify by observer
                model.setConversationNeedUpdate(true);
                //if the user's conversation tab is not opened do it
                if(model.getUserListOpenedTab().indexOf(user)==-1){
                    model.setNeedToOpenATab(true);
                    model.setUsertabToOpen(user);
                }
    			break;
    			
			case bye:
                if(!getLocalUser().getNickname().equals(m.getData())) {
                    System.out.println("[UDP] - Goodbye received from : " + addr); //same for info : put nickname !
                    model.remoteUserDisconnect(addr);
                }
                //TODO if user disconnect, we need to block sending message to him
				break;
    	}
    }


    public User getLocalUser() {
        return localUser;
    }
}