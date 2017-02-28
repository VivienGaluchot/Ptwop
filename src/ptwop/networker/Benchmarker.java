package ptwop.networker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ptwop.common.math.GaussianRandom;
import ptwop.network.NServent;
import ptwop.networker.model.Link;
import ptwop.networker.model.Network;
import ptwop.networker.model.NetworkerNAddress;
import ptwop.networker.model.Node;
import ptwop.networker.model.P2PCreator;
import ptwop.p2p.P2P;
import ptwop.p2p.flood.*;

public class Benchmarker {
	public static void main(String[] args) {

		// evaluateOneConnectionOverTime(new P2PCreator() {
		// @Override
		// public P2P createP2P(NManager n) {
		// return new FloodV0(n, "");
		// }
		// }, "FloodV0");
		//
		// evaluateOneConnectionOverTime(new P2PCreator() {
		// @Override
		// public P2P createP2P(NManager n) {
		// return new FloodV1(n, "");
		// }
		// }, "FloodV1");
		//
		// evaluateOneConnectionOverTime(new P2PCreator() {
		// @Override
		// public P2P createP2P(NManager n) {
		// return new FloodV2(n, "");
		// }
		// }, "FloodV2");

		// evaluateFullConnexionTimeOverNumberOfNodes(new P2PCreator() {
		// @Override
		// public P2P createP2P(NManager n) {
		// return new FloodV0(n, "");
		// }
		// }, "FloodV0");
		//
		// evaluateFullConnexionTimeOverNumberOfNodes(new P2PCreator() {
		// @Override
		// public P2P createP2P(NManager n) {
		// return new FloodV1(n, "");
		// }
		// }, "FloodV1");
		//
		// evaluateFullConnexionTimeOverNumberOfNodes(new P2PCreator() {
		// @Override
		// public P2P createP2P(NManager n) {
		// return new FloodV2(n, "");
		// }
		// }, "FloodV2");

		evaluateOneNodeConnexionTimeOverNumberOfNodes(new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV0(n, "");
			}
		}, "FloodV0");

		evaluateOneNodeConnexionTimeOverNumberOfNodes(new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV1(n, "");
			}
		}, "FloodV1");

		evaluateOneNodeConnexionTimeOverNumberOfNodes(new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV2(n, "");
			}
		}, "FloodV2");
	}

	public static void evaluateOneConnectionOverTime(P2PCreator p2pcreator, String name) {
		XYSeriesCollection bandwith = new XYSeriesCollection();
		XYSeriesCollection linknumber = new XYSeriesCollection();
		ArrayList<Thread> runners = new ArrayList<>();
		int nC = 100;
		int nNode = 20;
		int nThreads = 8;
		for (int t = 0; t < nThreads; t++) {
			Thread runner = new Thread() {
				@Override
				public void run() {
					for (int i = 0; i < nC / nThreads; i++) {
						Network net = new Network(p2pcreator);
						interconnectedNodes(net, nNode);
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
		displayMinMaxMoy(bandwith, name + " connection d'un noeud", "t (ms)", "Nombre de messages");
		displayMinMaxMoy(linknumber, name + " connection d'un noeud", "t (ms)", "Nombre de liens");
	}

	public static void evaluateFullConnexionTimeOverNumberOfNodes(P2PCreator p2pcreator, String name) {
		XYSeriesCollection connexionTime = new XYSeriesCollection();
		ArrayList<Thread> runners = new ArrayList<>();
		int nEssais = 20;
		int nNodeMax = 50;
		for (int essai = 0; essai < nEssais; essai++) {
			Thread runner = new Thread() {
				@Override
				public void run() {
					XYSeries series = new XYSeries(this.hashCode());
					int j = 1;
					for (int i = 0; i < nNodeMax; i = i + j++) {
						Network net = new Network(p2pcreator);
						interconnectedNodes(net, i);
						connectNodeAndWait(net);
						series.add(i, net.getTime());
						System.out.println(name + " : passe " + i);
					}
					synchronized (connexionTime) {
						connexionTime.addSeries(series);
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
		displayMinMaxMoy(connexionTime, name + " noeuds connectés en meme temps", "Nombre de noeuds", "t (ms)");
	}

	public static void evaluateOneNodeConnexionTimeOverNumberOfNodes(P2PCreator p2pcreator, String name) {
		XYSeriesCollection connexionTime = new XYSeriesCollection();
		ArrayList<Thread> runners = new ArrayList<>();
		int nEssais = 1;
		int nNodeMax = 150;
		for (int essai = 0; essai < nEssais; essai++) {
			Thread runner = new Thread() {
				@Override
				public void run() {
					XYSeries series = new XYSeries(this.hashCode());
					Network net = new Network(p2pcreator);
					interconnectedNodes(net, 2);
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
		displayMinMaxMoy(connexionTime, name + " noeuds connectés en meme temps", "Nombre de noeuds", "t (ms)");
	}

	public static void displayMinMaxMoy(XYSeriesCollection messageNumbersDataset, String title, String xAxis,
			String yAxis) {
		new Chart(title, xAxis, yAxis, getYMinMaxMoy(new XYSeriesCollection(), messageNumbersDataset));
	}

	public static XYSeriesCollection getYMinMaxMoy(XYSeriesCollection outDataSet, XYSeriesCollection inDataSet) {
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
			messageNumber = getPendingMessageNumber(net);
		} while (messageNumber > 0);
	}

	public static Network interconnectedNodes(Network net, int nodeNumber) {
		GaussianRandom linkLatency = new GaussianRandom(5, 1000, 50, 40);
		GaussianRandom linkLoss = new GaussianRandom(0, 0, 0, 1); // no-loss
		GaussianRandom linkPacketSize = new GaussianRandom(1, 15, 3, 2);
		net.setRandomizers(linkLatency, linkLoss, linkPacketSize);
		net.addNewNodes(nodeNumber);

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
		reachStability(net);
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
		// while (alone.getLinks().size() < (net.getNodes().size() - 1))
		// net.doTimeStep();
		// // few more steps
		// for (int j = 0; j < 10; j++)
		// net.doTimeStep();
		return alone;
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
