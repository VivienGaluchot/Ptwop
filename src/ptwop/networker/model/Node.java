package ptwop.networker.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptwop.network.NAddress;
import ptwop.network.NManager;

public class Node extends NManager implements Steppable {
	private Network net;

	private int id;
	private ArrayList<Link> links;
	private Map<Node, Link> routingMap;

	public Node(Network net) {
		this.net = net;
		this.setId(0);

		links = new ArrayList<>();
		routingMap = new HashMap<>();
	}

	public void connectedTo(Link link) {
		links.add(link);
		routingMap.put(link.getDestNode(), link);
		handler.connectedTo(link);
	}

	public void addLink(Link link) {
		links.add(link);
		routingMap.put(link.getDestNode(), link);
		handler.newUser(link);
	}

	public List<Link> getLinks() {
		return Collections.unmodifiableList(links);
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
		handler.newMessage(routingMap.get(source), data.data);
	}

	@Override
	public void doTimeStep() {
		for (Link l : links) {
			l.doTimeStep();
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	// NManager

	@Override
	public void start() {
		System.out.println("Starting node " + this);
	}

	@Override
	public NAddress getMyAddress() {
		return new NetworkerNAddress(this);
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		System.out.println("Connecting " + this + " to " + address);
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
