package services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    private static Controller instance = new Controller();

    private Controller(){
    	model = new Model();
        try {
            //TODO : change GUI name
            Connect("ValentinDeBase");
        } catch (UnknownHostException e) {
            //TODO
            e.printStackTrace();
        }
    }
    
    public void Connect(String nickname) throws UnknownHostException
    {
		localUser = new User((nickname + " @" +InetAddress.getLocalHost().getHostName()), InetAddress.getLocalHost());
    }
    
    public static Controller getInstance()
    {
    	return instance;
    }
    
    public void processMessage(Message m, InetAddress addr){
        //TODO manage localhost later
    	switch (m.getHeader())
    	{
    		case hello:
    			model.addUser(new User(m.getData(), addr));
                try {
					ChatNetwork.getInstance().sendHelloAck(localUser.getNickname(), localUser.getAddr());
                    System.out.println("New user : " + m.getData());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                //TODO open new "Comunica System"
    			break;
    			
    		case helloAck:
    			model.addUser(new User(m.getData(), addr));
                System.out.println("New user : " + m.getData());
                //TODO open new "Comunica System"
    			break;
    			
    		case message:
    			System.out.println("New message : "+m.getData());
                //TODO manage inside Comunica
    			break;
    			
			case bye:
                //TODO be carreful : add/get info from a hashmap containing the addr AND the username
                System.out.println("Goodbye from : " + addr); //same for info : put nickname !
                model.removeUser(addr);
				break;
    			
    	}
    }
    
}
