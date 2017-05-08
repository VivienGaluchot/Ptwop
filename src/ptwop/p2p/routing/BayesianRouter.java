package ptwop.p2p.routing;

import ptwop.common.Clock;
import ptwop.common.math.RandomCollection;
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
		RandomCollection<P2PUser> dests = new RandomCollection<>();
		for (P2PUser u : p2p.getUsers()) {
			double p = relativeBestUserProbability(destination, u);
			dests.add(p, u);
		}
		return dests.next();
	}

	private double relativeBestUserProbability(P2PUser destination, P2PUser route) {
		double lat = route.getBindedNPair().getLatency();
		int latency = (int) Math.round(lat);
		return probabilityBestRouteWithLatency(destination, route, latency)
				* probabilityGettingLatency(destination, route, latency)
				/ probabilityGettingLatencyWhileBeeingBestRoute(destination, route, latency);
	}

	private double probabilityBestRouteWithLatency(P2PUser destination, P2PUser route, int latency) {
		return 1.0 / latency;
	}

	private double probabilityGettingLatency(P2PUser destination, P2PUser route, int latency) {
		return latency;
	}

	private double probabilityGettingLatencyWhileBeeingBestRoute(P2PUser destination, P2PUser route, int latency) {
		return 1.0 / latency;
	}
}