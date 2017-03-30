package ptwop.p2p.base;

public class MessageToApp extends P2PMessage {
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
