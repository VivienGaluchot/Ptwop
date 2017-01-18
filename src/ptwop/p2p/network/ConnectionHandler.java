package ptwop.p2p.network;

public interface ConnectionHandler {
	/**
	 * Function called by the Connection thread when a object is received
	 * 
	 * @param connection
	 *            : connection receiving
	 * @param o
	 *            : object received
	 */
	void handleMessage(Connection connection, Object o);

	/**
	 * Function called bu the Connection thread when the connection is closed
	 * 
	 * @param connection
	 *            : connection closed
	 */
	void connectionClosed(Connection connection);
}
