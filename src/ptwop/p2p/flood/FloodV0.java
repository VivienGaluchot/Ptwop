package ptwop.p2p.flood;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ptwop.network.NAddress;
import ptwop.network.NServent;
import ptwop.network.NPair;
import ptwop.network.NPairHandler;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.base.ConnectTo;
import ptwop.p2p.base.MessageToApp;
import ptwop.p2p.base.MyNameIs;
import ptwop.p2p.base.P2PMessage;

public class FloodV0 implements P2P, NPairHandler {

	protected NServent manager;
	protected Set<P2PUser> otherUsers;
	protected P2PHandler p2pHandler;
	protected String myName;

	public FloodV0(NServent manager, String myName) {
		this.manager = manager;
		this.myName = myName;
		otherUsers = new HashSet<>();
		manager.setHandler(this);
	}

	private void sendUserListTo(NPair user) {
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers) {
				if (!u.equals(user)) {
					try {
						user.send(new ConnectTo(u.getAddress()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "FloodV0 P2P";
	}

	// P2P Interface

	@Override
	public void start() {
		manager.start();
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		manager.connectTo(address);
	}

	@Override
	public void stop() {
		manager.disconnect();
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers) {
				u.disconnect();
			}
			otherUsers.clear();
		}
	}

	@Override
	public void broadcast(Object msg) {
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers) {
				try {
					sendTo(u, msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void anycast(Set<P2PUser> dests, Object msg) {
		for (P2PUser u : dests) {
			try {
				sendTo(u, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendTo(P2PUser dest, Object msg) throws IOException {
		dest.send(new MessageToApp(msg));
	}

	@Override
	public Set<P2PUser> getUsers() {
		return Collections.unmodifiableSet(otherUsers);
	}

	@Override
	public void setMessageHandler(P2PHandler handler) {
		p2pHandler = handler;
	}

	// NPairHandler interface

	@Override
	public void incommingConnectionFrom(NPair pair) {
		P2PUser user = new P2PUser(pair);
		pair.setAlias(user);
		pair.start();
		sendUserListTo(pair);

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

	@Override
	public void incommingMessage(NPair npair, Object o) {
		if (!(npair instanceof P2PUser))
			throw new IllegalArgumentException("Wrong user class");

		P2PUser pair = (P2PUser) npair;

		if (!(o instanceof P2PMessage))
			throw new IllegalArgumentException("Unknown message class");

		if (o instanceof MyNameIs) {
			MyNameIs m = (MyNameIs) o;
			pair.setName(m.name);
			p2pHandler.userUpdate(pair);
		} else if (o instanceof ConnectTo) {
			ConnectTo m = (ConnectTo) o;
			try {
				manager.connectTo(m.address);
			} catch (IOException e) {
				System.out.println("Impossible to connect to " + m.address + " : " + e.getMessage());
			}
		} else if (o instanceof MessageToApp) {
			MessageToApp m = (MessageToApp) o;
			p2pHandler.handleMessage(pair, m.msg);
		} else {
			System.out.println("Flood>incommingMessage : Unknown message class");
		}
	}

	@Override
	public void pairQuit(NPair npair) {
		if (!(npair instanceof P2PUser))
			throw new IllegalArgumentException("Flood>handleMessage : Wrong user class");

		P2PUser pair = (P2PUser) npair;
		synchronized (otherUsers) {
			otherUsers.remove(pair);
		}
		p2pHandler.userDisconnect(pair);
	}
}
