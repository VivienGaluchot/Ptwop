package ptwop.p2p.flood.messages;

import ptwop.network.NAddress;

public class ConnectTo extends FloodMessage {
	private static final long serialVersionUID = 1L;
	public NAddress address;

	public ConnectTo(NAddress address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "ConnectTo " + address.toString();
	}
}
