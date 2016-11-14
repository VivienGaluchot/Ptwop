package ptwop.game.transfert;

import java.io.IOException;

import ptwop.game.transfert.messages.Message;

public interface ConnectionHandler {
	/**
	 * Function called when message is received
	 * 
	 * @throws IOException
	 */
	void handleMessage(Connection connection, Message o) throws IOException;

	void connectionClosed(Connection connection);
}
