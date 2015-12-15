package services.model;

/**
 * Class Msg
 * Generic message send by a user
 * Should be extended to match a real message (text, string, ...)
 */
public abstract class Messages{
    private User sender;
    public Messages(User sender){
        this.sender = sender;
    }
    public User getSender() {
        return sender;
    }
    public String toString(){
        String header = new String(getSender().getNickname() + ": " + System.lineSeparator());
        return header;
    }
}