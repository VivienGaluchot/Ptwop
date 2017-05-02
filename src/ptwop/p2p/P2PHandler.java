package ptwop.p2p;

import java.io.IOException;

public interface P2PHandler {

	/**
	 * Function called by P2P system when a new user is connected to the network
	 * 
	 * @param user
	 *            new user
	 */
	void handleConnection(P2PUser user);

	/**
	 * Function called by P2P system when user's informations have been updated.
	 * For example when his name have been received for the first time or
	 * changed.
	 * 
	 * @param user
	 *            updated object
	 */
	void handleUserUpdate(P2PUser user);

	/**
	 * Function called by P2P system when a message is received
	 * 
	 * @param sender
	 *            user known as sender of the message
	 * @param o
	 *            object received from the sender through the P2P network
	 * @throws IOException
	 */
	void handleMessage(P2PUser sender, Object o);

	/**
	 * Function called by the P2P system when a user is disconnected from the
	 * network
	 * 
	 * @param user
	 */
	void handleUserDisconnect(P2PUser user);
}
