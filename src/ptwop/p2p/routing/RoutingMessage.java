package ptwop.p2p.routing;

import ptwop.network.NAddress;
import ptwop.p2p.base.P2PMessage;

public class RoutingMessage extends P2PMessage {
	private static final long serialVersionUID = 1L;

	/**
	 * If senderAddress == null, the sender is the sender of this packet and
	 * don't knows his address
	 */
	public NAddress sourceAddress = null;

	/**
	 * If destAddress == null, the message is for you
	 */
	public NAddress destAddress = null;

	public Object object;

	public RoutingMessage(NAddress sender, NAddress dest, Object object) {
		sourceAddress = sender;
		destAddress = dest;
		this.object = object;
	}

	@Override
	public String toString() {
		return "R " + destAddress + " [" + object.toString() + "]";
	}
}
