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
		sendRoutingMessage(myselfPLaceOlder, null, getRoute(dest), dest, rm);
	}

	private void sendRoutingMessage(P2PUser source, P2PUser prec, P2PUser next, P2PUser dest, RoutingMessage rm)
			throws IOException {
		// sourceAddress = null if i'm source
		if (source.equals(myselfPLaceOlder)) {
			rm.sourceAddress = null;
			rm.forwarded = false;
		} else if (next.getAddress().equals(rm.sourceAddress)) {
			rm.sourceAddress = null;
			rm.forwarded = true;
		}

		// destAddress = null if next is dest
		if (next.getAddress().equals(rm.destAddress))
			rm.destAddress = null;

		// add record and wait for log
		SendRecord sd = new SendRecord(dest, prec, next, clock.getTime());
		addToSendRecords(source, rm.id, sd);

		// send routing message
		next.sendDirectly(rm);
	}

	@Override
	public void processRoutingMessage(P2PUser prec, RoutingMessage rm) throws IOException {
		P2PUser source = resolveSource(prec, rm);
		P2PUser dest = resolveDest(rm);
		if (source.equals(dest))
			throw new IllegalArgumentException("Weird message : source is destination");
		rm.sourceAddress = source.getAddress();
		rm.destAddress = dest.getAddress();

		if (rm.isResponse()) {
			SendRecord sd = removeFromSendRecords(dest, rm.id);
			if (sd == null)
				throw new IllegalArgumentException("Didn't find SendRecord from " + dest + " with id " + rm.id);
			// add value to map
			int latency = (int) (clock.getTime() - sd.sendTime);
			addObservedLatency(sd.destination, sd.next, latency);
			if (sd.prec != null)
				sendRoutingMessage(source, prec, sd.prec, dest, rm);
		} else if (rm.isToForward()) {
			if (dest == myselfPLaceOlder)
				throw new IllegalArgumentException("Can't forward message to myself");
			rm.forwarded = true;
			sendRoutingMessage(source, prec, getRoute(dest), dest, rm);
		} else {
			// handle message
			handler.incommingMessage(source.getBindedNPair(), rm.object);
			// send response
			RoutingMessage rmR = rm.getResponse();
			if (rmR.destAddress != null && rmR.destAddress.equals(prec.getAddress()))
				rmR.destAddress = null;
			prec.sendDirectly(rmR);
		}
	}

	private P2PUser resolveSource(P2PUser prec, RoutingMessage rm) {
		// source : sender of routing message
		P2PUser source = null;
		// replace address if sender is user
		if (!rm.isForwarded() && rm.sourceAddress == null) {
			source = prec;
		} else if (rm.isForwarded() && rm.sourceAddress == null) {
			source = myselfPLaceOlder;
		} else {
			source = p2p.getUser(rm.sourceAddress);
			if (source == null)
				throw new IllegalArgumentException("Didn't find source user with address " + rm.sourceAddress);
		}
		return source;
	}

	private P2PUser resolveDest(RoutingMessage rm) {
		P2PUser dest = null;
		if (rm.destAddress != null) {
			dest = p2p.getUser(rm.destAddress);
			if (dest == null)
				throw new IllegalArgumentException("Didn't find user with address " + rm.destAddress);
		} else {
			dest = myselfPLaceOlder;
		}
		return dest;
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
			throw new IllegalArgumentException("Source can't be null");
		if (!sendRecords.containsKey(source))
			sendRecords.put(source, new HashMap<>());
		// TODO must replace ?
		if (!sendRecords.get(source).containsKey(id))
			sendRecords.get(source).put(id, sd);
	}

	private SendRecord removeFromSendRecords(P2PUser source, Integer id) {
		if (source == null)
			throw new IllegalArgumentException("Source can't be null");
		Map<Integer, SendRecord> sourceMap = sendRecords.get(source);
		if (sourceMap != null) {
			SendRecord sd = sourceMap.remove(id);
			if (sourceMap.isEmpty())
				sendRecords.remove(sourceMap);
			return sd;
		}
		return null;
	}

	// TODO how to update ?
	private void addObservedLatency(P2PUser dest, P2PUser next, int latency) {
		if (!latencyRecords.containsKey(dest))
			latencyRecords.put(dest, new HashMap<>());
		Integer nextValue = latencyRecords.get(dest).get(next);
		if (nextValue != null)
			// nextValue = (int) (nextValue * 0.75 + latency * 0.25);
			nextValue = Math.min(nextValue, latency);
		else
			latencyRecords.get(dest).put(next, new Integer(latency));
	}

	// TODO optimize this
	public Integer getSendRecordsLatency(P2PUser dest, P2PUser next) {
		for (P2PUser u : sendRecords.keySet()) {
			Map<Integer, SendRecord> destMap = sendRecords.get(u);
			if (destMap != null) {
				for (Integer i : destMap.keySet()) {
					SendRecord sd = destMap.get(i);
					if (sd.destination.equals(dest) && sd.next.equals(next)) {
						Integer lat = (int) (clock.getTime() - destMap.get(i).sendTime);
						System.out.println("Found value " + lat);
						return lat;
					}
				}
			}
		}
		return null;
	}

	public Integer getObservedLatency(P2PUser dest, P2PUser next) {
		Map<P2PUser, Integer> destMap = latencyRecords.get(dest);
		if (destMap != null)
			return destMap.get(next);
		return null;
	}
}
