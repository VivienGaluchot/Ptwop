package ptwop.networker.model;

import ptwop.network.NManager;
import ptwop.p2p.P2P;

public interface P2PCreator {
	public P2P createP2P(NManager n);
}
