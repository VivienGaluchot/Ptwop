package ptwop.p2p.network;

import java.net.Socket;

public interface SocketHandler {
	/**
	 * Function called when a new client is connected
	 * @param socket : client's socket
	 */
	public void handleSocket(Socket socket);
}
