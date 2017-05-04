package ptwop.p2p.core;

import java.io.IOException;

import ptwop.p2p.P2PUser;
import ptwop.p2p.base.ConnectTo;

public class CoreV2 extends CoreV1 {

	public CoreV2() {
		this("unamed");
	}

	public CoreV2(String myName) {
		super(myName);
	}

	@Override
	public String toString() {
		return "CoreV2 P2P";
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
