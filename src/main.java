import services.*;
import services.network.ChatNetwork;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Controller c = Controller.getInstance();
		ChatNetwork.getInstance().sendHello("Lucas :)");
	}

}
