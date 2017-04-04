package ptwop.p2p.flood;

import java.io.IOException;

import ptwop.network.NPair;
import ptwop.network.NServent;
import ptwop.p2p.P2PUser;
import ptwop.p2p.base.ConnectTo;
import ptwop.p2p.base.MyNameIs;
import ptwop.p2p.routing.Router;

public class FloodV2 extends FloodV1 {

	public FloodV2(NServent manager, String myName, Router router) {
		super(manager, myName, router);
	}

	@Override
	public String toString() {
		return "FloodV2 P2P";
	}

	// System

	@Override
	protected void sendUserListTo(P2PUser user) {
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers) {
				try {
					if (u != user && !areNeighbours(user.getAddress(), u.getAddress())) {
						sendTo(user, new ConnectTo(u.getAddress()));
						sendTo(u, new ConnectTo(user.getAddress()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// NetworkUserHandler

	@Override
	public void incommingConnectionFrom(NPair pair) {
		P2PUser user = new P2PUser(pair);
		pair.setAlias(user);
		pair.start();
		sendUserListTo(user);

		synchronized (otherUsers) {
			otherUsers.add(user);
		}
		p2pHandler.userConnect(user);

		try {
			user.send(new MyNameIs(myName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectedTo(NPair pair) {
		incommingConnectionFrom(pair);
	}
}
