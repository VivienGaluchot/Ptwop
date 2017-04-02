package ptwop.p2p.routing;

import ptwop.network.NAddress;
import ptwop.p2p.P2PUser;
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

	public RoutingMessage(P2PUser sender, P2PUser dest, Object object) {
		if (sender != null)
			sourceAddress = sender.getAddress();
		else
			sourceAddress = null;

		if (dest != null)
			destAddress = dest.getAddress();
		else
			destAddress = null;

		this.object = object;
	}

	@Override
	public String toString() {
		return "R " + object.toString();
	}
}
