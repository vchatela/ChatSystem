import services.*;
import services.GUI.ChatGUI;
import services.network.ChatNetwork;

import java.io.IOException;
import java.net.InetAddress;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChatGUI gui = new ChatGUI(Controller.getInstance().getModel(),(args.length!=0 ? args[0] : null));
	}

}
