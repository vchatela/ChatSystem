package services.model;

/**
 * Class TextMsg
 * Used to store information about a text message
 * Is a Msg
 */
public class TextMessage extends Messages{
    private String text;
    public TextMessage(String text, User sender) {
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