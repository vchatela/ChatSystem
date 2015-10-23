import services.*;
import services.network.ChatNetwork;

import java.io.IOException;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Controller c = Controller.getInstance();
		try {
			ChatNetwork.getInstance().sendHello("Lucas :)");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
