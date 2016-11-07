package ptwop.game.transfert.messages;

public class TextMessage extends Message {
	private static final long serialVersionUID = 0L;

	private String message;

	public TextMessage(int timeStamp, String message) {
		this.setTimeStamp(timeStamp);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return message;
	}
}
