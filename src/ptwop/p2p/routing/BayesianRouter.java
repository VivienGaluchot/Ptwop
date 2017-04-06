package ptwop.p2p.routing;

import java.util.HashMap;
import java.util.HashSet;

import ptwop.p2p.P2PUser;

public class BayesianRouter extends DumbRouter {

	public BayesianRouter() {
		super();
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

	private double relativeBestUserProbability(P2PUser destination, P2PUser user) {
		double lat = user.getBindedNPair().getLatency();
		if (user.equals(destination))
			return 2 / (lat * lat * lat + 1.0);
		else
			return 1 / (lat * lat * lat + 1.0);
	}

	// Learning
	
	private HashMap<Integer, SendRecord> idSendMap;
	private HashSet<LatencyRecord> latencyRecords;

	/**
	 * Record used to keep track of message sent and learn from them
	 *
	 */
	private class SendRecord {
		P2PUser destination;
		P2PUser route;
		long sendTime;
	}

	private class LatencyRecord {
		P2PUser destination;
		P2PUser route;
		long latency;
	}
}
