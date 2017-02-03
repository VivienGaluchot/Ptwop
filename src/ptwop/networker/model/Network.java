package ptwop.networker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ptwop.common.math.GaussianRandom;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2PUser;
import ptwop.p2p.v0.Flood;

public class Network implements Steppable {

	private long time;

	private ArrayList<Node> nodes;

	private GaussianRandom packetSize;
	private GaussianRandom latency;
	private GaussianRandom loss;

	public Network() {
		nodes = new ArrayList<>();
		time = 0;
	}

	public long getTime() {
		return time;
	}

	public int numberOfNodes() {
		return nodes.size();
	}

	public void addNode(Node n) {
		n.setId(nodes.size());
		nodes.add(n);

		P2P p2p = new Flood(n, n.getName());
		p2p.setMessageHandler(new P2PHandler() {
			@Override
			public void handleMessage(P2PUser sender, Object o) {
				System.out.println("Node " + n + " | received from " + sender + " : " + o);
			}

			@Override
			public void userConnect(P2PUser user) {
				System.out.println("Node " + n + " | connected to " + user);
			}

			@Override
			public void userUpdate(P2PUser user) {
				System.out.println("Node " + n + " | update from " + user);
			}

			@Override
			public void userDisconnect(P2PUser user) {
				System.out.println("Node " + n + " | disconnected from " + user);
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

	@Override
	public void doTimeStep() {
		time++;
		// TODO generate data ?

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
		me.connectedTo(new Link(this, me, dest, latency.nextLong(), loss.nextFloat(), packetSize.nextInt()));
		dest.addLink(new Link(this, dest, me, latency.nextLong(), loss.nextFloat(), packetSize.nextInt()));
	}
}
