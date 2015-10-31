package services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import services.Model.User;
import services.network.ChatNetwork;

/**
 * Created by ValentinC on 21/10/2015.
 *
 */
public class Controller {

    /*public static final int USERVIEW_CLI = 0;
    public static final int USERVIEW_GUI = 1;
    public static final int STATE_ACTIVATED = 1;
    public static final int STATE_DESACTIVATED = 0;
    public static final int TCP_PROTOCOL_SERVER = 0;
    public static final int UDP_PROTOCOL_SERVER = 1;

    private String inMessage;
    private String outMessage;
    private int State;*/
	
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
        //On v�rifie que l'on ne soit pas d�j� connect�
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

    public void sendMessage (Model.Msg message) {
        //Sending a text or a file ?
        if (message.getClass()==Model.Text.class) {
            //Text

            //Sending the message
            System.out.println("Sending \"" + ((Model.Text) message).getText() + "\" to user : " + message.getSender().getNickname());
            ChatNetwork.getInstance().sendString(((Model.Text) message).getText(), message.getSender().getAddr());

            //Updating the model
            int index = model.getUserList().indexOf(message.getSender());
            Vector<Model.Msg> conversation = model.getConversations().elementAt(index);

            Model.Text t = new Model.Text(((Model.Text) message).getText(), localUser);
            conversation.add(t);
        }
        else {

        }
    }
    
    public void processMessage(Message m, InetAddress addr){
        //On v�rifie que l'on soit connect�
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
					ChatNetwork.getInstance().sendHelloAck(localUser.getNickname(), localUser.getAddr());
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
                //TODO open new "Comunica System"
    			break;
    			
    		case message:
    			System.out.println("New message : "+m.getData());
                Model.User user = model.findUser(addr);

                int index = model.getUserList().indexOf(user);
                Vector<Model.Msg> conversation = model.getConversations().elementAt(index);

                Model.Text t = new Model.Text(m.getData(), user);
                conversation.add(t);
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
