package ptwop.p2p.v0;

import ptwop.p2p.P2PUser;
import ptwop.p2p.network.Connection;

public class FloodUserConnection {
	P2PUser user;
	Connection connection;

	public FloodUserConnection(P2PUser user, Connection connection) {
		this.user = user;
		this.connection = connection;
	}
}
