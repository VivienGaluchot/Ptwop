package ptwop.p2p.routing;

import ptwop.common.Clock;
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
		double totalProb = 0;
		for (P2PUser u : p2p.getUsers()) {
			totalProb += relativeBestUserProbability(destination, u);
		}

		double p = Math.random() * totalProb;
		double cumulativeProbability = 0;
		for (P2PUser u : p2p.getUsers()) {
			cumulativeProbability += relativeBestUserProbability(destination, u);
			if (p <= cumulativeProbability) {
				return u;
			}
		}
		return destination;
	}

	private double relativeBestUserProbability(P2PUser destination, P2PUser user) {
		int lat = getObservedLatency(destination, user, user.getBindedNPair().getLatency());
		if (user.equals(destination))
			return 2 / (lat * lat * lat + 1.0);
		else
			return 1 / (lat * lat * lat + 1.0);
	}
}
