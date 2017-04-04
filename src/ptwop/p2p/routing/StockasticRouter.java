package ptwop.p2p.routing;

import java.io.IOException;

import ptwop.p2p.P2PUser;

public class StockasticRouter extends DumbRouter {

	public StockasticRouter() {
		super();
	}
	
	@Override
	public String toString(){
		return "StockasticRouter";
	}
	
	// TODO send the info only once to all user
	public void broadcast(Object msg){
		for (P2PUser u : p2p.getUsers()) {
			try {
				u.send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void routeTo(P2PUser dest, Object msg) throws IOException {
		P2PUser trueDest = getRoute(dest);
		if (trueDest.equals(dest))
			trueDest.send(msg);
		else
			trueDest.send(new RoutingMessage(null, dest.getAddress(), msg));
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
		double lat = user.getLatency();
		if (user.equals(destination))
			return 2 / (lat * lat * lat + 1.0);
		else
			return 1 / (lat * lat * lat + 1.0);
	}
}
