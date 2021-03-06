package ptwop.simulator.model;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.network.NAddress;
import ptwop.network.NPair;
import ptwop.network.NServent;
import ptwop.simulator.Data2DTracker;
import ptwop.simulator.DataTracker;

public class Node extends NServent implements Steppable {
	private Network net;

	private int id;
	private BiMap<Node, Link> linkMap;

	/* BENCHMARK */
	public boolean track = false;
	public Data2DTracker<Integer, Long> sizeVsTimeToReceive = new Data2DTracker<>();
	public Data2DTracker<Integer, Long> idVsTimeToReceive = new Data2DTracker<>();
	public DataTracker<Integer> linkNumberTracker = new DataTracker<>();
	public DataTracker<Integer> totalBandwithUsed = new DataTracker<>();

	public Node(Network net) {
		this.net = net;
		this.setId(0);

		linkMap = HashBiMap.create();
	}

	public void addLink(Link link) {
		if (linkMap.containsKey(link.getDestNode()))
			throw new IllegalArgumentException(
					"Link " + link + " - destination already present : " + link.getDestNode());
		if (linkMap.containsValue(link))
			throw new IllegalArgumentException("Link already present : " + link);
		linkMap.put(link.getDestNode(), link);
		net.signalNewLink(link);
	}

	public void disconnect(Link link) {
		if (linkMap.inverse().remove(link) == null)
			throw new IllegalArgumentException("Can't remove unconnected link : " + link);
		handleConnectionClosed(link);
		net.signalRemovedLink(link);
	}

	public void removeLinkTo(Node node) {
		Link link = linkMap.get(node);
		disconnect(link);
	}

	public Link getLinkTo(Node n) {
		return linkMap.get(n);
	}

	public Set<Link> getLinks() {
		return Collections.unmodifiableSet(linkMap.inverse().keySet());
	}

	public Network getNetwork() {
		return net;
	}

	public String getName() {
		return "n" + id;
	}

	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	// TODO optimize, hot method according mission control
	public void handleData(Link emitter, Data data) {
		if (!linkMap.containsKey(emitter.getSourceNode())) {
			Link l = new Link(net, emitter, this, emitter.getSourceNode());
			addLink(l);
			emitter.setPairLink(l);
		} else if (emitter.getPairLink() == null) {
			emitter.setPairLink(linkMap.get(emitter.getSourceNode()));
		}

		// Check if data is benchmarkdata and for me
		if (track && data.isLastPart() && data.benchmarkData != null && data.destinationReached) {
			sizeVsTimeToReceive.addData(data.getSize(), data.getEllapsedTime(net.getTime()));
			idVsTimeToReceive.addData(data.benchmarkData.id, data.getEllapsedTime(net.getTime()));
		}

		handleIncomingMessage(emitter.getPairLink(), data.object);
	}

	@Override
	public void doTimeStep() {
		for (Link l : linkMap.inverse().keySet()) {
			l.doTimeStep();
		}

		/* BENCHMARK */
		if (track) {
			linkNumberTracker.addData(linkMap.size());
			int res = 0;
			for (Link l : getLinks()) {
				res += l.getNumberOfTransitingElements();
			}
			totalBandwithUsed.addData(res);
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Node) && (((Node) o).id == id);
	}

	@Override
	public int hashCode() {
		return id;
	}

	// NServent

	@Override
	public void start() {

	}

	@Override
	public NAddress getAddress() {
		return new NetworkerNAddress(this);
	}

	@Override
	public void handleIncomingMessage(NPair user, Object o) {
		Link l = (Link) user;

		if (o instanceof DataTCP) {
			DataTCP m = (DataTCP) o;
			if (m.isSyn()) {
				l.sendSynAck();
			} else if (m.isSynAck()) {
				l.sendAck();
				l.setEstablished(true);
				if (!isConnectedTo(user.getAddress()))
					super.handleConnectionTo(user);
			} else if (m.isAck()) {
				l.setEstablished(true);
				if (!isConnectedTo(user.getAddress()))
					super.handleConnectionFrom(user);
			}
		} else {
			super.handleIncomingMessage(l, o);
		}
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		if (super.isConnectedTo(address))
			return;

		if (address instanceof NetworkerNAddress) {
			Node n = net.getNode((NetworkerNAddress) address);
			if (n == null)
				throw new IOException("Address unreachable");

			Link l = new Link(net, null, this, n);
			if (!linkMap.containsValue(l)) {
				addLink(l);
				l.sendSyn();
			}
		} else {
			System.out.println("Wrong address");
		}
	}
}
