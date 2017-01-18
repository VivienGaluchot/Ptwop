package ptwop.p2p;

import java.io.IOException;

public interface MessageHandler {
	/**
	 * Function called when message is received
	 * 
	 * @throws IOException
	 */
	void handleMessage(P2PUser sender, Object o) throws IOException;

	void connectionClosed(P2PUser user);
}
