package ptwop.game.transfert.messages;

import java.util.ArrayList;

public class MessagePack extends Message {
	private static final long serialVersionUID = 0L;

	public ArrayList<Message> messages;

	public MessagePack() {
		messages = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "MessagePack, " + messages.size() + " elements";
	}
}