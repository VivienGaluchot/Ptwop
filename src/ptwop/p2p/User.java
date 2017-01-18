package ptwop.p2p;

public class User {
	int id;

	public User(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof User && ((User) o).id == id;
	}
}
