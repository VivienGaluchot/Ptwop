package ptwop.p2p.v0.messages;

import java.util.ArrayList;

public class MessagePack extends FloodMessage{
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Object> messages;

	public MessagePack() {
		messages = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "MessagePack, " + messages.size() + " elements";
	}
}