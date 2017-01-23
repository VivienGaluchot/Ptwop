package ptwop.p2p.v0.messages;

public class MyId extends FloodMessage {
	private static final long serialVersionUID = 1L;

	public int id;

	public MyId(int id) {
		this.id = id;
	}
}
