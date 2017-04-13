package ptwop.p2p.routing;

import ptwop.common.Clock;
import ptwop.common.math.RandomCollection;
import ptwop.p2p.P2PUser;

public class StockasticLogRouter extends LogRouter {

	public StockasticLogRouter() {
		super();
	}

	public StockasticLogRouter(Clock clock) {
		super(clock);
	}

	@Override
	public String toString() {
		return "StockasticLogRouter";
	}

	@Override
	public P2PUser getRoute(P2PUser destination) {
		RandomCollection<P2PUser> dests = new RandomCollection<>();
		for (P2PUser u : p2p.getUsers()) {
			dests.add(relativeBestUserProbability(destination, u), u);
		}
		return dests.next();
	}

	private double relativeBestUserProbability(P2PUser destination, P2PUser user) {
		Integer lat = getObservedLatency(destination, user);
		if (lat == null)
			lat = user.getBindedNPair().getLatency() * 5;
		if (user.equals(destination))
			return 2 / (lat * lat * lat + 1.0);
		else
			return 1 / (lat * lat * lat + 1.0);
	}
}
