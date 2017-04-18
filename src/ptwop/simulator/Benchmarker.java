package ptwop.simulator;

import java.io.IOException;
import java.util.ArrayList;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ptwop.common.math.GaussianRandom;
import ptwop.network.NServent;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.flood.*;
import ptwop.p2p.routing.*;
import ptwop.simulator.model.BenchmarkData;
import ptwop.simulator.model.Link;
import ptwop.simulator.model.Network;
import ptwop.simulator.model.NetworkerNAddress;
import ptwop.simulator.model.Node;
import ptwop.simulator.model.P2PCreator;

public class Benchmarker {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		// Routing
		P2PCreator DumbRouterCreator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV1(n, "", new DumbRouter());
			}
		};
		P2PCreator StockasticRouterCreator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV1(n, "", new StockasticRouter());
			}
		};
		P2PCreator StockasticLogRouterCreator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV1(n, "", new StockasticLogRouter());
			}
		};
		P2PCreator StockasticLogRouter2Creator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV1(n, "", new StockasticLogRouter2());
			}
		};

		if (true) {
			initMoyCollections("Envois d'un message", "Message envoyés", "Latence (ms)", null, null, null);
			// evaluateSendTimeOverTime(DumbRouterCreator, "DumbRouter");
			// evaluateSendTimeOverTime(StockasticRouterCreator, "StockasticRouter");
			evaluateSendTimeOverTime(StockasticLogRouterCreator, "StockasticLogRouter");
			evaluateSendTimeOverTime(StockasticLogRouter2Creator, "StockasticLogRouter2");
			displayMoyCollections();
		}

		if (false) {
			initMoyCollections("Broadcast", "Taille de message (octets)", "Latence (ms)", null, null, null);
			evaluateBroadcastTimeOverMessageSize(DumbRouterCreator, "DumbRouter");
			evaluateBroadcastTimeOverMessageSize(StockasticRouterCreator, "StockasticRouter");
			evaluateBroadcastTimeOverMessageSize(StockasticLogRouterCreator, "StockasticLogRouter");
			displayMoyCollections();
		}

		// Interconnection
		P2PCreator floodV0Creator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV0(n, "", new DumbRouter());
			}
		};
		P2PCreator floodV1Creator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV1(n, "", new DumbRouter());
			}
		};
		P2PCreator floodV2Creator = new P2PCreator() {
			@Override
			public P2P createP2P(NServent n) {
				return new FloodV2(n, "", new DumbRouter());
			}
		};
		if (false) {
			initMoyCollections("Connection d'un noeud", "t (ms)", "Nombre de messages", "Connection d'un noeud",
					"t (ms)", "Nombre de liens");
			evaluateOneConnectionOverTime(floodV0Creator, "FloodV0");
			evaluateOneConnectionOverTime(floodV1Creator, "FloodV1");
			evaluateOneConnectionOverTime(floodV2Creator, "FloodV2");
			displayMoyCollections();

			initMoyCollections("Temps de connexion d'un noeud au réseau", "Nombre de noeuds", "t (ms)", null, null,
					null);
			evaluateOneNodeConnexionTimeOverNumberOfNodes(floodV0Creator, "FloodV0");
			evaluateOneNodeConnexionTimeOverNumberOfNodes(floodV1Creator, "FloodV1");
			evaluateOneNodeConnexionTimeOverNumberOfNodes(floodV2Creator, "FloodV2");
			displayMoyCollections();
		}
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
		int threadWorkNumber = 1;
		int threadNumber = 4;
		int nNode = 500;
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
		displayMinMaxMoy(bandwith, moyCollection1, name, name + " " + title1, "t (ms)", "Nombre de messages");
		title2 = "Connection d'un noeud (" + threadNumber * threadWorkNumber + "essais, " + nNode + "noeuds)";
		displayMinMaxMoy(linknumber, moyCollection2, name, name + " " + title2, "t (ms)", "Nombre de liens");
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
		displayMinMaxMoy(connexionTime, moyCollection1, name, name + " temps de connexion d'un noeud au réseau",
				"Nombre de noeuds", "t (ms)");
	}

	public static void evaluateBroadcastTimeOverMessageSize(P2PCreator p2pcreator, String name) {
		XYSeriesCollection broadcastTime = new XYSeriesCollection();
		ArrayList<Thread> runners = new ArrayList<>();
		int threadWorkNumber = 50;
		int threadNumber = 4;
		int networkSize = 15;
		for (int essai = 0; essai < threadNumber; essai++) {
			Thread runner = new Thread() {
				@Override
				public void run() {
					for (int essai = 0; essai < threadWorkNumber; essai++) {
						System.out.println("Passe " + essai + "/" + threadWorkNumber);
						Network net = new Network(p2pcreator);
						setToInterconnectedNetwork(net, networkSize);
						for (Node n : net.getNodes())
							n.track = true;
						Node n0 = net.getNode(0);
						for (int i = 10; i <= 500; i += i / 20 + 1) {
							net.getP2P(n0).broadcast(new BenchmarkData(i));
							// attente que tout le monde ait reçus
							reachStability(net, 10);
						}
						synchronized (broadcastTime) {
							for (int i = 1; i < networkSize; i++) {
								Node n = net.getNode(i);
								broadcastTime.addSeries(n.sizeVsTimeToReceive.getXYSerie());
							}
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
		displayMinMaxMoy(broadcastTime, moyCollection1, name, "15 users - Broadcast : routage " + name,
				"Taille de message (octets)", "Latence (ms)");
	}

	public static void evaluateSendTimeOverTime(P2PCreator p2pcreator, String name) {
		XYSeriesCollection sendTime = new XYSeriesCollection();
		ArrayList<Thread> runners = new ArrayList<>();
		int threadWorkNumber = 20;
		int threadNumber = 8;
		int networkSize = 12;
		for (int essai = 0; essai < threadNumber; essai++) {
			Thread runner = new Thread() {
				@Override
				public void run() {
					for (int essai = 0; essai < threadWorkNumber; essai++) {
						System.out.println("Passe " + essai + "/" + threadWorkNumber);
						Network net = new Network(p2pcreator);
						setToInterconnectedNetwork(net, networkSize);
						Node n0 = net.getNode(0);
						for (int i = 1; i < networkSize; i++)
							net.getNode(i).track = true;
						P2P senderP2P = net.getP2P(n0);
						for (int i = 0; i < 500; i++) {
							try {
								P2PUser receiver = senderP2P
										.getUser(net.getNode(1).getAddress());
								senderP2P.sendTo(receiver, new BenchmarkData(15, i));
								reachStability(net, 5);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						synchronized (sendTime) {
							XYSeries serie = net.getNode(1).idVsTimeToReceive.getXYSerie();
							for (int i = 2; i < networkSize; i++) {
								Node n = net.getNode(i);
								for(Object o : n.idVsTimeToReceive.getXYSerie().getItems()){
									serie.add((XYDataItem) o);
								}
							}
							sendTime.addSeries(serie);
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
		displayMinMaxMoy(sendTime, moyCollection1, name, "Envois d'un message, routeur " + name, "Message envoyés",
				"Latence (ms)");
	}

	public static void displayMinMaxMoy(XYSeriesCollection messageNumbersDataset, XYSeriesCollection moySerie,
			String moySerieName, String title, String xAxis, String yAxis) {
		new Chart(title, xAxis, yAxis,
				getYMinMaxMoy(new XYSeriesCollection(), moySerie, moySerieName, messageNumbersDataset));
	}

	public static XYSeriesCollection getYMinMaxMoy(XYSeriesCollection outDataSet, XYSeriesCollection moyDataSet,
			String moySerieName, XYSeriesCollection inDataSet) {
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
					} else if (value instanceof Long) {
						Long n = (Long) value;
						if (n < min)
							min = n.doubleValue();
						if (n > max)
							max = n.doubleValue();
						moy += n;
						nb++;
					} else {
						throw new IllegalArgumentException("Unsupported Number class");
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
			moyS.setKey("moy" + moySerieName);
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

	public static void reachStability(Network net, int overStep) {
		int messageNumber;
		int i = 0;
		do {
			net.doTimeStep();
			messageNumber = 0;
			for (Node n : net.getNodes()) {
				for (Link l : n.getLinks()) {
					messageNumber += l.getNumberOfPendingMessages();
					if (messageNumber > 0)
						break;
				}
				if (messageNumber > 0)
					break;
			}
			if (messageNumber == 0)
				i++;
			else
				i = 0;
		} while (i < overStep);
	}

	public static Network setToInterconnectedNetwork(Network net, int nodeNumber) {
		GaussianRandom linkLatency = new GaussianRandom(5, 3000, 100, 50);
		GaussianRandom linkLoss = new GaussianRandom(0, 0, 0, 1); // no-loss
		GaussianRandom linkPacketSize = new GaussianRandom(1, 20, 12, 5);
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
		reachStability(net, 0);
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
