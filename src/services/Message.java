package services;

import java.io.Serializable;

/**
 * Created by ValentinC on 22/10/2015.
 *
 */
public class Message implements Serializable {

    private static enum header_type{
        hello, helloAck, bye, message
    }

    private header_type header;
    private String data;

    private Message(header_type header,String message){
        this.header=header;
        this.data = message;
    }

    public header_type getHeader() {
		return header;
	}

	public String getData() {
		return data;
	}

	private Message(header_type header){
        this.header=header;
        this.data = null;
    }

    public static Message createHello(String nickname){
        return new Message(header_type.hello, nickname);
    }

    public static Message createBye(){
        return new Message(header_type.bye);
    }

    public static Message createHelloAck(String nickname){
        return new Message(header_type.helloAck,nickname);
    }

    public static Message createMessage(String message){
        return new Message(header_type.message,message);
    }
    
    
}
