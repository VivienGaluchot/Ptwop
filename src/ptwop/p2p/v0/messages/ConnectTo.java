package ptwop.p2p.v0.messages;

import java.net.InetAddress;

public class ConnectTo extends FloodMessage {
	private static final long serialVersionUID = 1L;

	public int id;
	public InetAddress ip;
	public int port;

	public ConnectTo(int id, InetAddress ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
	}
}
