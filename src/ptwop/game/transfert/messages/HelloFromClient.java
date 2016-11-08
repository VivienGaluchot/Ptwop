package ptwop.game.transfert.messages;

public class HelloFromClient extends Message {
	private static final long serialVersionUID = 0L;

	public String name;

	public HelloFromClient(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "HelloFromClient > Pseudo : " + name;
	}
}