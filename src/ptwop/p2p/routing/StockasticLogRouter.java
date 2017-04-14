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
//			System.out.print(u + ":\n");
			double p = relativeBestUserProbability(destination, u);
//			System.out.print("\t" + p + "\n");
			dests.add(p, u);
		}
//		System.out.print("\n");
		return dests.next();
	}

	private double relativeBestUserProbability(P2PUser destination, P2PUser user) {
		Integer lat = getObservedLatency(destination, user);
		if (lat == null) {
			lat = user.getBindedNPair().getLatency();
//			System.out.print(lat);
			if (user.equals(destination))
				return 20000000 / (lat * lat * lat + 1.0);
			else
				return 1000000 / (lat * lat * lat + 1.0);
		} else {
//			System.out.print(lat);
			return 100 / (lat + 1.0);
		}
	}
}
