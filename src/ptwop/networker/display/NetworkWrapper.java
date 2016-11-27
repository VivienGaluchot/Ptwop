package ptwop.networker.display;

import java.awt.Graphics;
import java.util.HashMap;

import ptwop.common.Animable;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;

public class NetworkWrapper implements Animable {

	private Network network;
	private HashMap<Node, NodeWrapper> nodes;

	public NetworkWrapper(Network network) {
		this.network = network;
		nodes = new HashMap<>();
		for (Node n : network.getNodes())
			addNode(n);
	}

	private void addNode(Node n) {
		nodes.put(n, new NodeWrapper(n, this));
	}

	public NodeWrapper getWrapper(Node n) {
		return nodes.get(n);
	}

	@Override
	public void paint(Graphics g) {
		long time = network.getTime();
		g.drawString("time " + time, -12, -12);
		for (Node n : nodes.keySet())
			nodes.get(n).paint(g);
	}

	@Override
	public void animate(long timeStep) {
		for (Node n : nodes.keySet())
			nodes.get(n).animate(timeStep);
	}

}
