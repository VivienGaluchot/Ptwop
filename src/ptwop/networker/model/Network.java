package ptwop.networker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	@Override
	public void doTimeStep() {
		time++;
		// TODO generate data ?

		for (Node n : nodes) {
			n.doTimeStep();
		}
	}
}
