package ptwop.p2p.flood.messages;

import ptwop.network.NAddress;

public class AddToMyNeighbours extends FloodMessage {
	private static final long serialVersionUID = 1L;
	public NAddress address;

	public AddToMyNeighbours(NAddress address) {
		this.address = address;
	}
	
	public String toString() {
		return "AddToMyNeighbours " + address.toString();
	}
}
