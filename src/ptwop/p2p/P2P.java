package ptwop.p2p;

import java.io.IOException;
import java.util.Set;

import ptwop.network.NAddress;

public interface P2P {
	/**
	 * connect to a p2p network
	 */
	public void start();
	
	public void connectTo(NAddress address) throws IOException;

	/**
	 * disconnect from the p2p network
	 */
	public void stop();

	/**
	 * send msg to all users
	 * 
	 * @param msg
	 */
	public void broadcast(Object msg);

	/**
	 * send the message msg to user dest
	 * 
	 * @param dest
	 * @param msg
	 * @throws IOException 
	 */
	public void sendTo(P2PUser dest, Object msg) throws IOException;

	/**
	 * get the p2p network user set
	 * 
	 * @return
	 */
	public Set<P2PUser> getUsers();

	/**
	 * Get your User object
	 * 
	 * @return
	 */
	public P2PUser getMyself();

	/**
	 * set the message handler, it will be used when a message is received
	 * 
	 * @param handler
	 */
	public void setMessageHandler(P2PHandler handler);
}
