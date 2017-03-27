package ptwop.network.tcp;

import java.io.Serializable;

public class Ping implements Serializable {
	private static final long serialVersionUID = 1L;

	int x = 0;

	public Ping(int x) {
		this.x = x;
	}
}