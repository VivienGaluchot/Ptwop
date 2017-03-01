package ptwop.p2p.flood.messages;

public class MessageToApp extends FloodMessage {
	private static final long serialVersionUID = 1L;

	public Object msg;

	public MessageToApp(Object msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "MessageToApp : " + msg.toString();
	}
}
