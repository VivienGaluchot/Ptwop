package ptwop.p2p;

import java.io.IOException;

public interface P2PHandler {
	/**
	 * Function called by P2P system when a message is received
	 * 
	 * @param sender
	 * @param o
	 *            : object received
	 * @throws IOException
	 */
	void handleMessage(P2PUser sender, Object o);

	void handleConnection(P2PUser user);

	void handleUserUpdate(P2PUser user);

	/**
	 * Function called by the P2P system when a user is disconnected from the
	 * network
	 * 
	 * @param user
	 */
	void handleUserDisconnect(P2PUser user);
}
