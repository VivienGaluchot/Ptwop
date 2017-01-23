package ptwop.p2p.v0.messages;

public class Hello extends FloodMessage {
	private static final long serialVersionUID = 1L;

	public int listenPort;

	public Hello(int listenPort) {
		this.listenPort = listenPort;
	}
}
