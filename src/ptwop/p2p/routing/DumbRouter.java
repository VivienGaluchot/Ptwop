package ptwop.p2p.routing;

import java.util.Set;

import ptwop.p2p.P2PUser;

public class DumbRouter implements Router {

	@Override
	public P2PUser getRoute(Set<P2PUser> choices, P2PUser destination) {
		return destination;
	}

}