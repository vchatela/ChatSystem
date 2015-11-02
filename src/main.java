import services.*;
import services.network.ChatNetwork;

import java.io.IOException;
import java.net.InetAddress;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Controller c = Controller.getInstance();
		try {
			ChatNetwork.getInstance().sendHello("Lucas");
			ChatNetwork.getInstance().sendString("Moi c'est Valentin, ca va bien ?", InetAddress.getLocalHost());
            ChatNetwork.getInstance().sendBye();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
