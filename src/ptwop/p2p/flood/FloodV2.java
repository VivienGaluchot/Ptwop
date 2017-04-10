package ptwop.p2p.flood;

import java.io.IOException;

import ptwop.network.NServent;
import ptwop.p2p.P2PUser;
import ptwop.p2p.base.ConnectTo;
import ptwop.p2p.routing.DumbRouter;
import ptwop.p2p.routing.Router;

public class FloodV2 extends FloodV1 {
	
	public FloodV2(NServent manager) {
		this(manager, "unamed", new DumbRouter());
	}

	public FloodV2(NServent manager, Router router) {
		this(manager, "unamed", router);
	}
	
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
		synchronized (users) {
			for (P2PUser u : users) {
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
}
