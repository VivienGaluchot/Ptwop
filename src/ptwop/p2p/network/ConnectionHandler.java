package ptwop.p2p.network;

import java.io.IOException;

public interface ConnectionHandler {
	/**
	 * Function called when message is received
	 * 
	 * @throws IOException
	 */
	void handleMessage(Connection connection, Object o) throws IOException;

	void connectionClosed(Connection connection);
}
