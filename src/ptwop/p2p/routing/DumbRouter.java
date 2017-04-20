package ptwop.p2p.routing;

import java.io.IOException;

import ptwop.common.Clock;
import ptwop.p2p.P2PUser;

public class DumbRouter extends Router {

	public DumbRouter() {
		super();
	}

	public DumbRouter(Clock clock) {
		super(clock);
	}

	@Override
	public String toString() {
		return "DumbRouter";
	}

	@Override
	public P2PUser getRoute(P2PUser destination) {
		return destination;
	}

	@Override
	public void routeTo(P2PUser dest, Object msg) throws IOException {
		// new routing message, sender = null because is me
		RoutingMessage rm = new RoutingMessage(null, dest.getAddress(), msg);
		sendRoutingMessage(dest, rm);
	}

	private void sendRoutingMessage(P2PUser dest, RoutingMessage rm) throws IOException {
		P2PUser next = getRoute(dest);

		// destAddress = null if next is dest
		if (next.getAddress().equals(rm.destAddress))
			rm.destAddress = null;

		// send routing message
		next.sendDirectly(rm);
	}

	@Override
	public void processRoutingMessage(P2PUser prec, RoutingMessage rm) throws IOException {
		// replace address if sender is user
		if (!rm.isForwarded())
			rm.sourceAddress = prec.getAddress();

		if (rm.isResponse()) {
			throw new IllegalArgumentException("DumbRouter can't work response message");
		} else if (rm.isToForward()) {
			// next : user to forward
			P2PUser next = p2p.getUser(rm.destAddress);
			if (next != null)
				sendRoutingMessage(next, rm);
			else
				throw new IllegalArgumentException("Didn't find SendRecord with id " + rm.id);
		} else {
			// source : sender of routing message
			P2PUser source = p2p.getUser(rm.sourceAddress);
			if (source != null)
				// handle message
				handler.handleIncommingMessage(source.getBindedNPair(), rm.object);
			else
				throw new IllegalArgumentException("Didn't find source user with address " + rm.sourceAddress);
		}
	}
}