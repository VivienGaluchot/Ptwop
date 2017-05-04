package ptwop.p2p;

import java.io.IOException;
import java.util.Set;

import ptwop.network.NAddress;
import ptwop.network.NPair;
import ptwop.network.NServent;
import ptwop.p2p.routing.Router;

/**
 * This interface lists the core functions of a P2P system. P2P objects should
 * be designed to execute them on any network given.
 *
 */
public interface P2P {

	/**
	 * Start the P2P system with given servent and router system
	 * 
	 * @param servent
	 * @param router
	 */
	public void start(NServent servent, Router router);

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
	public void broadcast(Object msg);

	/**
	 * send msg to some users contained in dests set
	 * 
	 * @param msg
	 */
	public void anycast(Set<P2PUser> dests, Object msg);

	/**
	 * send the message msg to user dest, the routing system will be used
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
	 * Set the handler, its functions will be called when a corresponding event
	 * will occur
	 * 
	 * @param handler
	 */
	public void setP2PHandler(P2PHandler handler);

	/**
	 * Return the Router currently used by the P2P system
	 * 
	 * @return
	 */
	public Router getRouter();
	
	/**
	 * Return the NServent currently used by P2P the system
	 * 
	 * @return
	 */
	public NServent getNServent();
}
