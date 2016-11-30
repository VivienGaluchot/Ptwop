package ptwop.networker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import ptwop.common.gui.AnimationPanel;
import ptwop.common.gui.AnimationThread;
import ptwop.common.gui.Frame;
import ptwop.common.gui.SpaceTransform;
import ptwop.networker.display.NetworkWrapper;
import ptwop.networker.model.DualLink;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;

public class NetWorker {
	public static void main(String[] args) {
		SpaceTransform spaceTransform = new SpaceTransform();
		AnimationPanel mainPanel = new AnimationPanel(spaceTransform);
		spaceTransform.setFather(mainPanel);

		Network net = new Network();
		Command command = new Command(net);

		Node n0 = new Node(net, 1);
		Node n1 = new Node(net, 1);
		Node n2 = new Node(net, 1);
		Node n3 = new Node(net, 1);

		// // n1 -> n2
		// Link.connect(net, n1, n2);
		// // n2 -> n1
		// Link.connect(net, n2, n1);
		// // n0 -> n2
		// Link.connect(net, n0, n2);
		// // n0 -> n3
		// Link.connect(net, n0, n3);
		// // n3 -> n1
		// Link.connect(net, n3, n1);
		// n1 <-> n0
		new DualLink(net, n1, n0, 200, 0, 2);
		new DualLink(net, n1, n2, 100, 0, 2);
		new DualLink(net, n2, n0, 50, 0, 2);
		new DualLink(net, n3, n0, 50, 0, 2);
		new DualLink(net, n1, n3, 60, 0, 4);

		net.addNode(n0);
		net.addNode(n1);
		net.addNode(n2);
		net.addNode(n3);

		net.initBellmanFord();

		NetworkWrapper mainWrapper = new NetworkWrapper(net, spaceTransform, command);
		spaceTransform.setAnimable(mainWrapper);
		spaceTransform.setGraphicSize(30);

		mainWrapper.getWrapper(n0).setPos(4, 0);
		mainWrapper.getWrapper(n1).setPos(-4, 0);
		mainWrapper.getWrapper(n2).setPos(0, -4);
		mainWrapper.getWrapper(n3).setPos(0, 4);

		mainPanel.setFocusable(true);
		mainPanel.requestFocus();
		mainPanel.addMouseListener(mainWrapper);
		mainPanel.addMouseMotionListener(mainWrapper);
		mainPanel.addMouseWheelListener(mainWrapper);

		mainPanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				net.doTimeStep();
				command.update();
			}
		});

		AnimationThread thread = new AnimationThread(mainPanel);
		thread.startAnimation();
		new Frame(mainPanel, command);
	}
}
