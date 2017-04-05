package ptwop.network;

import java.io.IOException;

public interface NPair {

	/**
	 * start NPair message listening
	 */
	public void start();

	/**
	 * Send objet to the pair
	 * 
	 * @param o
	 *            object to send
	 * @throws IOException
	 */
	public void send(byte[] bytes) throws IOException;

	/**
	 * Disconnect from pair
	 */
	public void disconnect();

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
}
