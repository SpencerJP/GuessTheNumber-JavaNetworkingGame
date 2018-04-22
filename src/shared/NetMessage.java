package shared;

import java.io.Serializable;

public class NetMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1504755199159148621L;
	
	private MessageType messtype;
	private String msg;
	
	public NetMessage(MessageType type, String message) {
		this.messtype = type;
		this.msg = message;
	}
	
	public MessageType getMessageType() {
		return messtype;
	}
	
	public String getMessage() {
		return msg;
	}
	
}
