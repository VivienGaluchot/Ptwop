package ptwop.p2p.core.messages;

import ptwop.network.NAddress;
import ptwop.p2p.base.P2PMessage;

public class AddToMyNeighbours extends P2PMessage {
	private static final long serialVersionUID = 1L;
	public NAddress address;

	public AddToMyNeighbours(NAddress address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "AddToMyNeighbours " + address.toString();
	}
}
