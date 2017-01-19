package ptwop.p2p.v0.messages;

public class ConnectTo extends FloodMessage{
	private static final long serialVersionUID = 1L;
	
	public String ip;
	public int id;

	public ConnectTo(int id, String ip) {
		this.id = id;
		this.ip = ip;
	}
}
