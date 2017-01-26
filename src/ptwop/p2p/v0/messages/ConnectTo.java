package ptwop.p2p.v0.messages;

import ptwop.network.NetworkAdress;

public class ConnectTo extends FloodMessage {
	private static final long serialVersionUID = 1L;
	public NetworkAdress adress;

	public ConnectTo(NetworkAdress adress) {
		this.adress = adress;
	}
}
