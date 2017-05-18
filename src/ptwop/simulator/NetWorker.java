package ptwop.simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import passgen.WordGenerator;
import ptwop.common.SystemClock;
import ptwop.common.gui.AnimationPanel;
import ptwop.common.gui.AnimationThread;
import ptwop.common.gui.Dialog;
import ptwop.common.gui.Frame;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.GaussianRandom;
import ptwop.network.NServent;
import ptwop.p2p.P2P;
import ptwop.p2p.core.CoreV0;
import ptwop.p2p.core.CoreV1;
import ptwop.p2p.core.CoreV2;
import ptwop.p2p.routing.BayesianRouter;
import ptwop.p2p.routing.DumbRouter;
import ptwop.p2p.routing.LogRouter;
import ptwop.p2p.routing.Router;
import ptwop.p2p.routing.StockasticLogRouter;
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
		NetworkWrapper mainWrapper = new NetworkWrapper(net, spaceTransform);
		AnimationThread thread = new AnimationThread(mainPanel);
		Command command = new Command(thread, mainWrapper);
		Frame frame = new Frame(mainPanel, command, "Networker");

		spaceTransform.setFather(mainPanel);
		spaceTransform.setAnimable(mainWrapper);
		spaceTransform.setGraphicSize(net.getNodes().size() * 2 + 10);
		spaceTransform.setDisplayGrid(true);
		spaceTransform.setGridSize(5);

		mainPanel.setFocusable(true);
		mainPanel.requestFocus();
		mainPanel.addMouseListener(mainWrapper);
		mainPanel.addMouseMotionListener(mainWrapper);
		mainPanel.addMouseWheelListener(mainWrapper);

		mainWrapper.putInCircle();
		mainWrapper.setCommand(command);

		frame.setLocation(frame.getLocation().x - 200, frame.getLocation().y);

		thread.startAnimation();
	}

	public static void main(String[] args) {
		WordGenerator nameGenerator = new WordGenerator();

		P2P[] p2ps = { new CoreV0(), new CoreV1(), new CoreV2() };
		Router[] routers = { new DumbRouter(), new StockasticRouter(), new LogRouter(), new StockasticLogRouter(),
				new BayesianRouter() };

		P2P p2p = (P2P) Dialog.JListDialog(null, "Select a P2P kernel", p2ps);
		if (p2p == null)
			return;
		Router router = (Router) Dialog.JListDialog(null, "Select a router system", routers);
		if (router == null)
			return;
		
		int numberOfNodes = Dialog.NumberDialog(null, "Number of nodes in the network", "Info", "10");

		try {
			@SuppressWarnings("unchecked")
			Constructor<P2P> p2pConstructor = (Constructor<P2P>) p2p.getClass().getConstructor(String.class);
			@SuppressWarnings("unchecked")
			Constructor<Router> routerConstructor = (Constructor<Router>) router.getClass().getConstructor();

			P2PCreator pcreator = new P2PCreator() {
				@Override
				public P2P createAndStartP2P(NServent n) {
					try {
						Router router = routerConstructor.newInstance();
						router.setClock(new SystemClock());
						P2P p2p = p2pConstructor.newInstance(nameGenerator.getWord(5));
						p2p.start(n, router);
						return p2p;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
					return null;
				}
			};

			Network net = new Network(pcreator);
			int nodeNumber = numberOfNodes;
			GaussianRandom linkLatency = new GaussianRandom(5, 1000, 50, 40);
			GaussianRandom linkLoss = new GaussianRandom(0, 0, 0, 1); // no-loss
			GaussianRandom linkPacketSize = new GaussianRandom(1, 20, 12, 5);
			net.setRandomizers(linkLatency, linkLoss, linkPacketSize);
			net.addNewNodes(nodeNumber);

			display(net);
			
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
