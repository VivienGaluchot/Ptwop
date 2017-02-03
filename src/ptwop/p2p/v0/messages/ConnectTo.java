package ptwop.p2p.v0.messages;

import ptwop.network.NAddress;

public class ConnectTo extends FloodMessage {
	private static final long serialVersionUID = 1L;
	public NAddress adress;

	public ConnectTo(NAddress adress) {
		this.adress = adress;
	}
}
