package ptwop.p2p;

public class P2PUser {
	int id;

	public P2PUser(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "User " + id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof P2PUser && ((P2PUser) o).id == id;
	}
}
