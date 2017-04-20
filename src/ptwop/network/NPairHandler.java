package ptwop.network;

/**
 * Handler model for NPair events. Those functions will be called when the
 * corresponding event occur in a NServent or a NPair object.
 */
public interface NPairHandler {
	/**
	 * An incoming connection have been received from the NServent. The other
	 * tried to reach me first with his NServent connectTo() function.
	 * 
	 * @param npair,
	 *            new NPair object corresponding to the new connection
	 */
	void handleConnectionFrom(NPair npair);

	/**
	 * An connection have been established with the given NPair. I'm the one who
	 * tried to reach the other pair first with NServent connectTo() function.
	 * 
	 * @param npair,
	 *            new NPair object corresponding to the new connection
	 */
	void handleConnectionTo(NPair npair);

	/**
	 * A message have been received by the NPair given.
	 * 
	 * @param npair
	 *            message's sender
	 * @param o
	 *            message received
	 */
	void handleIncommingMessage(NPair npair, Object o);

	/**
	 * A network connection have been closed or lost. The NPair returned won't
	 * be able to send any more data.
	 * 
	 * @param npair
	 *            corresponding NPair
	 */
	void handleConnectionClosed(NPair npair);
}
