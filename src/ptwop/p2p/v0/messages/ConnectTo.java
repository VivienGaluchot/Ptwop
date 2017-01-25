package ptwop.p2p.v0.messages;

import ptwop.network.NetworkAdress;
import ptwop.p2p.P2PUser;

public class ConnectTo extends FloodMessage {
	private static final long serialVersionUID = 1L;
	public NetworkAdress adress;
	public String name;

	public ConnectTo(P2PUser user) {
		this.adress = user.getAdress();
		this.name = user.getName();
	}

	public P2PUser createUser() {
		return new P2PUser(name, adress);
	}
}
