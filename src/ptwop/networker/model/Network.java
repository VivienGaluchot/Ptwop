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
	 * Create a random network
	 * 
	 * @param n
	 *            number of nodes
	 * @param nodeLatency
	 *            random generator for node latency
	 * @param connex
	 *            medium connexity
	 * @param linkLatency
	 *            random generator for link latency
	 * @param linkLoss
	 *            random generator for link loss
	 * @param linkPacketSize
	 *            random generator for link packet size max
	 */
	public void randomize(int n, GaussianRandom nodeLatency, float connex, GaussianRandom linkLatency,
			GaussianRandom linkLoss, GaussianRandom linkPacketSize) {
		nodes.clear();
		time = 0;

		float probLink = connex / (n - 1);

		Random random = new Random();
		
		int nl = 0;

		for (int i = 0; i < n; i++) {
			long latency = Math.round(nodeLatency.nextDouble());
			Node node = new Node(this, latency);

			// linking
			for (Node prevNode : nodes) {
				if (random.nextFloat() < probLink) {
					new DualLink(this, node, prevNode, linkLatency.nextLong(), linkLoss.nextFloat(),
							linkPacketSize.nextInt());
					nl++;
				}
			}

			addNode(node);
		}
		
		System.out.println("true connexity "+ ((float) 2*nl)/(n));

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
