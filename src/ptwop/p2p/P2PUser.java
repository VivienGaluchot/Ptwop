package ptwop.p2p;

import java.io.IOException;

import ptwop.network.NAddress;
import ptwop.network.NPair;

public class P2PUser {
	private String name;
	private NPair bindedNPair;

	public P2PUser(NPair bindedNPair) {
		this("noname", bindedNPair);
	}

	public P2PUser(String name, NPair bindedNPair) {
		this.name = name;
		this.bindedNPair = bindedNPair;
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
		return (bindedNPair != null) ? bindedNPair.hashCode() : 0;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof P2PUser && getAddress().equals(((P2PUser) o).getAddress());
	}

	public void sendDirectly(Object o) throws IOException {
		bindedNPair.send(o);
	}

	public NAddress getAddress() {
		return (bindedNPair != null) ? bindedNPair.getAddress() : null;
	}

	public NPair getBindedNPair() {
		return bindedNPair;
	}
}
