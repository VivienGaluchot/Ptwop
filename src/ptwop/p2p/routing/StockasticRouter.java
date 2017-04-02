package ptwop.p2p.routing;

import java.io.IOException;

import ptwop.p2p.P2PUser;

public class StockasticRouter extends DumbRouter {

	public StockasticRouter() {
		super();
	}

	@Override
	public void routeTo(P2PUser dest, Object msg) throws IOException {
		P2PUser trueDest = getRoute(dest);
		if (trueDest.equals(dest))
			trueDest.send(msg);
		else
			trueDest.send(new RoutingMessage(null, dest, msg));
	}

	@Override
	public P2PUser getRoute(P2PUser destination) {
		double totalProb = 0;
		for (P2PUser u : p2p.getUsers()) {
			double currentProb;
			if (u != destination)
				currentProb = relativeBestUserProbability(u);
			else
				currentProb = 2 * relativeBestUserProbability(u);
			totalProb += currentProb;
		}

		double p = Math.random() * totalProb;
		double cumulativeProbability = 0;
		for (P2PUser u : p2p.getUsers()) {
			double currentProb;
			if (u != destination)
				currentProb = relativeBestUserProbability(u);
			else
				currentProb = 2 * relativeBestUserProbability(u);
			cumulativeProbability += currentProb;
			if (p <= cumulativeProbability) {
				return u;
			}
		}

		return destination;
	}

	private double relativeBestUserProbability(P2PUser user) {
		return 1 / (user.getLatency() + 1);
	}
}
