package ptwop.networker.model;

import ptwop.network.NAddress;

public class NetworkerNAddress extends NAddress {
	private static final long serialVersionUID = 1L;

	public int id;

	public NetworkerNAddress(Node n) {
		id = n.getId();
	}

	public NetworkerNAddress(int id) {
		this.id = id;
	}

	public String toString() {
		return "n" + id;
	}
	
	@Override
	public int hashCode(){
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NetworkerNAddress)
			return ((NetworkerNAddress) o).id == id;
		return false;
	}
}
