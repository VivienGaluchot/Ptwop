package ptwop.game.transfert.messages;

import java.io.Serializable;

public class HelloFromClient implements Serializable {
	private static final long serialVersionUID = 0L;

	private String name;

	public HelloFromClient(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "HelloFromClient > Pseudo : " + name;
	}
}