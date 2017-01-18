package ptwop.p2p;

import java.io.IOException;

import ptwop.game.transfert.messages.Message;

public interface MessageHandler {
	/**
	 * Function called when message is received
	 * 
	 * @throws IOException
	 */
	void handleMessage(User sender, Object o) throws IOException;

	void connectionClosed(User user);
}
