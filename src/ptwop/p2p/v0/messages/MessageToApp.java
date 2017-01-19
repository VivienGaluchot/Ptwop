package ptwop.p2p.v0.messages;

public class MessageToApp extends FloodMessage{
	private static final long serialVersionUID = 1L;
	
	public Object msg;

	public MessageToApp(Object msg) {
		this.msg = msg;
	}
}
