package ptwop.p2p;

import ptwop.network.NAddress;

public class P2PUser {
	private String name;
	private NAddress address;

	public P2PUser(String name) {
		this(name, null);
	}

	public P2PUser(NAddress address) {
		this("noname", address);
	}

	public P2PUser(String name, NAddress address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NAddress getAddress() {
		return address;
	}

	public void setAdress(NAddress address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return name + " @ " + address;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof P2PUser && ((P2PUser) o).address.equals(address);
	}
}
