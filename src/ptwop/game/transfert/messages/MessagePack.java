package ptwop.game.transfert.messages;

import java.util.ArrayList;

public class MessagePack extends Message {
	private static final long serialVersionUID = 0L;

	public ArrayList<Message> array;

	public MessagePack() {
		array = new ArrayList<>();
	}

	public String toString() {
		return "MessagePack, " + array.size() + " elements";
	}
}