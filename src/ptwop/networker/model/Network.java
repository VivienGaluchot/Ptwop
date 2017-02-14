package ptwop.networker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptwop.common.math.GaussianRandom;
import ptwop.networker.display.NetworkWrapper;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2PUser;

public class Network implements Steppable {

	private long time;

	private ArrayList<Node> nodes;
	private Map<Node, P2P> p2ps;

	private GaussianRandom packetSize;
	private GaussianRandom latency;
	private GaussianRandom loss;

	private NetworkWrapper wrapper;

	private P2PCreator creator;

	public Network(P2PCreator creator) {
		this.creator = creator;
		nodes = new ArrayList<>();
		p2ps = new HashMap<>();
		time = 0;
		wrapper = null;
	}

	// Wrapper signal
	public void setWrapper(NetworkWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public void signalNewLink(Link l) {
		if (wrapper != null)
			wrapper.addLink(l);
	}

	public void signalRemovedLink(Link l) {
		if (wrapper != null)
			wrapper.removeLink(l);
	}

	public void signalNewData(TimedData d, Link l) {
		if (wrapper != null)
			wrapper.addData(d, l);
	}

	public void signalRemovedData(TimedData d) {
		if (wrapper != null)
			wrapper.removeData(d);
	}
	// end wrapper signal

	public long getTime() {
		return time;
	}

	public int numberOfNodes() {
		return nodes.size();
	}

	public void addNode(Node n) {
		n.setId(nodes.size());
		nodes.add(n);

		P2P p2p = creator.createP2P(n);
		p2ps.put(n, p2p);
		p2p.setMessageHandler(new P2PHandler() {
			@Override
			public void handleMessage(P2PUser sender, Object o) {
				System.out.println("Node " + n + " | received from " + sender + " : " + o);
			}

			@Override
			public void userConnect(P2PUser user) {
				// System.out.println("Node " + n + " | connected to " + user);
			}

			@Override
			public void userUpdate(P2PUser user) {
				// System.out.println("Node " + n + " | update from " + user);
			}

			@Override
			public void userDisconnect(P2PUser user) {
				// System.out.println("Node " + n + " | disconnected from " +
				// user);
			}
		});
		p2p.start();
	}

	public Node getNode(int i) {
		return nodes.get(i);
	}

	public Node getNode(NetworkerNAddress address) {
		return getNode(address.id);
	}

	public List<Node> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public P2P getP2P(Node n) {
		return p2ps.get(n);
	}

	@Override
	public synchronized void doTimeStep() {
		time++;
		for (Node n : nodes) {
			n.doTimeStep();
		}
	}

	/**
	 * Create a random network
	 * 
	 * @param n
	 *            number of nodes
	 * @param latency
	 *            random generator for link latency
	 * @param loss
	 *            random generator for link loss
	 * @param packetSize
	 *            random generator for link packet size max
	 */
	public void randomize(int n, GaussianRandom latency, GaussianRandom loss, GaussianRandom packetSize) {
		nodes.clear();
		p2ps.clear();
		time = 0;

		this.latency = latency;
		this.loss = loss;
		this.packetSize = packetSize;

		for (int i = 0; i < n; i++) {
			Node node = new Node(this);
			addNode(node);
		}
	}

	public void connectMeTo(Node me, Node dest) {
		me.linkConnectedTo(new Link(this, me, dest, latency.nextLong(), loss.nextFloat(), packetSize.nextInt()));
		dest.addLink(new Link(this, dest, me, latency.nextLong(), loss.nextFloat(), packetSize.nextInt()));
	}
}
