package ptwop.p2p.routing;

import ptwop.common.Clock;
import ptwop.common.math.RandomCollection;
import ptwop.p2p.P2PUser;

public class StockasticLogRouter2 extends LogRouter {

	public StockasticLogRouter2() {
		super();
	}

	public StockasticLogRouter2(Clock clock) {
		super(clock);
	}

	@Override
	public String toString() {
		return "StockasticLogRouter2";
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
		if (lat == null) {
			lat = user.getBindedNPair().getLatency();
			if (user.equals(destination))
				return 2 / (lat * lat * lat + 1.0);
			else
				return 1 / (lat * lat * lat + 1.0);
		} else {
			return 1 / (lat + 1.0);
		}
	}
}
