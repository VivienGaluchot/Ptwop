package ptwop.networker.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptwop.network.NAddress;
import ptwop.network.NPair;
import ptwop.network.NServent;
import ptwop.networker.DataTracker;

public class Node extends NServent implements Steppable {
	private Network net;

	private int id;
	private ArrayList<Link> links;
	private Map<Node, Link> linkMap;

	/* BENCHMARK */
	public boolean track = false;
	public DataTracker<Integer> linkNumberTracker = new DataTracker<>();
	public DataTracker<Integer> totalBandwithUsed = new DataTracker<>();

	public Node(Network net) {
		this.net = net;
		this.setId(0);

		links = new ArrayList<>();
		linkMap = new HashMap<>();
	}

	public void addLink(Link link) {
		if (links.contains(link)) {
			// System.out.println("already connected to " + link);
			return;
		}

		links.add(link);
		net.signalNewLink(link);
		linkMap.put(link.getDestNode(), link);
	}

	public void removeLink(Link link) {
		links.remove(link);
		linkMap.remove(link);
		pairQuit(link);
		net.signalRemovedLink(link);
	}

	public void removeLinkTo(Node node) {
		Link link = linkMap.get(node);
		removeLink(link);
	}

	public List<Link> getLinks() {
		return Collections.unmodifiableList(links);
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

	public void handleData(Node source, Data data) {
		if (!linkMap.containsKey(source))
			addLink(new Link(net, this, source));
		incommingMessage(linkMap.get(source), data.data);
	}

	@Override
	public void doTimeStep() {
		for (Link l : links) {
			l.doTimeStep();
		}

		/* BENCHMARK */
		if (track) {
			linkNumberTracker.addData(links.size());
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
			try {
				DataTCP m = (DataTCP) o;
				Link l = (Link) user;
				if (m.isSyn()) {
					l.send(new DataTCP(DataTCP.Type.ACK));
				} else if (m.isAck()) {
					l.send(new DataTCP(DataTCP.Type.SYNACK));
					l.setEstablished(true);
					super.connectedTo(user);
				} else if (m.isSynAck()) {
					l.setEstablished(true);
					super.incommingConnectionFrom(user);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			super.incommingMessage(user, o);
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		// System.out.println("Connecting " + this + " to " + address);
		if (address instanceof NetworkerNAddress) {
			Node n = net.getNode((NetworkerNAddress) address);
			if (n == null)
				throw new IOException("Address unreachable");

			Link l = new Link(net, this, n);
			addLink(l);
			l.send(new DataTCP(DataTCP.Type.SYN));
			// n.addLink(new Link(net, n, this));
		} else {
			System.out.println("Wrong address");
		}
	}
}
