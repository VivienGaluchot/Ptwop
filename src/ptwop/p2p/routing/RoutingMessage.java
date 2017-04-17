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
	public boolean forwarded = false;

	/**
	 * If destAddress == null, the message is for you
	 */
	public NAddress destAddress = null;

	public Object object = null;

	public int id = 0;

	public RoutingMessage(NAddress sender, NAddress dest, Object object) {
		this.sourceAddress = sender;
		forwarded = sourceAddress != null;
		this.destAddress = dest;
		this.object = object;
		id = 0;
	}

	public boolean isForwarded() {
		return forwarded;
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
		String str = "";
		if (isForwarded() && sourceAddress != null)
			str += sourceAddress + " -> ";
		else if(isForwarded() && sourceAddress == null)
			str += "You -> ";
		else
			str += "Me -> ";
		if (isToForward())
			str += destAddress + ", ";
		else
			str += "You, ";
		if (isResponse())
			str += "Response";
		else
			str += "[" + object.toString() + "]";
		str += " " + id;
		return str;
	}
}
