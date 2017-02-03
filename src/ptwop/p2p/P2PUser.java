package ptwop.p2p;

import ptwop.network.NAddress;

public class P2PUser {
	private String name;
	private NAddress adress;

	public P2PUser(String name) {
		this(name, null);
	}

	public P2PUser(NAddress adress) {
		this("noname", adress);
	}

	public P2PUser(String name, NAddress adress) {
		this.name = name;
		this.adress = adress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NAddress getAdress() {
		return adress;
	}

	public void setAdress(NAddress adress) {
		this.adress = adress;
	}

	@Override
	public String toString() {
		return name + " @ " + adress;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof P2PUser && ((P2PUser) o).adress.equals(adress);
	}
}
