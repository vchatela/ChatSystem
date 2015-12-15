package services.model;


import java.net.InetAddress;
import java.util.Observable;
import java.util.Vector;

/**
 * Class user
 * Store information about the user
 */
public class User extends Observable {
    //User attributes
    private String nickname;
    private InetAddress addr;
    private boolean connected;
    private boolean newMessagesB;
    private Vector<Messages> conversation;

    //User methods
    public User(String nickname, InetAddress addr)
    {
        this.nickname = nickname;
        this.addr = addr;
        connected = true;
        newMessagesB = false;
        conversation = new Vector<>();
        setChanged();
        notifyObservers();
    }
    public boolean isConnected() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected = connected;
        setChanged();
        notifyObservers();
    }
    public synchronized Vector<Messages> getConversation() {
        newMessagesB=false;
        return conversation;
    }
    public synchronized void addMessage(Messages msg) {
        conversation.addElement(msg);
        newMessagesB=true;
        setChanged();
        notifyObservers();
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
        setChanged();
        notifyObservers();
    }
    public InetAddress getAddr() {
        return addr;
    }
    public boolean newMessages() {
        return newMessagesB;
    }
    public String toString() {
        return nickname +  " @" + addr;
    }
    @Override
    public boolean equals(Object u2){
        return ((this.nickname).equals(((User)u2).getNickname()));
    }
}