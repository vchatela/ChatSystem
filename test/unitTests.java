import org.junit.Test;
import services.Controller;
import services.network.ChatNetwork;

import java.io.IOException;


/**
 * Created by ValentinC on 23/10/2015.
 *
 */
public class unitTests {
    private Controller c;

    protected void setUp() {
        c = Controller.getInstance();
    }

    @Test
    public final void sendHello(){
        try {
            ChatNetwork.getInstance().sendHello("Lucas :)");
        } catch (IOException e) {
           assert false;
        }
        assert true;
    }

    @Test
    public final void receiveHello(){
        int i = Controller.getInstance().getNbrUser();
        try {
            ChatNetwork.getInstance().sendHello("Lucas :)");
        } catch (IOException e) {
            //error what TODO
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert i + 1 == Controller.getInstance().getNbrUser(); // TODO : check in local
    }

    @Test
    public final void exchangeHello(){


    }

    @Test
    public final void exchangeMessage() {

    }

    @Test
    public final void addUser(){

    }
}
