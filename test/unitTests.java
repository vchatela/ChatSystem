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
