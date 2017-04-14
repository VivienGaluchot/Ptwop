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
	private Map<P2PUser, Map<Integer, SendRecord>> sendRecords;
	private Map<P2PUser, Map<P2PUser, Integer>> latencyRecords;
	private P2PUser myselfPLaceOlder;

	public LogRouter() {
		this(null);
	}

	public LogRouter(Clock clock) {
		super(clock);
		msgCounter = 0;
		sendRecords = new HashMap<>();
		latencyRecords = new HashMap<>();
		myselfPLaceOlder = new P2PUser("Myself", null);
	}

	@Override
	public String toString() {
		return "LogRouter";
	}

	@Override
	public void routeTo(P2PUser dest, Object msg) throws IOException {
		// new routing message, sender = null because is me
		RoutingMessage rm = new RoutingMessage(null, dest.getAddress(), msg);
		rm.id = msgCounter;
		msgCounter++;
		sendRoutingMessage(myselfPLaceOlder, dest, null, rm);
	}

	private void sendRoutingMessage(P2PUser source, P2PUser dest, P2PUser prec, RoutingMessage rm) throws IOException {
		P2PUser next = getRoute(dest);
		
		// sourceAddress = null if i'm source
		if (source.equals(myselfPLaceOlder))
			rm.sourceAddress = null;
		
		// destAddress = null if next is dest
		if (next.getAddress().equals(rm.destAddress))
			rm.destAddress = null;

		// add record and wait for log
		synchronized (sendRecords) {
			SendRecord sd = new SendRecord(dest, prec, next, clock.getTime());
			addToSendRecords(source, rm.id, sd);
		}

		// send routing message
		next.sendDirectly(rm);
	}

	@Override
	public void processRoutingMessage(P2PUser prec, RoutingMessage rm) throws IOException {
		// source : sender of routing message
		P2PUser source = null;
		// replace address if sender is user
		if (rm.sourceAddress == null) {
			rm.sourceAddress = prec.getAddress();
			source = prec;
		} else {
			source = p2p.getUser(rm.sourceAddress);
		}

		if (rm.isResponse()) {
			// source is original source, or response's destAddress
			handleResponseMessage(p2p.getUser(rm.destAddress), prec, rm);
		} else if (rm.isToForward()) {
			// next : user to forward
			P2PUser next = p2p.getUser(rm.destAddress);
			if (next != null)
				sendRoutingMessage(source, next, prec, rm);
			else
				throw new IllegalArgumentException("Didn't find user with address " + rm.destAddress);
		} else {
			if (source != null) {
				// handle message
				handler.incommingMessage(source.getBindedNPair(), rm.object);
				// send response
				RoutingMessage rmR = rm.getResponse();
				if (rmR.destAddress.equals(prec.getAddress()))
					rmR.destAddress = null;
				prec.sendDirectly(rmR);
			} else
				throw new IllegalArgumentException("Didn't find source user with address " + rm.sourceAddress);
		}
	}

	private void handleResponseMessage(P2PUser originalSource, P2PUser prec, RoutingMessage rm) throws IOException {
		synchronized (sendRecords) {
			// get and remove from send records
			SendRecord sd = removeFromSendRecords(originalSource, rm.id);
			if (sd != null) {
				// add value to map
				int latency = (int) (clock.getTime() - sd.sendTime);
				addObservedLatency(sd.destination, sd.next, latency);

				// forward response back
				if (sd.prec != null) {
					if (sd.prec.getAddress().equals(rm.sourceAddress))
						rm.sourceAddress = null;
					if (sd.prec.getAddress().equals(rm.destAddress))
						rm.destAddress = null;
					sd.prec.sendDirectly(rm);
				}
			} else {
				throw new IllegalArgumentException(
						"Didn't find SendRecord from " + originalSource + " with id " + rm.id);
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

		public SendRecord(P2PUser destination, P2PUser prec, P2PUser next, long sendTime) {
			this.destination = destination;
			this.prec = prec;
			this.next = next;
			this.sendTime = sendTime;
		}
	}

	private void addToSendRecords(P2PUser source, Integer id, SendRecord sd) {
		if (source == null)
			source = myselfPLaceOlder;
		if (!sendRecords.containsKey(source))
			sendRecords.put(source, new HashMap<>());
		// TODO must replace ?
		if (!sendRecords.get(source).containsKey(id))
			sendRecords.get(source).put(id, sd);
	}

	private SendRecord removeFromSendRecords(P2PUser source, Integer id) {
		if (source == null)
			source = myselfPLaceOlder;
		Map<Integer, SendRecord> sourceMap = sendRecords.get(source);
		if (sourceMap != null) {
			SendRecord sd = sourceMap.remove(id);
			if (sourceMap.isEmpty())
				sendRecords.remove(sourceMap);
			return sd;
		}
		return null;
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

	public Integer getObservedLatency(P2PUser dest, P2PUser next) {
		// TODO read records to find if the message have already been sent to
		// this next and avoid buckle
		Map<P2PUser, Integer> destMap = latencyRecords.get(dest);
		if (destMap != null)
			return destMap.get(next);
		return null;
	}
}
