package ptwop.p2p.routing;

import java.io.IOException;

import ptwop.p2p.P2PUser;

public class DumbRouter extends Router {
	public DumbRouter() {
		super();
	}

	@Override
	public P2PUser getRoute(P2PUser destination) {
		return destination;
	}

	@Override
	public void routeTo(P2PUser dest, Object msg) throws IOException {
		P2PUser trueDest = getRoute(dest);
		if (trueDest.equals(dest))
			trueDest.send(msg);
		else
			trueDest.send(new RoutingMessage(null, dest.getAddress(), msg));
	}

	@Override
	public void processRoutingMessage(P2PUser npair, RoutingMessage rm) throws IOException {
		if (rm.sourceAddress == null)
			rm.sourceAddress = npair.getAddress();
		if (rm.destAddress != null) {
			// To forward
			P2PUser next = p2p.getUserWithAddress(rm.destAddress);
			if(next != null)
				routeTo(next, rm.object);
			else
				npair.send(new RoutingMessage(rm.sourceAddress, rm.destAddress, rm.object));
			return;
		} else {
			// To process
			handler.incommingMessage(npair, rm.object);
		}
	}
}