package ptwop.p2p.routing;

import ptwop.common.Clock;
import ptwop.common.math.RandomCollection;
import ptwop.p2p.P2PUser;

public class StockasticLogRouter4 extends LogRouter {

	public StockasticLogRouter4() {
		super();
	}

	public StockasticLogRouter4(Clock clock) {
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
			// System.out.print(u + ":\n");
			double p = relativeBestUserProbability(destination, u);
			// System.out.print("\t" + p + "\n");
			dests.add(p, u);
		}
		// System.out.print("\n");
		// return dests.next();
		return dests.next();
	}

	private double relativeBestUserProbability(P2PUser dest, P2PUser next) {
		Integer lat = getObservedLatency(dest, next);
		Integer recordLat = getSendRecordsLatency(dest, next);
		if (lat == null) {
			lat = next.getBindedNPair().getLatency();
			if (recordLat != null)
				lat += recordLat;
			// System.out.print(lat);
			if (next.equals(dest))
				return 2000000 / (lat * lat * lat + 1.0);
			else
				return 1000000 / (lat * lat * lat + 1.0);
		} else {
			if (recordLat != null)
				lat += recordLat;
			// System.out.print(lat);
			return 10000000 / (lat + 1.0);
		}
	}
}
