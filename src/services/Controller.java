package services;
/**
 * Created by ValentinC on 21/10/2015.
 *
 */
public class Controller {

    public static final int USERVIEW_CLI = 0;
    public static final int USERVIEW_GUI = 1;
    public static final int STATE_ACTIVATED = 1;
    public static final int STATE_DESACTIVATED = 0;
    public static final int TCP_PROTOCOL_SERVER = 0;
    public static final int UDP_PROTOCOL_SERVER = 1;

    private String inMessage;
    private String outMessage;
    private int State;

    public Controller(){

    }

    public static void main (String args[]){
        //new CommunicaApp(USERVIEW_GUI, TCP_PROTOCOL_SERVER,12700, "127.0.0.1",0);
        //new CommunicaApp(USERVIEW_GUI, TCP_PROTOCOL_SERVER,12700, "127.0.0.1",0);
        //new CommunicaApp(USERVIEW_GUI, UDP_PROTOCOL_SERVER,12700, "127.0.0.1",0);
        //new CommunicaApp(USERVIEW_GUI, UDP_PROTOCOL_SERVER,12700, "127.0.0.1",0);
    }
}
