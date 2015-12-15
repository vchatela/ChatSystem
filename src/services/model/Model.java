package services.model;

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
	 * Class Model
	 */

	//Attributes
	private Vector<User> userList;
	private Vector<User> userListOpenedTab;
	private LinkedList<FileMessage> newFileTransferRequests;
	private User usertabToOpen;
	private boolean userListNeedUpdate = false;
	private boolean fileTransferNeedUpdate = false;
	private boolean needToOpenATab = false;

	//Methods

	public Model(){
		userList = new Vector<>();
		newFileTransferRequests = new LinkedList<>();
		userListOpenedTab = new Vector<>();
	}

	public synchronized Vector<User> getUserList() {
		return userList;
	}

	public synchronized LinkedList<FileMessage> getNewFileTransferRequests() {
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
			if(userList.get(i).getAddr().equals(addr)){
				userList.elementAt(i).setConnected(false);
				userListNeedUpdate = true;
                found = true;
			}
		}
	}

	public synchronized void addFileTransferRequest(FileMessage fileMsg)
	{
		setChanged();
		newFileTransferRequests.add(fileMsg);
	}
}
