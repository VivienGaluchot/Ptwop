package ptwop.networker;

import java.io.IOException;
import java.util.Iterator;

import org.jfree.data.category.DefaultCategoryDataset;

import ptwop.common.math.GaussianRandom;
import ptwop.network.NManager;
import ptwop.networker.model.Link;
import ptwop.networker.model.Network;
import ptwop.networker.model.NetworkerNAddress;
import ptwop.networker.model.Node;
import ptwop.networker.model.P2PCreator;
import ptwop.p2p.P2P;
import ptwop.p2p.flood.*;

public class Benchmarker {
	public static void main(String[] args) {
		DefaultCategoryDataset messageNumbersDataset = new DefaultCategoryDataset();
		DefaultCategoryDataset threeMoy = new DefaultCategoryDataset();

		int nC = 100;
		for (int i = 0; i < nC; i++) {
			Network net = createInterconnectedNetwork(new P2PCreator() {
				@Override
				public P2P createP2P(NManager n) {
					return new FloodV0(n, "");
				}
			}, 10, messageNumbersDataset, "" + i);
			connectNodeAndWait(net, messageNumbersDataset, "" + i);
			System.out.println("Passe " + i);
		}
		 displayMinMaxMoy(messageNumbersDataset, "FloodV0", "Temps", "Nombre de messages");
		getMinMaxMoy(threeMoy, messageNumbersDataset, "FloodV0");

		for (int i = 0; i < nC; i++) {
			Network net = createInterconnectedNetwork(new P2PCreator() {
				@Override
				public P2P createP2P(NManager n) {
					return new FloodV1(n, "");
				}
			}, 10, messageNumbersDataset, "" + i);
			connectNodeAndWait(net, messageNumbersDataset, "" + i);
			System.out.println("Passe " + i);
		}
		 displayMinMaxMoy(messageNumbersDataset, "FloodV1", "Temps", "Nombre de messages");
		getMinMaxMoy(threeMoy, messageNumbersDataset, "FloodV1");

		for (int i = 0; i < nC; i++) {
			Network net = createInterconnectedNetwork(new P2PCreator() {
				@Override
				public P2P createP2P(NManager n) {
					return new FloodV2(n, "");
				}
			}, 10, messageNumbersDataset, "" + i);
			connectNodeAndWait(net, messageNumbersDataset, "" + i);
			System.out.println("Passe " + i);
		}
		 displayMinMaxMoy(messageNumbersDataset, "FloodV2", "Temps", "Nombre de messages");
		getMinMaxMoy(threeMoy, messageNumbersDataset, "FloodV2");
		new Chart("Comparaison", "Temps", "Nombre de messages", threeMoy);
	}

	public static void displayMinMaxMoy(DefaultCategoryDataset messageNumbersDataset, String title, String xAxis,
			String yAxis) {
		new Chart(title, xAxis, yAxis, getMinMaxMoy(new DefaultCategoryDataset(), messageNumbersDataset, title));
	}

	public static DefaultCategoryDataset getMinMaxMoy(DefaultCategoryDataset outDataSet,
			DefaultCategoryDataset inDataSet, String title) {
		for (Object ckey : inDataSet.getColumnKeys()) {
			Double min = Double.MAX_VALUE;
			Double max = Double.MIN_VALUE;
			Double moy = 0.0;
			int nb = 0;
			for (Object rkey : inDataSet.getRowKeys()) {
				Number value = inDataSet.getValue((Comparable<?>) rkey, (Comparable<?>) ckey);
				if (value instanceof Double) {
					Double n = (Double) value;
					if (n < min)
						min = n;
					if (n > max)
						max = n;
					moy += n;
					nb++;
				}
			}
			moy = moy / nb;
			outDataSet.addValue(min, title + "min", (Comparable<?>) ckey);
			outDataSet.addValue(moy, title + "moy", (Comparable<?>) ckey);
			outDataSet.addValue(max, title + "max", (Comparable<?>) ckey);
		}

		return outDataSet;
	}

	public static void reachStability(Network net, DefaultCategoryDataset dataset, String category) {
		int messageNumber;
		do {
			net.doTimeStep();
			if (dataset != null) {
				dataset.setValue(getTransitingMessagesNumber(net), category, new Long(net.getTime()));
			}
			messageNumber = getPendingMessageNumber(net);
		} while (messageNumber > 0);
	}

	public static Network createInterconnectedNetwork(P2PCreator p2pC, int nodeNumber, DefaultCategoryDataset dataset,
			String category) {
		Network net = new Network(p2pC);
		GaussianRandom linkLatency = new GaussianRandom(5, 1000, 50, 40);
		GaussianRandom linkLoss = new GaussianRandom(0, 0, 0, 1); // no-loss
		GaussianRandom linkPacketSize = new GaussianRandom(1, 15, 3, 2);
		net.randomize(nodeNumber + 1, linkLatency, linkLoss, linkPacketSize);

		// Connect all nodes but 'alone' in line
		Iterator<Node> nodeIt = net.getNodes().iterator();
		Node prev = null;
		while (nodeIt.hasNext()) {
			Node n = nodeIt.next();
			if (prev != null)
				try {
					n.connectTo(prev.getAddress());
				} catch (IOException e) {
					e.printStackTrace();
				}
			prev = n;
		}
		reachStability(net, dataset, category);
		return net;
	}

	public static void connectNodeAndWait(Network net, DefaultCategoryDataset dataset, String category) {
		Node alone = net.addNewNode();
		try {
			alone.connectTo(new NetworkerNAddress(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		reachStability(net, dataset, category);
	}

	public static int getPendingMessageNumber(Network net) {
		int res = 0;
		for (Node n : net.getNodes()) {
			for (Link l : n.getLinks()) {
				res += l.getNumberOfPendingMessages();
			}
		}
		return res;
	}

	public static int getTransitingMessagesNumber(Network net) {
		int res = 0;
		for (Node n : net.getNodes()) {
			for (Link l : n.getLinks()) {
				res += l.getNumberOfTransitingElements();
			}
		}
		return res;
	}
}
