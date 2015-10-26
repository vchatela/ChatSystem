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
            Connect("ValentinDeBase");
        } catch (UnknownHostException e) {
            //TODO
            e.printStackTrace();
        }
    }
    
    public void Connect(String nickname) throws UnknownHostException
    {
    	//TODO
		localUser = new User((nickname + " @" +InetAddress.getLocalHost().getHostName()), InetAddress.getLocalHost());
    }
    
    public static Controller getInstance()
    {
    	return instance;
    }
    
    public void processMessage(Message m, InetAddress addr){
    	switch (m.getHeader())
    	{
    		case hello:
    			model.addUser(new User(m.getData(), addr));
                try {
					ChatNetwork.getInstance().sendHelloAck(localUser.getNickname(), localUser.getAddr()); // TODO localUser at NULL
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			break;
    			
    		case helloAck:
    			model.addUser(new User(m.getData(), addr));
    			break;
    			
    		case message:
    			System.out.println(m.getData());
    			break;
    			
			case bye:
				//TODO delete user
				break;
    			
    	}
    	System.out.println(m.getData());
    }
    
}
