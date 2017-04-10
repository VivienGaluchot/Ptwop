package ptwop.p2p.routing;

import ptwop.p2p.P2PUser;

public class LatencyRecord {
	public P2PUser route;
	public int latency;
	
	public LatencyRecord(P2PUser route, int latency){
		this.route = route;
		this.latency = latency;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof LatencyRecord && ((LatencyRecord) o).route.equals(route);
	}

	@Override
	public int hashCode() {
		return route.hashCode();
	}
}