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
		ChatController chatController = ChatController.getInstance();
		Model model = chatController.getModel();
		new ChatGUI(model,(args.length!=0 ? args[0] : null));
	}
}
