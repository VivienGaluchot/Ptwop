package ptwop.simulator;

import java.io.IOException;
import java.util.ArrayList;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ptwop.common.math.GaussianRandom;
import ptwop.network.NServent;
import ptwop.p2p.P2P;
import ptwop.p2p.flood.*;
import ptwop.simulator.model.Link;
import ptwop.simulator.model.Network;
import ptwop.simulator.model.NetworkerNAddress;
import ptwop.simulator.model.Node;
import ptwop.simulator.model.P2PCreator;

public class Benchmarker {

	public static void main(String[] args) {

		P2PCreator floodV0Creator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV0(n, "");
			}
		};
		P2PCreator floodV1Creator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV1(n, "");
			}
		};
		P2PCreator floodV2Creator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV2(n, "");
			}
		};

		initMoyCollections("Connection d'un noeud", "t (ms)", "Nombre de messages", "Connection d'un noeud", "t (ms)",
				"Nombre de liens");
		evaluateOneConnectionOverTime(floodV0Creator, "FloodV0");
		evaluateOneConnectionOverTime(floodV1Creator, "FloodV1");
		evaluateOneConnectionOverTime(floodV2Creator, "FloodV2");
		displayMoyCollections();

		initMoyCollections("Temps de connexion d'un noeud au réseau", "Nombre de noeuds", "t (ms)", null, null, null);
		evaluateOneNodeConnexionTimeOverNumberOfNodes(floodV0Creator, "FloodV0");
		evaluateOneNodeConnexionTimeOverNumberOfNodes(floodV1Creator, "FloodV1");
		evaluateOneNodeConnexionTimeOverNumberOfNodes(floodV2Creator, "FloodV2");
	}

	static XYSeriesCollection moyCollection1;
	static String title1, xAxis1, yAxis1, title2, xAxis2, yAxis2;
	static XYSeriesCollection moyCollection2;

	public static void initMoyCollections(String title1_v, String xAxis1_v, String yAxis1_v, String title2_v,
			String xAxis2_v, String yAxis2_v) {
		title1 = title1_v;
		xAxis1 = xAxis1_v;
		yAxis1 = yAxis1_v;
		title2 = title2_v;
		xAxis2 = xAxis2_v;
		yAxis2 = yAxis2_v;
		moyCollection1 = new XYSeriesCollection();
		moyCollection2 = new XYSeriesCollection();
	}

	public static void displayMoyCollections() {
		if (title1 != null)
			new Chart(title1, xAxis1, yAxis1, moyCollection1);
		if (title2 != null)
			new Chart(title2, xAxis2, yAxis2, moyCollection2);
	}

	public static void evaluateOneConnectionOverTime(P2PCreator p2pcreator, String name) {
		XYSeriesCollection bandwith = new XYSeriesCollection();
		XYSeriesCollection linknumber = new XYSeriesCollection();
		ArrayList<Thread> runners = new ArrayList<>();
		int threadWorkNumber = 10;
		int threadNumber = 8;
		int nNode = 50;
		for (int t = 0; t < threadNumber; t++) {
			Thread runner = new Thread() {
				@Override
				public void run() {
					for (int i = 0; i < threadWorkNumber; i++) {
						Network net = new Network(p2pcreator);
						setToInterconnectedNetwork(net, nNode);
						net.track = true;
						Node alone = connectNodeAndWait(net);
						synchronized (bandwith) {
							bandwith.addSeries(net.totalBandwithUsed.getXYSerie(0, this.hashCode() + i));
							linknumber.addSeries(alone.linkNumberTracker.getXYSerie(0, this.hashCode() + i));
						}
						System.out.println(name + " : passe " + i);
					}
				}
			};
			runners.add(runner);
			runner.start();
		}
		for (Thread runner : runners) {
			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		title1 = "Connection d'un noeud (" + threadNumber * threadWorkNumber + "essais, " + nNode + "noeuds)";
		displayMinMaxMoy(bandwith, moyCollection1, name + " " + title1, "t (ms)", "Nombre de messages");
		title2 = "Connection d'un noeud (" + threadNumber * threadWorkNumber + "essais, " + nNode + "noeuds)";
		displayMinMaxMoy(linknumber, moyCollection2, name + " " + title2, "t (ms)", "Nombre de liens");
	}

	public static void evaluateOneNodeConnexionTimeOverNumberOfNodes(P2PCreator p2pcreator, String name) {
		XYSeriesCollection connexionTime = new XYSeriesCollection();
		ArrayList<Thread> runners = new ArrayList<>();
		int threadWorkNumber = 1;
		int threadNumber = 8;
		int nNodeMax = 70;
		for (int essai = 0; essai < threadNumber; essai++) {
			Thread runner = new Thread() {
				@Override
				public void run() {
					for (int essai = 0; essai < threadWorkNumber; essai++) {
						XYSeries series = new XYSeries(this.hashCode());
						Network net = new Network(p2pcreator);
						setToInterconnectedNetwork(net, 2);
						for (int i = 0; i < nNodeMax; i++) {
							long st = net.getTime();
							connectNodeAndWait(net);
							long et = net.getTime();
							series.add(i, et - st);
							System.out.println(name + " : passe " + i);
						}
						synchronized (connexionTime) {
							connexionTime.addSeries(series);
						}
					}
				}
			};
			runners.add(runner);
			runner.start();
		}
		for (Thread runner : runners)
			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		displayMinMaxMoy(connexionTime, moyCollection1, name + " temps de connexion d'un noeud au réseau",
				"Nombre de noeuds", "t (ms)");
	}

	public static void displayMinMaxMoy(XYSeriesCollection messageNumbersDataset, XYSeriesCollection moySerie,
			String title, String xAxis, String yAxis) {
		new Chart(title, xAxis, yAxis, getYMinMaxMoy(new XYSeriesCollection(), moySerie, messageNumbersDataset));
	}

	public static XYSeriesCollection getYMinMaxMoy(XYSeriesCollection outDataSet, XYSeriesCollection moyDataSet,
			XYSeriesCollection inDataSet) {
		XYSeries minS = new XYSeries("min");
		XYSeries moyS = new XYSeries("moy");
		XYSeries maxS = new XYSeries("max");
		int nSeries = inDataSet.getSeriesCount();
		boolean doMore = true;
		for (int i = 0; doMore; i++) {
			Double min = Double.MAX_VALUE;
			Double max = Double.MIN_VALUE;
			Double moy = 0.0;
			int nb = 0;
			doMore = false;
			Number vx = null;
			for (int j = 0; j < nSeries; j++) {
				XYSeries series = inDataSet.getSeries(j);
				if (i < series.getItemCount()) {
					vx = series.getX(i);
					Number value = series.getY(i);
					if (value instanceof Double) {
						Double n = (Double) value;
						if (n < min)
							min = n;
						if (n > max)
							max = n;
						moy += n;
						nb++;
					} else if (value instanceof Integer) {
						Integer n = (Integer) value;
						if (n < min)
							min = n.doubleValue();
						if (n > max)
							max = n.doubleValue();
						moy += n;
						nb++;
					}
					doMore = true;
				}
			}
			if (nb > 0) {
				moy = moy / nb;
				minS.add(vx, min);
				moyS.add(vx, moy);
				maxS.add(vx, max);
			}
		}
		outDataSet.addSeries(minS);
		outDataSet.addSeries(moyS);
		outDataSet.addSeries(maxS);
		if (moyDataSet != null) {
			moyS.setKey(moyDataSet.getSeriesCount());
			moyDataSet.addSeries(moyS);
		}
		return outDataSet;
	}

	public static XYSeriesCollection getMoy(XYSeriesCollection outDataSet, XYSeriesCollection inDataSet, String title) {
		XYSeries moyS = new XYSeries("moy");
		int nSeries = inDataSet.getSeriesCount();
		int nValues = inDataSet.getSeries(0).getItemCount();
		for (int i = 0; i < nValues; i++) {
			Double moy = 0.0;
			int nb = 0;
			for (int j = 0; j < nSeries; j++) {
				XYSeries series = inDataSet.getSeries(j);
				if (i < series.getItemCount()) {
					Number value = series.getY(i);
					if (value instanceof Double) {
						Double n = (Double) value;
						moy += n;
						nb++;
					} else if (value instanceof Integer) {
						Integer n = (Integer) value;
						moy += n;
						nb++;
					}
				}
			}
			moy = moy / nb;
			moyS.add(inDataSet.getSeries(0).getX(i), moy);
		}
		outDataSet.addSeries(moyS);
		return outDataSet;
	}

	public static void reachStability(Network net) {
		int messageNumber;
		do {
			net.doTimeStep();
			messageNumber = 0;
			for (Node n : net.getNodes()) {
				for (Link l : n.getLinks()) {
					messageNumber += l.getNumberOfPendingMessages();
					if(messageNumber > 0)
						break;
				}
				if(messageNumber > 0)
					break;
			}
		} while (messageNumber > 0);
	}

	public static Network setToInterconnectedNetwork(Network net, int nodeNumber) {
		GaussianRandom linkLatency = new GaussianRandom(5, 1000, 50, 40);
		GaussianRandom linkLoss = new GaussianRandom(0, 0, 0, 1); // no-loss
		GaussianRandom linkPacketSize = new GaussianRandom(1, 15, 3, 2);
		net.setRandomizers(linkLatency, linkLoss, linkPacketSize);
		net.addNewNodes(nodeNumber);
		net.setToFullyInterconnected();
		return net;
	}

	public static Node connectNodeAndWait(Network net) {
		Node alone = net.addNewNode();
		alone.track = true;
		try {
			alone.connectTo(new NetworkerNAddress(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		reachStability(net);
		return alone;
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
