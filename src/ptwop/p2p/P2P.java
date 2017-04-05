package ptwop.p2p;

import java.io.IOException;
import java.util.Set;

import ptwop.network.NAddress;
import ptwop.network.NPair;

public interface P2P {

	/**
	 * start P2P system
	 */
	public void start();

	/**
	 * disconnect from the p2p network
	 */
	public void stop();

	/**
	 * connect to a p2p network
	 */
	public void connectTo(NAddress address) throws IOException;

	/**
	 * send msg to all users
	 * 
	 * @param msg
	 */
	public void broadcast(byte[] bytes);

	/**
	 * send the message msg to user dest
	 * 
	 * @param dest
	 * @param msg
	 * @throws IOException
	 */
	public void sendTo(P2PUser dest, byte[] bytes) throws IOException;

	/**
	 * send msg to some users contained in dests set
	 * 
	 * @param msg
	 */
	public void anycast(Set<P2PUser> dests, byte[] bytes);

	/**
	 * get the p2p network user set
	 * 
	 * @return
	 */
	public Set<P2PUser> getUsers();

	/**
	 * Find the user with the address given, return null if not present
	 * 
	 * @param address
	 * @return user with address given
	 */
	public P2PUser getUser(NAddress address);

	/**
	 * Find the user binded with the pair given, return null if not present
	 * 
	 * @param address
	 * @return user with address given
	 */
	public P2PUser getUser(NPair pair);

	/**
	 * set the message handler, it will be used when a message is received
	 * 
	 * @param handler
	 */
	public void setMessageHandler(P2PHandler handler);
}
