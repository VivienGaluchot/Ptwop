package ptwop.networker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ptwop.common.math.GaussianRandom;

public class Network implements Steppable {

	private long time;

	private ArrayList<Node> nodes;

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
	}

	public Node getNode(int i) {
		return nodes.get(i);
	}

	public List<Node> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public void initBellmanFord() {
		for (Node n : nodes)
			n.startBellmanFord();
	}

	/**
	 * @param n
	 *            number of nodes
	 */
	public void randomize(int n, GaussianRandom nodeLatency, float probLink, GaussianRandom linkLatency,
			GaussianRandom linkLoss, GaussianRandom linkPacketSize) {
		nodes.clear();
		time = 0;

		Random random = new Random();

		for (int i = 0; i < n; i++) {
			long latency = Math.round(nodeLatency.nextDouble());
			Node node = new Node(this, latency);

			// linking
			for (Node prevNode : nodes) {
				if (random.nextFloat() < probLink) {
					new DualLink(this, node, prevNode, linkLatency.nextLong(), linkLoss.nextFloat(),
							linkPacketSize.nextInt());
				}
			}

			addNode(node);
		}

	}

	@Override
	public void doTimeStep() {
		time++;
		// TODO generate data ?

		for (Node n : nodes) {
			n.doTimeStep();
		}
	}
}
