package ptwop.p2p.routing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ptwop.common.Clock;
import ptwop.p2p.P2PUser;

public class LogRouter extends DumbRouter {

	private int msgCounter;

	// Learning
	// TODO : empty this map when message are old and have been lost
	private Map<Integer, SendRecord> idSendMap;
	private Map<P2PUser, Map<P2PUser, Integer>> latencyRecords;

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
		sendRoutingMessage(dest, null, rm);
	}

	private void sendRoutingMessage(P2PUser dest, P2PUser prec, RoutingMessage rm) throws IOException {
		P2PUser next = getRoute(dest);

		// destAddress = null if next is dest
		if (next.getAddress().equals(rm.destAddress))
			rm.destAddress = null;

		// add record and wait for log
		synchronized (idSendMap) {
			SendRecord sd = new SendRecord(dest, prec, next, rm.id, clock.getTime());
			idSendMap.put(msgCounter, sd);
			rm.id = msgCounter;
			msgCounter++;
		}

		// send routing message
		next.sendDirectly(rm);
	}

	@Override
	public void processRoutingMessage(P2PUser prec, RoutingMessage rm) throws IOException {
		// replace address if sender is user
		if (!rm.isForwarded())
			rm.sourceAddress = prec.getAddress();

		if (rm.isResponse()) {
			handleResponseMessage(prec, rm);
		} else if (rm.isToForward()) {
			// next : user to forward
			P2PUser next = p2p.getUser(rm.destAddress);
			if (next != null)
				sendRoutingMessage(next, prec, rm);
			else
				throw new IllegalArgumentException("Didn't find SendRecord with id " + rm.id);
		} else {
			// source : sender of routing message
			P2PUser source = p2p.getUser(rm.sourceAddress);
			if (source != null) {
				// handle message
				handler.incommingMessage(source.getBindedNPair(), rm.object);
				// send response
				prec.sendDirectly(rm.getResponse());
			} else
				throw new IllegalArgumentException("Didn't find source user with address " + rm.sourceAddress);
		}
	}

	private void handleResponseMessage(P2PUser prec, RoutingMessage rm) throws IOException {
		synchronized (idSendMap) {
			SendRecord sd = idSendMap.get(rm.id);
			if (sd != null) {
				// remove from send records
				idSendMap.remove(sd);

				// add value to map
				int latency = (int) (clock.getTime() - sd.sendTime);
				addObservedLatency(sd.destination, sd.next, latency);

				// forward response back
				if (sd.prec != null) {
					rm.id = sd.initId;
					if (sd.prec.getAddress().equals(rm.destAddress))
						rm.destAddress = null;
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

	private void addObservedLatency(P2PUser dest, P2PUser next, int latency) {
		if (!latencyRecords.containsKey(dest))
			latencyRecords.put(dest, new HashMap<>());
		Integer nextValue = latencyRecords.get(dest).get(next);
		if (nextValue != null)
			nextValue = (int) (nextValue * 0.75 + latency * 0.25);
		else
			latencyRecords.get(dest).put(next, new Integer(latency));
	}

	public int getObservedLatency(P2PUser dest, P2PUser next, int defaultValue) {
		Map<P2PUser, Integer> destMap = latencyRecords.get(dest);
		if (destMap != null) {
			Integer latency = destMap.get(next);
			if (latency != null)
				return latency;
		}
		return defaultValue;
	}
}
