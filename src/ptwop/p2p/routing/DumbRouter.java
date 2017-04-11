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
		P2PUser trueDest = getRoute(dest);
		if (trueDest.equals(dest))
			trueDest.sendDirectly(msg);
		else
			trueDest.sendDirectly(new RoutingMessage(null, dest.getAddress(), msg));
	}

	@Override
	public void processRoutingMessage(P2PUser user, RoutingMessage rm) throws IOException {
		if (rm.sourceAddress == null)
			rm.sourceAddress = user.getAddress();
		
		if (rm.destAddress != null) {
			// next : user to forward
			P2PUser next = p2p.getUser(rm.destAddress);
			if (next != null)
				// forward
				routeTo(next, rm.object);
			else
				// return message to sender
				user.sendDirectly(new RoutingMessage(rm.sourceAddress, rm.destAddress, rm.object));
		} else {
			// To process
			P2PUser source = p2p.getUser(rm.sourceAddress);
			if(source != null)
				handler.incommingMessage(source.getBindedNPair(), rm.object);
		}
	}
}