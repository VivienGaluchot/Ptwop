package ptwop.p2p.base;

public class MyNameIs extends P2PMessage {
	private static final long serialVersionUID = 1L;
	public String name;

	public MyNameIs(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "MyNameIs " + name;
	}
}
