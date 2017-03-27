package ptwop.simulator.model;

import ptwop.network.NServent;
import ptwop.p2p.P2P;

public interface P2PCreator {
	public P2P createP2P(NServent n);
}
