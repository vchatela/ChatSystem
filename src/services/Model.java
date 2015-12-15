package services;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Vector;

/**
 * Created by ValentinC on 21/10/2015.
 *
 */
public class Model extends Observable{

	//Classes

	/**
	 * Class user
	 * Store information about the user
	 */
	public static class User{
		//User attributes
		private String nickname;
		private InetAddress addr;
		private boolean connected;

		//User methods
		public User(String nickname, InetAddress addr)
		{
			this.nickname = nickname;
			this.addr = addr;
			connected = true;
		}
		public boolean isConnected() {
			return connected;
		}
		public void setConnected(boolean connected) {
			this.connected = connected;
		}
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public InetAddress getAddr() {
			return addr;
		}
		public String toString() {
			return nickname +  " @" + addr;
		}
		@Override 
		public boolean equals(Object u2){
			return ((this.nickname).equals(((User)u2).getNickname()));
		}
	}

	/**
	 * Class Msg
	 * Generic message send by a user
	 * Should be extended to match a real message (text, string, ...)
	 */
	public static abstract class Msg{
		private User sender;
		public Msg(User sender){
			this.sender = sender;
		}
		public User getSender() {
			return sender;
		}
		public String toString(){
			String header;
			if (getSender().equals(ChatController.getInstance().getLocalUser()))
				header = new String( ChatController.getInstance().getLocalUser().getNickname()+ ": " + System.lineSeparator());
			else
				header = new String(getSender().getNickname() + ": " + System.lineSeparator());

			return header;
		}

	};

	/**
	 * Class TextMsg
	 * Used to store information about a text message
	 * Is a Msg
	 */
	public static class TextMsg extends Msg{
		private String text;
		public TextMsg(String text, User sender) {
			super(sender);
			this.text=text;
		}
		public String toString() {
			return (super.toString() + text);
		}
		public String getText() {
			return text;
		}
	}


	/**
	 * Class FileMsg
	 * Used to store information about a FileMsg
	 * Is a Msg
	 */
	public static class FileMsg extends Msg{
		public static enum TransferType {ToRemoteUser, FromRemoteUser};

		private TransferType transferType;
		private int hashcodeTCP;

		public FileMsg(User sender, int hashcodeTCP, TransferType transferType) {
			super(sender);
			this.hashcodeTCP = hashcodeTCP;
			this.transferType = transferType;
		}
		public int getHashcodeTCP() {
			return hashcodeTCP;
		}
		public TransferType getTransferType() {
			return transferType;
		}
	}


	/**
	 * Class Model
	 */

	//Attributes
	private Vector<User> userList;
	private Vector<User> userListOpenedTab;
	private Vector<Vector<Msg>> conversations;
	private LinkedList<FileMsg> newFileTransferRequests;
	private User usertabToOpen;
	private boolean userListNeedUpdate = false;
	private boolean conversationNeedUpdate = false;
	private boolean fileTransferNeedUpdate = false;
	private boolean needToOpenATab = false;

	//Methods

	public Model(){
		userList = new Vector<>();
		conversations = new Vector<>();
		newFileTransferRequests = new LinkedList<>();
		userListOpenedTab = new Vector<>();
	}

	public synchronized Vector<User> getUserList() {
		return userList;
	}
	public synchronized Vector<Vector<Msg>> getConversations() {
		return conversations;
	}
	public synchronized LinkedList<FileMsg> getNewFileTransferRequests() {
		return newFileTransferRequests;
	}

	public synchronized Vector<User> getUserListOpenedTab() {
		return userListOpenedTab;
	}

	public synchronized User getUsertabToOpen() {
		return usertabToOpen;
	}

	public int getUserListSize(){
		return userList.size();
	}

	public boolean isUserListNeedUpdate() {
		return userListNeedUpdate;
	}

	public boolean isNeedToOpenATab() {
		return needToOpenATab;
	}

	public boolean isFileTransferNeedUpdate() {
		return fileTransferNeedUpdate;
	}

	public User findUser(InetAddress addr)
	{
		boolean found = false;
		User u = null;
		int i=0;

		while(i<userList.size() && (!found))
		{
			if (userList.elementAt(i).getAddr().equals(addr))
			{
				u = userList.elementAt(i);
				found = true;
			}
			i++;
		}
		return u;
	}

	public void setFileTransferNeedUpdate(boolean fileTransferNeedUpdate) {
		this.fileTransferNeedUpdate = fileTransferNeedUpdate;
		setChanged();
	}

	public void setNeedToOpenATab(boolean needToOpenATab) {
		setChanged();
		this.needToOpenATab = needToOpenATab;
	}

	public void setUsertabToOpen(User usertabToOpen) {
		this.usertabToOpen = usertabToOpen;
	}

	public void setUserListNeedUpdate(boolean userListNeedUpdate) {
		setChanged();
		this.userListNeedUpdate = userListNeedUpdate;
	}
	public boolean isConversationNeedUpdate() {
		return conversationNeedUpdate;
	}

	public void setConversationNeedUpdate(boolean conversationNeedUpdate) {
		setChanged();
		this.conversationNeedUpdate = conversationNeedUpdate;
	}
    
    public synchronized void addUser(User u)
    {
		setChanged();
		//Is the user already in the list ?
		User bis  = findUser(u.getAddr());
		userListNeedUpdate=true;
		if (bis==null)
		{
			//Creating new user
			userList.add(u);
			conversations.add(new Vector<Msg>());
		}
		else
		{
			//Updating pseudonyme and connected state
			bis.setConnected(true);
			bis.setNickname(u.getNickname());
		}
    }

	public synchronized void remoteUserDisconnect(InetAddress addr)
	{
		setChanged();
		int i = 0;
		boolean found = false;
		while(i<userList.size() && !found ){
			if(userList.get(i).addr.equals(addr)){
				userList.elementAt(i).setConnected(false);
				userListNeedUpdate = true;
                found = true;
			}
		}
	}

	public synchronized void addFileTransferRequest(FileMsg fileMsg)
	{
		setChanged();
		newFileTransferRequests.add(fileMsg);
	}
}
