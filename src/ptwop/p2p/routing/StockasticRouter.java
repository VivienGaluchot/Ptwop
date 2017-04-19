package ptwop.p2p.routing;

import ptwop.common.Clock;
import ptwop.common.math.RandomCollection;
import ptwop.p2p.P2PUser;

public class StockasticRouter extends DumbRouter {

	public StockasticRouter() {
		super();
	}

	public StockasticRouter(Clock clock) {
		super(clock);
	}

	@Override
	public String toString() {
		return "StockasticRouter";
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
		int lat = user.getBindedNPair().getLatency();
		if (user.equals(destination))
			return 2000000 / (double) (lat * lat * lat + 1);
		else
			return 1000000 / (double) (lat * lat * lat + 1);
	}
}
