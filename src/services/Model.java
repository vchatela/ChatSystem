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
			System.out.println("DEBUG : equals called \n");
			return (this.nickname==((User)u2).nickname);
		}
	}
	
	private Vector<User> userList;
	
    public Model(){
    	userList = new Vector<User>();
    }
    
    public void addUser(User u)
    {
    	//TODO avoid duplicata
    	if (!userList.contains(u))
    			userList.add(u);
    }

}
