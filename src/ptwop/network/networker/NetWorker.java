package ptwop.network.networker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import ptwop.common.gui.AnimationPanel;
import ptwop.common.gui.AnimationThread;
import ptwop.common.gui.Frame;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.GaussianRandom;
import ptwop.network.networker.display.NetworkWrapper;
import ptwop.network.networker.model.Network;

public class NetWorker {
	public static void main(String[] args) {
		SpaceTransform spaceTransform = new SpaceTransform();
		AnimationPanel mainPanel = new AnimationPanel(spaceTransform);
		spaceTransform.setFather(mainPanel);

		final Network net = new Network();
		int nodeNumber = 25;
		GaussianRandom nodeLatency = new GaussianRandom(1,100,10,5);
		float connex = 2f;
		GaussianRandom linkLatency = new GaussianRandom(5,1000,50,40);
		GaussianRandom linkLoss = new GaussianRandom(0,0,0,1); // no-loss
		GaussianRandom linkPacketSize = new GaussianRandom(1,100,10,5);
		net.randomize(nodeNumber, nodeLatency, connex, linkLatency, linkLoss, linkPacketSize);

		final Command command = new Command(net);
		NetworkWrapper mainWrapper = new NetworkWrapper(net, spaceTransform, command);
		spaceTransform.setAnimable(mainWrapper);
		spaceTransform.setGraphicSize(nodeNumber*2+10);
		
		mainWrapper.putInCircle();

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

		spaceTransform.setDisplayGrid(true);
		spaceTransform.setGridSize(5);

		AnimationThread thread = new AnimationThread(mainPanel);
		thread.startAnimation();
		new Frame(mainPanel, command);
	}
}
