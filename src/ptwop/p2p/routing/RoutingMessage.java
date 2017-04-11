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

	public Object object = null;

	public int id = 0;

	public RoutingMessage(NAddress sender, NAddress dest, Object object) {
		sourceAddress = sender;
		destAddress = dest;
		this.object = object;
		id = 0;
	}

	public boolean isFromDirectSender() {
		return sourceAddress == null;
	}

	public boolean isToForward() {
		return destAddress != null;
	}

	public boolean isResponse() {
		return object == null;
	}

	/**
	 * Same id, dest <=> source, object = null
	 * 
	 * @return
	 */
	public RoutingMessage getResponse() {
		RoutingMessage rm = new RoutingMessage(destAddress, sourceAddress, null);
		rm.id = id;
		return rm;
	}

	@Override
	public String toString() {
		String str = "R, ";
		if (isToForward())
			str += "Forward to " + destAddress + ", ";
		else
			str += "Keep, ";
		if (isFromDirectSender())
			str += "From me, ";
		else
			str += "From " + sourceAddress + ", ";
		if (isResponse())
			str += "Response";
		else
			str += "[" + object.toString() + "]";
		return str;
	}
}
