package ptwop.p2p.flood.messages;

public class MyNameIs extends FloodMessage {
	private static final long serialVersionUID = 1L;
	public String name;

	public MyNameIs(String name) {
		this.name = name;
	}

	public String toString() {
		return "MyNameIs " + name;
	}
}
