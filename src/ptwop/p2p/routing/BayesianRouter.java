package ptwop.p2p.routing;

import ptwop.common.Clock;
import ptwop.p2p.P2PUser;

public class BayesianRouter extends LogRouter {

	public BayesianRouter() {
		super();
	}

	public BayesianRouter(Clock clock) {
		super(clock);
	}

	@Override
	public String toString() {
		return "BayesianRouter";
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

	private double relativeBestUserProbability(P2PUser destination, P2PUser route) {
		double lat = route.getBindedNPair().getLatency();
		int latency = (int) Math.round(lat);
		return probabilityBestRouteWithLatency(destination, route, latency)
				* probabilityGettingLatency(destination, route, latency)
				/ probabilityGettingLatencyWhileBeeingBestRoute(destination, route, latency);
	}

	private double probabilityBestRouteWithLatency(P2PUser destination, P2PUser route, int latency) {
		// TODO
		return 1;
	}

	private double probabilityGettingLatency(P2PUser destination, P2PUser route, int latency) {
		// TODO
		return 1;
	}

	private double probabilityGettingLatencyWhileBeeingBestRoute(P2PUser destination, P2PUser route, int latency) {
		// TODO
		return 1;
	}
}