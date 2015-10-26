package services;

import java.net.InetAddress;
import java.util.Vector;

/**
 * Created by ValentinC on 21/10/2015.
 */
public class Model {
	
	public static class User{
		private String nickname;
		private InetAddress addr;
		
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
		}
		
		public String toString()
		{
			return nickname +  " @" + addr;
		}
		
		@Override 
		public boolean equals(Object u2){
			return (this.nickname==((User)u2).nickname);
		}
	}
	
	private Vector<User> userList;

    public int getUserListSize(){
        return userList.size();
    }
	
    public Model(){
    	userList = new Vector<User>();
    }
    
    public void addUser(User u)
    {
        //TODO : check if it's working
    	if (!userList.contains(u))
    			userList.add(u);
    }
	public void removeUser(InetAddress addr)
	{
		int i = 0;
		boolean found = false;
		while(i<userList.size() && !found ){
			if(userList.get(i).addr.equals(addr)){
				userList.remove(i);
                found = true;
			}
		}
	}


}
