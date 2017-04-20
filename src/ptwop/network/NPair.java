package ptwop.network;

import java.io.IOException;

/**
 * NPair for Network Pair. Will be returned by the NServent when a network
 * connection is established and can be used to communicate with pair over the
 * network
 */
public interface NPair {

	/**
	 * start NPair message listening
	 */
	public void start();

	/**
	 * Send object to the pair
	 * 
	 * @param o
	 *            object to send
	 * @throws IOException
	 */
	public void send(Object o) throws IOException;

	/**
	 * Return the pair's network address
	 * 
	 * @return
	 */
	public NAddress getAddress();

	/**
	 * Return the last known value of latency to reach the node
	 * 
	 * @return latency time in ms
	 */
	public int getLatency();

	/**
	 * Disconnect from pair
	 */
	public void disconnect();
}
