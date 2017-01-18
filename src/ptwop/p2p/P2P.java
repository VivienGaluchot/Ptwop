package ptwop.p2p;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

public interface P2P {
	/**
	 * Connect to a p2p network
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void connect() throws UnknownHostException, IOException;

	/**
	 * disconnect from the p2p network
	 */
	public void disconnect();

	/**
	 * send msg to all users
	 * @param msg
	 */
	public void broadcast(Object msg);
	
	/**
	 * send the message msg to user dest
	 * @param dest
	 * @param msg
	 */
	public void sendTo(P2PUser dest, Object msg);

	/**
	 * get the p2p network user set
	 * @return
	 */
	public Set<P2PUser> getUsers();
	
	/**
	 * Get your User object
	 * @return
	 */
	public P2PUser getMyself();

	/**
	 * set the message handler, it will be used when a message is received
	 * @param handler
	 */
	public void setMessageHandler(MessageHandler handler);
}
