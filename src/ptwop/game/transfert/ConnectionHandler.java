package ptwop.game.transfert;

import java.io.IOException;

public interface ConnectionHandler {
	/**
	 * Function called when message is received
	 * @throws IOException 
	 */
	void handleMessage(Connection connection, Object o) throws IOException;
	
	void connectionClosed(Connection connection);
}
