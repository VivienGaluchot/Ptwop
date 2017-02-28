package ptwop.networker.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptwop.network.NAddress;
import ptwop.network.NManager;
import ptwop.networker.DataTracker;

public class Node extends NManager implements Steppable {
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

	/**
	 * Function used to establish a link connection. Called when the connecting
	 * link have send his TCP syn message
	 * 
	 * @param n
	 */
	public void signalInitTCP(Node n) {
		linkMap.get(n).replyTCP();
	}
	
	public void signalACK(Node n) {
		linkMap.get(n).signalAck();
	}

	public void linkConnectedTo(Link link) {
		if (links.contains(link)) {
			// System.out.println("already connected to " + link);
			return;
		}

		links.add(link);
		net.signalNewLink(link);
		linkMap.put(link.getDestNode(), link);
		super.connectedTo(link);
	}

	public void addLink(Link link) {
		if (links.contains(link)) {
			// System.out.println("already connected to " + link);
			return;
		}

		links.add(link);
		net.signalNewLink(link);
		linkMap.put(link.getDestNode(), link);
		super.newUser(link);
	}

	public void removeLink(Link link) {
		links.remove(link);
		linkMap.remove(link);
		userQuit(link);
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
		newMessage(linkMap.get(source), data.data);
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

	// NManager

	@Override
	public void start() {
		// System.out.println("Starting node " + this);
	}

	@Override
	public NAddress getAddress() {
		return new NetworkerNAddress(this);
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		// System.out.println("Connecting " + this + " to " + address);
		if (address instanceof NetworkerNAddress) {
			Node n = net.getNode((NetworkerNAddress) address);
			if (n == null)
				throw new IOException("Address unreachable");
			net.connectMeTo(this, n);
		} else {
			System.out.println("Wrong address");
		}
	}
}
