package ptwop.networker;

import ptwop.common.gui.AnimationPanel;
import ptwop.common.gui.Frame;
import ptwop.common.gui.SpaceTransform;
import ptwop.networker.display.NetworkWrapper;
import ptwop.networker.model.Link;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;

public class NetWorker {
	public static void main(String[] args) {
		SpaceTransform spaceTransform = new SpaceTransform();
		AnimationPanel mainPanel = new AnimationPanel(spaceTransform);
		spaceTransform.setFather(mainPanel);

		Network net = new Network();
		Node n0 = new Node(net, 0, "n0", 1);
		Node n1 = new Node(net, 0, "n1", 1);
		Node n2 = new Node(net, 0, "n2", 1);
		Node n3 = new Node(net, 0, "n3", 1);

		// n1 -> n2
		Link l = new Link(net, n2, 10, 0.1f, 5);
		n1.addLink(l);
		// n2 -> n1
		l = new Link(net, n1, 10, 0.1f, 5);
		n2.addLink(l);
		// n0 -> n2
		l = new Link(net, n2, 10, 0.1f, 10);
		n0.addLink(l);
		// n0 -> n3
		l = new Link(net, n3, 10, 0.1f, 10);
		n0.addLink(l);
		// n3 -> n1
		l = new Link(net, n1, 10, 0.1f, 10);
		n3.addLink(l);

		net.addNode(n0);
		net.addNode(n1);
		net.addNode(n2);
		net.addNode(n3);

		NetworkWrapper mainWrapper = new NetworkWrapper(net);
		spaceTransform.setAnimable(mainWrapper);
		spaceTransform.setGraphicSize(30);

		mainWrapper.getWrapper(n0).setPos(10, 1);
		mainWrapper.getWrapper(n1).setPos(-10, -3);
		mainWrapper.getWrapper(n2).setPos(0, -10);

		new Frame(mainPanel);
	}
}
