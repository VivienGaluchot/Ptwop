package ptwop.network;

import java.io.IOException;

public interface NPair {
	/**
	 * Send objet to the pair
	 * @param o object to send
	 * @throws IOException
	 */
	public void send(Object o) throws IOException;

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
	 * @return latency, in ms
	 */
	public int getLatency();
}
