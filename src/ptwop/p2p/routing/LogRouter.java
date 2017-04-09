package ptwop.p2p.routing;

import java.io.IOException;
import java.util.HashMap;

import ptwop.p2p.P2PUser;

public class LogRouter extends DumbRouter {

	private int msgCounter;

	// Learning

	private HashMap<Integer, SendRecord> idSendMap;
	private HashMap<P2PUser, LatencyRecord> latencyRecords;

	public LogRouter() {
		super();
		msgCounter = 0;
		idSendMap = new HashMap<>();
		latencyRecords = new HashMap<>();
	}

	@Override
	public String toString() {
		return "LogRouter";
	}

	@Override
	public void routeTo(P2PUser dest, Object msg) throws IOException {
		sendRoutingMessage(dest, new RoutingMessage(null, dest.getAddress(), msg));
	}
	
	private void sendRoutingMessage(P2PUser dest, RoutingMessage rm) throws IOException {
		P2PUser trueDest = getRoute(dest);

		if (trueDest.equals(dest))
			rm.destAddress = null;
		
		synchronized (idSendMap) {
			rm.id = msgCounter;
			trueDest.sendDirectly(rm);

			SendRecord sd = new SendRecord();
			sd.destination = dest;
			sd.route = trueDest;
			idSendMap.put(msgCounter, sd);

			msgCounter++;
		}
	}

	@Override
	public void processRoutingMessage(P2PUser user, RoutingMessage rm) throws IOException {
		if (rm.isFromDirectSender())
			rm.sourceAddress = user.getAddress();

		if (rm.isToForward()) {
			// next : user to forward
			P2PUser next = p2p.getUser(rm.destAddress);
			if (next != null)
				// forward
				routeTo(next, rm.object);
			else
				// return message to sender
				user.sendDirectly(new RoutingMessage(rm.sourceAddress, rm.destAddress, rm.object));
		} else {
			synchronized (idSendMap) {
				SendRecord sd = idSendMap.get(rm.id);
				if (sd != null && rm.sourceAddress.equals(sd.destination.getAddress()) && rm.isResponse()) {
					// ping response from pair
					idSendMap.remove(sd);

					LatencyRecord lr = new LatencyRecord();
					lr.route = sd.route;
					latencyRecords.put(p2p.getUser(rm.sourceAddress), lr);
				} else {
					P2PUser source = p2p.getUser(rm.sourceAddress);
					if(source != null && rm.object != null) {
						// handle and respond
						handler.incommingMessage(source.getBindedNPair(), rm.object);
						// reply pings
						sendRoutingMessage(source, rm.getResponse());
					}
				}
			}
		}
	}

	// Learning

	/**
	 * Record used to keep track of message sent and learn from them
	 *
	 */
	private class SendRecord {
		public P2PUser destination;
		public P2PUser route;
		public long sendTime;
	}

	private class LatencyRecord {
		public P2PUser route;
		public long latency;
	}
}
