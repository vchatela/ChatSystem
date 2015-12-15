import org.junit.Test;
import services.ChatController;
import services.network.ChatNetwork;

import java.io.IOException;


/**
 * Created by ValentinC on 23/10/2015.
 *
 */
public class unitTests {
    private ChatController c;

    protected void setUp() {
        c = ChatController.getInstance();
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
        int i = ChatController.getInstance().getNbrUser();
        try {
            ChatNetwork.getInstance().sendHello("Lucas :)");
        } catch (IOException e) {
            assert false;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert i == ChatController.getInstance().getNbrUser();
    }
}
