import services.*;
import services.GUI.ChatGUI;

/**
 * Created by ValentinC on 21/10/2015.
 *
 */

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ChatGUI(Controller.getInstance().getModel(),(args.length!=0 ? args[0] : null));
	}

}
