package ptwop.game.transfert.messages;

import java.io.Serializable;

public class TextMessage implements Serializable {
	private static final long serialVersionUID = 0L;
	
	private String message;

	public TextMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return message;
	}
}
