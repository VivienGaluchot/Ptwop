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
		// new routing message, sender = null because is me
		RoutingMessage rm = new RoutingMessage(null, dest.getAddress(), msg);

		P2PUser next = getRoute(dest);

		// destAddress = null if next is dest
		if (next.getAddress().equals(rm.destAddress))
			rm.destAddress = null;

		// add record and wait for log
		synchronized (idSendMap) {
			SendRecord sd = new SendRecord(dest, null, next, rm.id, clock.getTime());
			idSendMap.put(msgCounter, sd);
			rm.id = msgCounter;
			msgCounter++;
		}

		// send routing message
		next.sendDirectly(rm);
	}

	@Override
	public void processRoutingMessage(P2PUser user, RoutingMessage rm) throws IOException {
		// replace address if sender is user
		if (rm.isFromDirectSender())
			rm.sourceAddress = user.getAddress();

		if (rm.isResponse()) {
			handleResponseMessage(user, rm);
		} else if (rm.isToForward()) {
			// next : user to forward
			P2PUser next = p2p.getUser(rm.destAddress);
			if (next != null)
				routeTo(next, rm.object);
			else
				throw new IllegalArgumentException("Didn't find SendRecord with id " + rm.id);
		} else {
			// source : sender of routing message
			P2PUser source = p2p.getUser(rm.sourceAddress);
			if (source != null) {
				// handle message
				handler.incommingMessage(source.getBindedNPair(), rm.object);
				// send response
				user.sendDirectly(rm.getResponse());
			}
			else
				throw new IllegalArgumentException("Didn't find source user with address " + rm.sourceAddress);
		}
	}

	private void handleResponseMessage(P2PUser user, RoutingMessage rm) throws IOException {
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

	/**
	 * Record used to keep track of message sent and learn from them
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
