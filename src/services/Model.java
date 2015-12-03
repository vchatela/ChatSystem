package services;

import java.io.File;
import java.net.InetAddress;
import java.util.Vector;

/**
 * Created by ValentinC on 21/10/2015.
 *
 */
public class Model {

	//Classes
	public static class User{
		private String nickname;
		private InetAddress addr;
		private boolean connected;

		public boolean isConnected() {
			return connected;
		}
		public void setConnected(boolean connected) {
			this.connected = connected;
		}
		public String getNickname() {
			return nickname;
		}
		public InetAddress getAddr() {
			return addr;
		}
		public User(String nickname, InetAddress addr)
		{
			this.nickname = nickname;
			this.addr = addr;
			connected = true;
		}
		
		public String toString() {
			return nickname +  " @" + addr;
		}
		
		@Override 
		public boolean equals(Object u2){
			return ((this.nickname).equals(((User)u2).getNickname()));
		}
	}

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
			if (getSender().equals(Controller.getInstance().getLocalUser()))
				header = new String( Controller.getInstance().getLocalUser().getNickname()+ ": " + System.lineSeparator());
			else
				header = new String(getSender().getNickname() + ": " + System.lineSeparator());
			return header;
		}

	};
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
	public static class FileMsg extends Msg{
		public static enum TransferType {ToRemoveUser, FromRemoteUser};

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


	//Attributes
	private Vector<User> userList;
	private Vector<Vector<Msg>> conversations;
	private boolean userListNeedUpdate = false;
	private boolean conversationNeedUpdate = false;

	public boolean isUserListNeedUpdate() {
		return userListNeedUpdate;
	}

	public void setUserListNeedUpdate(boolean userListNeedUpdate) {
		this.userListNeedUpdate = userListNeedUpdate;
	}

	public boolean isConversationNeedUpdate() {
		return conversationNeedUpdate;
	}

	public void setConversationNeedUpdate(boolean conversationNeedUpdate) {
		this.conversationNeedUpdate = conversationNeedUpdate;
	}

	//Functions
    public int getUserListSize(){
        return userList.size();
    }
	
    public Model(){
    	userList = new Vector<>();
		conversations = new Vector<>();
    }
    
    public void addUser(User u)
    {
        //TODO : check if it's working
    	if (!userList.contains(u))
		{
			userList.add(u);
			conversations.add(new Vector<Msg>());
			userListNeedUpdate=true;
		}
    }
	public void remoteUserDisconnect(InetAddress addr)
	{
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

	public Vector<User> getUserList()
	{
		return userList;
	}
	public Vector<Vector<Msg>> getConversations()
	{
		return conversations;
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

}
