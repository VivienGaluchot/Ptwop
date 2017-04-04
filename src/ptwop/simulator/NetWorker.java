package ptwop.simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import passgen.WordGenerator;
import ptwop.common.gui.AnimationPanel;
import ptwop.common.gui.AnimationThread;
import ptwop.common.gui.Frame;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.GaussianRandom;
import ptwop.network.NServent;
import ptwop.p2p.P2P;
import ptwop.p2p.flood.FloodV0;
import ptwop.p2p.routing.StockasticRouter;
import ptwop.simulator.display.NetworkWrapper;
import ptwop.simulator.model.Network;
import ptwop.simulator.model.P2PCreator;

public class NetWorker {
	private static JTextPane console;
	private static JScrollPane scrollSole;

	private static class ConsoleOutputStream extends OutputStream {
		private final StringBuilder sb = new StringBuilder();

		@Override
		public void write(int b) throws IOException {
			if (b == '\r')
				return;
			if (b == '\n') {
				final String text = sb.toString() + "\n";
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						console.setText(console.getText() + text);
						scrollSole.getVerticalScrollBar().setValue(scrollSole.getVerticalScrollBar().getMaximum());
					}
				});
				sb.setLength(0);
				return;
			}
			sb.append((char) b);
		}
	}

	public static void display(Network net) {
		console = new JTextPane();
		console.setEditable(false);
		console.setFont(new Font("Consolas", Font.PLAIN, 12));
		console.setBackground(Color.black);
		console.setForeground(Color.white);
		console.setCaretColor(Color.lightGray);
		scrollSole = new JScrollPane(console);
		scrollSole.setMinimumSize(new Dimension(100, 100));
		scrollSole.setPreferredSize(new Dimension(300, 200));

		Frame term = new Frame(scrollSole, "Terminal");
		term.setSize(400, term.getHeight());
		term.setLocation(term.getLocation().x + 600, term.getLocation().y);

		System.setOut(new PrintStream(new ConsoleOutputStream()));

		SpaceTransform spaceTransform = new SpaceTransform();
		AnimationPanel mainPanel = new AnimationPanel(spaceTransform);
		spaceTransform.setFather(mainPanel);

		NetworkWrapper mainWrapper = new NetworkWrapper(net, spaceTransform);
		Command command = new Command(mainWrapper);
		mainWrapper.setCommand(command);
		spaceTransform.setAnimable(mainWrapper);
		spaceTransform.setGraphicSize(net.getNodes().size() * 2 + 10);

		mainWrapper.putInCircle();

		mainPanel.setFocusable(true);
		mainPanel.requestFocus();
		mainPanel.addMouseListener(mainWrapper);
		mainPanel.addMouseMotionListener(mainWrapper);
		mainPanel.addMouseWheelListener(mainWrapper);

		spaceTransform.setDisplayGrid(true);
		spaceTransform.setGridSize(5);

		AnimationThread thread = new AnimationThread(mainPanel);
		thread.startAnimation();
		Frame frame = new Frame(mainPanel, command, "Networker");
		frame.setLocation(frame.getLocation().x - 200, frame.getLocation().y);
	}

	public static void main(String[] args) {
		WordGenerator nameGenerator = new WordGenerator();
		Network net = new Network(new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV0(n, nameGenerator.getWord(6), new StockasticRouter());
			}
		});

		int nodeNumber = 10;
		GaussianRandom linkLatency = new GaussianRandom(5, 1000, 50, 40);
		GaussianRandom linkLoss = new GaussianRandom(0, 0, 0, 1); // no-loss
		GaussianRandom linkPacketSize = new GaussianRandom(1, 20, 12, 5);
		net.setRandomizers(linkLatency, linkLoss, linkPacketSize);
		net.addNewNodes(nodeNumber);

		display(net);
	}
}