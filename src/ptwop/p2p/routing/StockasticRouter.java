package ptwop.p2p.routing;

import java.util.Set;

import ptwop.p2p.P2PUser;

public class StockasticRouter implements Router {

	public double relativeBestUserProbability(P2PUser user) {
		return 1 / (user.getLatency() + 1);
	}

	@Override
	public P2PUser getRoute(Set<P2PUser> choices, P2PUser destination) {
		double totalProb = 0;
		for (P2PUser u : choices) {
			double currentProb;
			if (u != destination)
				currentProb = relativeBestUserProbability(u);
			else
				currentProb = 100 * relativeBestUserProbability(u);
			totalProb += currentProb;
		}

		double p = Math.random() * totalProb;
		double cumulativeProbability = 0;
		for (P2PUser u : choices) {
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

}
