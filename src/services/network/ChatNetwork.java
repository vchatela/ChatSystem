package services.network;

/**
 * Created by ValentinC on 22/10/2015.
 */
public class ChatNetwork {
    private static ChatNetwork instanceChatNetwork = new ChatNetwork();

    private ChatNetwork(){

    }
    public static ChatNetwork getInstance(){
        return instanceChatNetwork;
    }


}
