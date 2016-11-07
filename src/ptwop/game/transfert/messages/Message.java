package ptwop.game.transfert.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private int timeStamp;

	public Message() {
		timeStamp = -1;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getTimeStamp() {
		return timeStamp;
	}
}
