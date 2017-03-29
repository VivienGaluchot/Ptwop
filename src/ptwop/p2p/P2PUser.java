package ptwop.p2p;

import java.io.IOException;

import ptwop.network.NAddress;
import ptwop.network.NPair;

public class P2PUser implements NPair {
	private String name;
	private NAddress address;
	private NPair npair;

	public P2PUser(String name, NPair npair) {
		this(name, null, npair);
	}

	public P2PUser(NAddress address, NPair npair) {
		this("noname", address, npair);
	}

	public P2PUser(String name, NAddress address, NPair npair) {
		this.name = name;
		this.address = address;
		this.npair = npair;
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
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof P2PUser && ((P2PUser) o).address.equals(address);
	}

	// NPair
	@Override
	public void send(Object o) throws IOException {
		npair.send(o);
	}

	@Override
	public void disconnect() {
		npair.disconnect();
	}

	@Override
	public int getLatency() {
		return npair.getLatency();
	}
}
