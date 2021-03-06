package ptwop.simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptwop.common.Clock;
import ptwop.common.math.GaussianRandom;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2PUser;
import ptwop.simulator.DataTracker;
import ptwop.simulator.display.NetworkWrapper;

public class Network implements Steppable {

	private long time;

	private ArrayList<Node> nodes;
	private Map<Node, P2P> p2ps;

	private GaussianRandom packetSize;
	private GaussianRandom latency;
	private GaussianRandom loss;

	private NetworkWrapper wrapper;

	private P2PCreator creator;

	/* BENCHMARK */
	public boolean track = false;
	public DataTracker<Integer> nodeNumberTracker = new DataTracker<>();
	public DataTracker<Integer> totalBandwithUsed = new DataTracker<>();

	public Network(P2PCreator creator) {
		this.creator = creator;
		nodes = new ArrayList<>();
		p2ps = new HashMap<>();
		time = 0;
		packetSize = null;
		latency = null;
		loss = null;
		wrapper = null;
	}

	public Clock getClock() {
		return new Clock() {
			@Override
			public long getTime() {
				return time;
			}
		};
	}

	// Wrapper signal
	public void setWrapper(NetworkWrapper wrapper) {
		this.wrapper = wrapper;
	}
	
	public void signalNewNode(Node n) {
		if (wrapper != null)
			wrapper.addNode(n);
	}
	
	public void signalRemovedNode(Node n) {
		if (wrapper != null)
			wrapper.removeNode(n);
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

	public synchronized int numberOfNodes() {
		return nodes.size();
	}

	public synchronized void addNode(Node n) {
		n.setId(nodes.size());
		nodes.add(n);
		signalNewNode(n);

		P2P p2p = creator.createAndStartP2P(n);
		p2p.getRouter().setClock(getClock());
		p2ps.put(n, p2p);
		p2p.setP2PHandler(new P2PHandler() {
			@Override
			public void handleMessage(P2PUser sender, Object o) {
				// System.out.println("Node " + n + " | received from " + sender
				// + " : " + o);
			}

			@Override
			public void handleConnection(P2PUser user) {
				// System.out.println("Node " + n + " | connected to " + user);
			}

			@Override
			public void handleUserUpdate(P2PUser user) {
				// System.out.println("Node " + n + " | update from " + user);
			}

			@Override
			public void handleUserDisconnect(P2PUser user) {
				// System.out.println("Node " + n + " | disconnected from " +
				// user);
			}
		});
	}

	public synchronized Node getNode(int i) {
		return nodes.get(i);
	}

	public Node getNode(NetworkerNAddress address) {
		return getNode(address.id);
	}

	public synchronized List<Node> getNodes() {
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

		/* BENCHMARK */
		if (track) {
			nodeNumberTracker.addData(nodes.size());
			int res = 0;
			for (Node n : getNodes()) {
				for (Link l : n.getLinks()) {
					res += l.getNumberOfTransitingElements();
				}
			}
			totalBandwithUsed.addData(res);
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

	public void setRandomizers(GaussianRandom latency, GaussianRandom loss, GaussianRandom packetSize) {
		this.latency = latency;
		this.loss = loss;
		this.packetSize = packetSize;
	}

	public GaussianRandom getLatencyRandomizer() {
		return latency;
	}

	public GaussianRandom getLossRandomizer() {
		return loss;
	}

	public GaussianRandom getPacketSizeRandomizer() {
		return packetSize;
	}

	public void addNewNodes(int n) {
		for (int i = 0; i < n; i++) {
			addNewNode();
		}
	}

	public Node addNewNode() {
		Node node = new Node(this);
		addNode(node);
		return node;
	}

	public synchronized void setToFullyInterconnected() {
		for (int i = 0; i < nodes.size(); i++) {
			Node ni = getNode(i);
			for (int j = i + 1; j < nodes.size(); j++) {
				Node nj = getNode(j);
				// interconnect nodes ni and nj
				Link li = new Link(this, null, ni, nj);
				li.setEstablished(true);
				ni.addLink(li);
				Link lj = new Link(this, li, nj, ni);
				li.setPairLink(lj);
				lj.setEstablished(true);
				nj.addLink(lj);
				ni.handleConnectionFrom(li);
				nj.handleConnectionTo(lj);

				for (Link l : nj.getLinks())
					l.clearBuffers();
				for (Link l : ni.getLinks())
					l.clearBuffers();
			}
		}
	}
}
