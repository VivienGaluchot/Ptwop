package ptwop.p2p.routing;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ptwop.common.Clock;
import ptwop.p2p.P2PUser;

public class LogRouter extends DumbRouter {

	private int msgCounter;

	// Learning

	private Map<Integer, SendRecord> idSendMap;
	private Map<P2PUser, Set<LatencyRecord>> latencyRecords;

	public LogRouter() {
		this(null);
	}

	public LogRouter(Clock clock) {
		super(clock);
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
		sendRoutingMessage(new RoutingMessage(null, dest.getAddress(), msg));
	}

	private void sendRoutingMessage(RoutingMessage rm) throws IOException {
		P2PUser next = getRoute(p2p.getUser(rm.destAddress));

		// dest = null if next is dest
		if (next.getAddress().equals(rm.destAddress))
			rm.destAddress = null;

		// add record and wait for log
		addToRecordAndSend(null, rm, next);
	}

	public void addToRecordAndSend(P2PUser user, RoutingMessage rm, P2PUser next) throws IOException {
		synchronized (idSendMap) {
			SendRecord sd = new SendRecord(p2p.getUser(rm.destAddress), user, next, rm.id, clock.getTime());
			idSendMap.put(msgCounter, sd);
			rm.id = msgCounter;
			msgCounter++;
		}
		next.sendDirectly(rm);
	}

	public void handleResponseMessage(P2PUser user, RoutingMessage rm) throws IOException {
		synchronized (idSendMap) {
			SendRecord sd = idSendMap.get(rm.id);
			if (sd != null) {
				// remove from send records
				idSendMap.remove(sd);

				// convert into latency Record
				LatencyRecord lr = new LatencyRecord(sd.next, (int) (clock.getTime() - sd.sendTime));
				if (!latencyRecords.containsKey(sd.destination))
					latencyRecords.put(sd.destination, new HashSet<>());
				latencyRecords.get(sd.destination).add(lr);

				// forward response back
				if (sd.prec != null) {
					rm.id = sd.initId;
					sd.prec.sendDirectly(rm);
				}
			} else {
				throw new IllegalArgumentException("Didn't find SendRecord with id " + rm.id);
			}
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

					LatencyRecord lr = new LatencyRecord(sd.route, (int) (clock.getTime() - sd.sendTime));
					if (!latencyRecords.containsKey(sd.destination))
						latencyRecords.put(sd.destination, new HashSet<>());
					latencyRecords.get(sd.destination).add(lr);
				} else {
					P2PUser source = p2p.getUser(rm.sourceAddress);
					if (source != null && rm.object != null) {
						// handle and respond
						handler.incommingMessage(source.getBindedNPair(), rm.object);
						// reply pings
						sendNewRoutingMessage(source, rm.getResponse());
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
		public P2PUser prec;
		public P2PUser next;
		public long sendTime;
		public int initId;

		public SendRecord(P2PUser destination, P2PUser prec, P2PUser next, int initId, long sendTime) {
			this.destination = destination;
			this.prec = prec;
			this.next = next;
			this.initId = initId;
			this.sendTime = sendTime;
		}
	}

	public Set<LatencyRecord> getLarencyRecords(P2PUser destination) {
		return latencyRecords.get(destination);
	}

	public int getObservedLatency(P2PUser destination, P2PUser route, int defaultValue) {
		Set<LatencyRecord> lrs = latencyRecords.get(destination);
		if (lrs != null) {
			for (LatencyRecord lr : lrs) {
				if (lr.route == route)
					return lr.latency;
			}
		}
		return defaultValue;
	}
}
