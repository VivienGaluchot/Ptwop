package ptwop.networker;

import java.io.IOException;
import java.util.ArrayList;

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

	static ArrayList<Long> connectionTime;
	static ArrayList<Integer> messageNumbers;

	public static void main(String[] args) {
		connectionTime = new ArrayList<>();
		messageNumbers = new ArrayList<>();

		int nC = 1;
		for (int i = 0; i < nC; i++) {
			createNetwork(100);
			for (Integer j : messageNumbers)
				System.out.println(j);
			messageNumbers.clear();
		}

		long moy = 0;
		for (Long l : connectionTime) {
			moy += l;
		}
		moy = moy / nC;
		System.out.println("Moyenne : " + moy);

		double variance = 0;
		for (Long l : connectionTime) {
			variance += (l - moy) * (l - moy);
		}
		variance = variance / connectionTime.size();
		// System.out.println("Variance : " + variance);

		double eqType = Math.sqrt(variance);
		System.out.println("eqType : " + eqType);
	}

	// test de connection a un réseau existant
	public static Network createNetwork(int nodeNumber) {
		Network net = new Network(new P2PCreator() {
			@Override
			public P2P createP2P(NManager n) {
				return new FloodV2(n, "");
			}
		});
		GaussianRandom linkLatency = new GaussianRandom(5, 1000, 50, 40);
		GaussianRandom linkLoss = new GaussianRandom(0, 0, 0, 1); // no-loss
		GaussianRandom linkPacketSize = new GaussianRandom(1, 15, 3, 2);
		net.randomize(nodeNumber + 1, linkLatency, linkLoss, linkPacketSize);

		Node alone = net.getNode(0);

		// connect all nodes to all nodes and wait stabilization
		for (Node n1 : net.getNodes()) {
			for (Node n2 : net.getNodes()) {
				if (n1 != alone && n2 != alone && n1 != n2 && !n1.isConnectedTo(n2.getAddress())) {
					try {
						n1.connectTo(n2.getAddress());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		boolean stable = false;
		long stableTime = 0;
		int messageNumber;
		do {
			do {
				net.doTimeStep();
				messageNumber = getMessageNumber(net);
				messageNumbers.add(messageNumber);
			} while (messageNumber > 0);
			stable = true;
			stableTime = net.getTime();
			// 50 more steps
			for (int i = 0; i < 50; i++) {
				net.doTimeStep();
				messageNumber = getMessageNumber(net);
				if (messageNumber > 0) {
					stable = false;
					break;
				}
			}
		} while (!stable);

		System.out.println("Network stabilized at " + stableTime + "ms");
		connectionTime.add(stableTime);
		try {
			alone.connectTo(new NetworkerNAddress(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stable = false;
		do {
			do {
				net.doTimeStep();
				messageNumber = getMessageNumber(net);
				messageNumbers.add(messageNumber);
			} while (messageNumber > 0);
			stable = true;
			stableTime = net.getTime();
			// 50 more steps
			for (int i = 0; i < 50; i++) {
				net.doTimeStep();
				messageNumber = getMessageNumber(net);
				if (messageNumber > 0) {
					stable = false;
					break;
				}
			}
		} while (!stable);
		return net;
	}

	public static int getMessageNumber(Network net) {
		int res = 0;
		for (Node n : net.getNodes()) {
			for (Link l : n.getLinks()) {
				res += l.getTransitingDatas().size();
			}
		}
		return res;
	}
}
