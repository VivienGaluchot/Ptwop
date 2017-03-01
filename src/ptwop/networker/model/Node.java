package ptwop.networker.model;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.network.NAddress;
import ptwop.network.NPair;
import ptwop.network.NServent;
import ptwop.networker.DataTracker;

public class Node extends NServent implements Steppable {
	private Network net;

	private int id;
	private BiMap<Node, Link> linkMap;

	/* BENCHMARK */
	public boolean track = false;
	public DataTracker<Integer> linkNumberTracker = new DataTracker<>();
	public DataTracker<Integer> totalBandwithUsed = new DataTracker<>();

	public Node(Network net) {
		this.net = net;
		this.setId(0);

		linkMap = HashBiMap.create();
	}

	public synchronized void addLink(Link link) {
		if (linkMap.containsValue(link))
			throw new IllegalArgumentException("Link already present : " + link);
		net.signalNewLink(link);
		linkMap.put(link.getDestNode(), link);
	}

	public synchronized void removeLink(Link link) {
		net.signalRemovedLink(link);
		if (linkMap.inverse().remove(link) == null)
			throw new IllegalArgumentException("Can't remove unconnected link : " + link);
		pairQuit(link);
	}

	public synchronized void removeLinkTo(Node node) {
		Link link = linkMap.get(node);
		removeLink(link);
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

	public synchronized void handleData(Node source, Data data) {
		if (!linkMap.containsKey(source))
			addLink(new Link(net, this, source));
		incommingMessage(linkMap.get(source), data.data);
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
		return o instanceof Node && ((Node) o).id == id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	// NServent

	@Override
	public void start() {
		// System.out.println("Starting node " + this);
	}

	@Override
	public NAddress getAddress() {
		return new NetworkerNAddress(this);
	}

	@Override
	public void incommingMessage(NPair user, Object o) {
		if (o instanceof DataTCP) {
			DataTCP m = (DataTCP) o;
			Link l = (Link) user;
			if (m.isSyn()) {
				l.sendSynAck();
			} else if (m.isSynAck()) {
				l.sendAck();
				l.setEstablished(true);
				if (!isConnectedTo(user.getAddress()))
					super.connectedTo(user);
			} else if (m.isAck()) {
				l.setEstablished(true);
				if (!isConnectedTo(user.getAddress()))
					super.incommingConnectionFrom(user);
			}
		} else
			super.incommingMessage(user, o);
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		if (super.isConnectedTo(address))
			return;

		if (address instanceof NetworkerNAddress) {
			Node n = net.getNode((NetworkerNAddress) address);
			if (n == null)
				throw new IOException("Address unreachable");

			Link l = new Link(net, this, n);
			if (!linkMap.containsValue(l)) {
				addLink(l);
				l.sendSyn();
			}
		} else {
			System.out.println("Wrong address");
		}
	}
}
