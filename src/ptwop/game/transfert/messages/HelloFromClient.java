package ptwop.game.transfert.messages;

public class HelloFromClient extends Message {
	private static final long serialVersionUID = 0L;

	public String name;

	public HelloFromClient(int timeStamp, String name) {
		this.setTimeStamp(timeStamp);
		this.name = name;
	}

	public String toString() {
		return "HelloFromClient > Pseudo : " + name;
	}
}