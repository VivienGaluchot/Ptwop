package ptwop.p2p;

import java.io.IOException;

import ptwop.network.NAddress;
import ptwop.network.NPair;

public class P2PUser implements NPair {
	private String name;
	private NPair alias;

	public P2PUser(NPair alias) {
		this("noname", alias);
	}

	public P2PUser(String name, NPair alias) {
		this.name = name;
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + " @ " + getAddress();
	}

	@Override
	public int hashCode() {
		return alias.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NPair && getAddress().equals(((NPair) o).getAddress());
	}

	// NPair
	@Override
	public void start() {
		alias.start();
	}

	/**
	 * Should not be used to send information to this node directly This
	 * information won't be routed
	 */
	@Override
	public void send(Object o) throws IOException {
		alias.send(o);
	}

	@Override
	public NAddress getAddress() {
		return alias.getAddress();
	}

	@Override
	public void disconnect() {
		alias.disconnect();
	}

	@Override
	public int getLatency() {
		return alias.getLatency();
	}

	public NPair getTrueNPair() {
		return alias;
	}

	@Override
	public void setAlias(NPair pair) {
		throw new RuntimeException("Can't use setAlias on P2PUser object");
	}
}
